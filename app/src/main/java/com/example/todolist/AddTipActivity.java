package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todolist.Adpter.AddDataAdapter;
import com.example.todolist.Context.MyApplication;
import com.example.todolist.ui.RecyclerItemDecoration;
import com.example.todolist.ui.fragment.AddTipFragment;
import com.example.todolist.ui.fragment.TimePickerFragment;

import org.litepal.LitePalApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddTipActivity extends AppCompatActivity /*implements AddTipFragment.ClickListener*/{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tip);

        initCancle();
        setDefaultFragment();
    }



    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        AddTipFragment fragment = new AddTipFragment();
        transaction.replace(R.id.id_content, fragment);
        transaction.commit();
    }


    /**
     * 将空白处设置为TextView
     * 点击空白处，触发点击事件，finish当前Activity
     * 实现返回
     * */
    public void initCancle() {
        View view = findViewById(R.id.addtip_cancle);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击finish Activity，实现退回home页面
                //AddTipActivity.this.finish();

                /**要刷新UI所以用跳转重新创建HomeActivity*/
                Intent intent = new Intent(AddTipActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}