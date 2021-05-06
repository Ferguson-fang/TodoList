package com.example.todolist.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.todolist.R;


public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化音乐播放器
        mediaPlayer = MediaPlayer.create(this, R.raw.fooling_mode);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public void playMusic(){
            mediaPlayer.start();//播放
    }

    public void pauseMusic(){
        mediaPlayer.pause();
    }


    public class MyBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放mediaPlayer
        mediaPlayer.release();
    }
}
