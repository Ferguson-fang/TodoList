 package com.example.todolist.logic.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

 public class ImageResizer {
     private static final String TAG = "ImageResizer";

     public ImageResizer() {
     }

     public Bitmap decodeSampledBitmapFromResource(
             Resources res,int resId,int reqWidth, int reqHeight){
         // First decode with inJustDecodeBounds = true check dimensions
         //将BitmapFactory.Options的inJustDecodeBounds参数设为true并加载图片
         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeResource(res,resId,options);

         // Calculate inSampleSize
         options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

         // Decode bitmap with inSampleSize set
         options.inJustDecodeBounds = false;

         return BitmapFactory.decodeResource(res,resId,options);
     }

     public Bitmap decodeSampledBitmapFromFileDescriptor(
             FileDescriptor fd,int reqWidth,int reqHeight){
         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeFileDescriptor(fd,null,options);

         options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

         options.inJustDecodeBounds = false;
         return BitmapFactory.decodeFileDescriptor(fd,null,options);
     }

     public static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWith,int reqHeight){
         if(reqHeight == 0 || reqWith == 0){
             return 1;
         }
         // Raw height and Width of image
         //从BitmapFactory.Options中取出图片的原始宽高信息，他们对应于outWidth和outHeight参数
         final int height = options.outHeight;
         final int width = options.outWidth;
         int inSampleSize = 1;

         if(height > reqHeight || width > reqWith){
             final int halfHeight = height / 2;
             final int halfWidth = width / 2;
             //Calculate the largest inSampleSize value that is a power of 2 and keeps both
             //height and width larger than the requested height and width.
             while((halfHeight / inSampleSize) >= reqHeight
                     &&(halfWidth / inSampleSize) >= reqWith){
                 inSampleSize *= 2;
             }
         }
         return inSampleSize;
     }
 }
