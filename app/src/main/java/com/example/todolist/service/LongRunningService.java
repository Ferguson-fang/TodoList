package com.example.todolist.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

public class LongRunningService extends Service {
    private long triggerTime;


    public void clock(long nowTime, long targetTime){
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        triggerTime = SystemClock.elapsedRealtime() + targetTime - nowTime;
        Intent i = new Intent(this, LongRunningService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pi);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBinder extends Binder{
        public LongRunningService getService(){
            return LongRunningService.this;
        }
    }




    /**
     * Intent intent3=new Intent(this,AlarmReceiver2.class);
     * 		intent3.putExtra("time", shutDownTime-nowTime+"");
     * 		PendingIntent pi3=PendingIntent.getBroadcast(this, 0, intent3,0);
     * 		//设置一个PendingIntent对象，发送广播
     * 		AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
     * 		//获取AlarmManager对象
     * 		long triggerAtTime = SystemClock.elapsedRealtime() + shutDownTime-nowTime;
     * 		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime , pi3);
     * */
}
