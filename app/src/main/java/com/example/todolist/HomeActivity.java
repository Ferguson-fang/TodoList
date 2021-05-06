package com.example.todolist;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.todolist.Adpter.AddDataAdapter;
import com.example.todolist.logic.dao.Target;
import com.example.todolist.ui.LetterDrawable;
import com.example.todolist.ui.PlusDrawable;
import com.example.todolist.ui.RecyclerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private RecyclerView rvAddNewView;
    private List<String> addViewed = new ArrayList<>(16);
    int i = 0;
    private AddDataAdapter addDataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        initItem();
        initToolBar();
    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.user_image);
        mDrawerLayout = findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar, 0,0);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        Glide.with(this).load("https://www.hualigs.cn/image/607536286a9f1.jpg")
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating);
        fab.setImageDrawable(new PlusDrawable());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, AddTipActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initToolBar(){
        assert toolbar != null;
        toolbar.setTitle("To do List");
        toolbar.setNavigationIcon(new LetterDrawable("F", this.getResources().getColor(R.color.letterColor), Color.WHITE));
        setSupportActionBar(toolbar);
    }

    private void initItem(){

        rvAddNewView = findViewById(R.id.rv_add_new_view);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 4);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddNewView.setLayoutManager(linearLayoutManager);
        addDataAdapter = new AddDataAdapter(addViewed);
        rvAddNewView.addItemDecoration(new RecyclerItemDecoration(20,4));
        rvAddNewView.setAdapter(addDataAdapter);


        List<Target> targets = DataSupport.findAll(Target.class);
        if(targets!=null){
            long processTime;
            long triggerTime;
            for(Target target: targets){
                addViewed.add(target.getTimeTarget()+target.getTaskTarget());
                addDataAdapter.notifyDataSetChanged();
                processTime = target.getProcess();
                //创建一个Intent对象
                Intent intent = new Intent(HomeActivity.this,AlarmActivity.class);
                //获取显示时钟的Activity
                PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this,0,intent,0);
                //获取AlarmManager对象
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP,processTime,pendingIntent);
                Log.e("aaaaaa",SystemClock.elapsedRealtime()+"\n"+processTime);
                Toast.makeText(this,"闹钟设置成功",Toast.LENGTH_SHORT).show();
                Log.e("dasdasdas","闹钟设置成功");
            }


        }
        addDataAdapter.setLongClickListenerRemove(new AddDataAdapter.LongClickListenerRemove() {
            @Override
            public void setLongClickListener(View view) {
                addViewed.remove(rvAddNewView.getChildLayoutPosition(view));
                addDataAdapter.notifyDataSetChanged();
                if(targets!=null){
                    for(Target target: targets){
                        target.delete();
                    }
                }
            }
        });

        addDataAdapter.setAddDataListener(new AddDataAdapter.AddDataListener() {
            @Override
            public void onAddDataListener(int position) {

            }
        });

    }
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mActionBarDrawerToggle.syncState();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
//    }
}