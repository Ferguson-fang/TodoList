package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.todolist.logic.dao.User;

import org.litepal.LitePal;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {



    private Button r_button;
    private EditText r_et_count;
    private EditText r_et_password;
    private boolean isRemember;

    public static class USER{
        public static User user = new User();
        public static int FLAG = 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init() {
        r_button = findViewById(R.id.r_button);
        r_et_count = findViewById(R.id.r_et_count);
        r_et_password = findViewById(R.id.r_et_password);

        r_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.r_button:
                createLitePal();
                break;
        }
    }

    public void createLitePal(){
        if(r_et_count.toString() == null || r_et_password.toString() == null){
            Toast.makeText(this,"用户名或密码为空",Toast.LENGTH_SHORT).show();
            return;
        }
        LitePal.getDatabase();

        USER.user.setCount(r_et_count.getText().toString());
        USER.user.setPassword(r_et_password.getText().toString());
        USER.user.setRemember(false);//默认不记住密码
        USER.user.save();
        USER.FLAG = 1;
        Log.e("110",r_et_count.getText().toString());
        Log.e("111","id"+USER.user.getCount());
        Log.e("220",r_et_password.getText().toString());
        Log.e("222","password"+USER.user.getPassword());
        Toast.makeText(this,"注册成功",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,ToLoginActivity.class);
        startActivity(intent);
    }
}