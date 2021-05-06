package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.todolist.logic.dao.Target;
import com.example.todolist.service.MusicService;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AlarmActivity extends AppCompatActivity {
    private List<Target>targets = DataSupport.findAll(Target.class);
    private String task;
    private MusicService musicService;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
            musicService = myBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        bindService(
                new Intent(this,MusicService.class),
                connection,
                Context.BIND_AUTO_CREATE
        );
        init();
        initData();

        /**
         * 传入3个参数
         *
         *
         *  flags:声明了绑定Service的一些配置
         *  BIND_AUTO_CREATE：若绑定服务时服务未启动，则会自动启动服务。注意，这种情况下服务的
         *  onStartCommand仍未被调用（它只会在显式调用startService时才会被调用）
         * */

    }

    private void init(){
        AlertDialog alertDialog =new AlertDialog.Builder(this).create();
        alertDialog.setIcon(R.drawable.clock);
        alertDialog.setTitle("到点了！！");
        alertDialog.setMessage("该"+task+"啦");
        Log.e("faffas", String.valueOf(musicService != null));
        if(musicService != null){
            musicService.playMusic();
        }
        //确定按钮
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                musicService.pauseMusic();

                Intent intent = new Intent(AlarmActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }

    private void initData(){
        if(targets!=null){
            for(Target target:targets){
                task = target.getTaskTarget();
            }
        }
    }
}