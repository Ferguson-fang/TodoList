package com.example.todolist.logic.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageLoader {
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;// 50MB
    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private boolean mIsDiskLruCacheCreated = false;//用来标记mDiskLruCache是否创建成功
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT+ 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 10;
    private final int DISK_CACHE_INDEX = 0;

    private static final int MESSAGE_POST_RESULT = 101;

    private ImageResizer imageResizer = new ImageResizer();

    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"ImageLoader#"+mCount.getAndIncrement());
        }
    };
    /**
     * 创建线程池
     */
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),mThreadFactory
    );

    /**
     * 创建Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_POST_RESULT){
                LoaderResult loadResult = (LoaderResult) msg.obj;
                ImageView iv = loadResult.iv;
                String url = (String) iv.getTag();
                if (url.equals(loadResult.uri)){//防止加载列表形式时，滑动复用的错位
                    iv.setImageBitmap(loadResult.bitmap);
                }
            }
        }
    };

    public ImageLoader(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        init();
    }
    /**
     * 创建一个ImageLoader
     */
    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }


    /**
     * 初始化
     * LruCache<String,Bitmap> mMemoryCache
     * DiskLruCache mDiskLruCache
     */
    private void init() {
        // LruCache<String,Bitmap> mMemoryCache
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                return bitmap.getByteCount() / 1024;
            }
        };
        // DiskLruCache mDiskLruCache
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  加载原始大小的图
     */
    public  void bindBitmap(String uri,ImageView iv){
        bindBitmap(uri,iv,0,0);
    }

    /**
     * 异步加载网络图片 指定大小
     */
    public void bindBitmap(final String uri, final ImageView iv, final int targetWidth, final int targetHeight){
        iv.setTag(uri);
        Bitmap bitmap = loadBitmapFormMemCache(uri);
        if (bitmap != null){
            iv.setImageBitmap(bitmap);
            return;
        }
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(uri,targetWidth,targetHeight);
                if (bitmap != null){
                    LoaderResult result = new LoaderResult(iv,uri,bitmap);
                    Message message = mHandler.obtainMessage();
                    message.obj = result;
                    message.what = MESSAGE_POST_RESULT;
                    mHandler.sendMessage(message);
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    /**
     * 同步加载网络图片
     */
    private Bitmap loadBitmap(String url, int targetWidth, int targetHeight) {
        Bitmap bitmap = loadBitmapFormMemCache(url);
        if (bitmap != null) {
            return bitmap;
        }
        try {
            bitmap = loadBitmapFromDiskCache(url, targetWidth, targetHeight);
            if (bitmap != null) {
                return bitmap;
            }
            bitmap = loadBitmapFromHttp(url, targetWidth, targetHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null && !mIsDiskLruCacheCreated) {//缓存文件夹创建失败
            bitmap = downLoadFromUrl(url);
        }
        return bitmap;
    }

    /**
     * 向缓存中添加Bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 通过key拿到bitmap
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private Bitmap loadBitmapFormMemCache(String url) {
        final String key = hashKeyFromUrl(url);
        return getBitmapFromMemoryCache(key);
    }

    /**
     * 从网络进行请求
     */
    private Bitmap loadBitmapFromHttp(String url, int targetWidth, int targetHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("UI 线程不能进行网络访问");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        String key = hashKeyFromUrl(url);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downLoadUrlToStream(url, outputStream)) {
                editor.commit();
            } else {
                editor.abort();//重复操作
            }
            mDiskLruCache.flush();//刷新
        }
        return loadBitmapFromDiskCache(url, targetWidth, targetHeight);
    }

    /**
     * 从硬盘缓存中读取Bitmap
     */
    private Bitmap loadBitmapFromDiskCache(String url, int targetWidth, int targetHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("硬盘读取Bitmap在UI线程，UI 线程不进行耗时操作");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        String key = hashKeyFromUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fis = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fis.getFD();
            bitmap = imageResizer.decodeSampledBitmapFromFileDescriptor(fileDescriptor, targetWidth, targetHeight);
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 将数据请求到流之中
     */
    private boolean downLoadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            bis = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bos = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = bis.read()) != -1) {
                bos.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            closeIn(bis);
            closeOut(bos);
        }
        return false;
    }

    /**
     * 直接通过网络请求图片 也不做任何的缩放处理
     */
    private Bitmap downLoadFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream bis = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            bis = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            closeIn(bis);
        }
        return bitmap;
    }


    /**
     * 得到MD5值key
     */
    private String hashKeyFromUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = byteToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    /**
     * 将byte转换成16进制字符串
     */
    private String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);//得到十六进制字符串
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    /**
     * 得到缓存文件夹
     */
    private File getDiskCacheDir(Context mContext, String uniqueName) {
        //判断储存卡是否可以用
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = mContext.getExternalCacheDir().getPath();//储存卡
        } else {
            cachePath = mContext.getCacheDir().getPath();//手机自身内存
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 得到可用空间大小
     */
    private long getUsableSpace(File file) {
        return file.getUsableSpace();
    }

    /**
     * jdk1.6以后所有流都实现了Closeable方法，所以closeIn()和closeOut()是多余的
     * */

    /**
     * 关闭输入流
     */
    private void closeIn(BufferedInputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                in = null;
            }
        }
    }

    /**
     * 关闭输输出流
     */
    private void closeOut(BufferedOutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                out = null;
            }
        }
    }

    private static class LoaderResult {
        public ImageView iv ;
        public String uri;
        public Bitmap bitmap;

        public LoaderResult(ImageView iv, String uri, Bitmap bitmap) {
            this.iv = iv;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }
}
