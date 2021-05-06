package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.todolist.logic.dao.User;
import com.example.todolist.logic.model.ImageLoader;

import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.util.List;

import javax.security.auth.login.LoginException;

public class ToLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button Register;
    private Button button2;
    private EditText login_count;
    private EditText login_password;
    private ImageLoader imageLoader = new ImageLoader(LitePalApplication.getContext());
    private ImageView imageView;
    private CheckBox checkBox;
    private List<User>users = DataSupport.findAll(User.class);
    private String count;
    private String password;
    private boolean isRemember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_login);

        init();
    }

    private void init() {
        Register = findViewById(R.id.btn_Register);
        button2 = findViewById(R.id.button2);
        login_count = findViewById(R.id.login_count);
        login_password = findViewById(R.id.login_password);
        imageView = findViewById(R.id.user_id);
        checkBox = findViewById(R.id.checkbox);

        button2.setOnClickListener(this);
        Register.setOnClickListener(this);
        if(users !=null){
            for(User user:users){
                isRemember = user.isRemember();
                count = user.getCount();
                password = user.getPassword();
            }
        }
        writeDown();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){

            case R.id.btn_Register:
                intent = new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.button2:
                Log.e("aaa",count+"\n"+password);
                if(login_count.getText().toString().equals(count)
                &&login_password.getText().toString().equals(password)
                &&count != null
                &&password!=null){

//                    imageLoader.bindBitmap("https://www.hualigs.cn/image/607536286a9f1.jpg",imageView);
                    if(checkBox.isChecked()){
                        isRemember = true;
                    }
                    for(User user:users){
                        user.setRemember(isRemember);
                        user.save();
                    }
                    intent = new Intent(this,HomeActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(ToLoginActivity.this,"账号或密码不正确，请重新输入",Toast.LENGTH_SHORT).show();
                }

                break;
        }


    }


    private void writeDown(){
        if(RegisterActivity.USER.FLAG == 1){
            login_count.setText(RegisterActivity.USER.user.getCount());
            login_password.setText(RegisterActivity.USER.user.getPassword());
            Glide.with(this).load("https://www.hualigs.cn/image/607536286a9f1.jpg")
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(imageView);
//            imageLoader.bindBitmap("https://www.hualigs.cn/image/607536286a9f1.jpg",imageView);
            RegisterActivity.USER.FLAG = 0;
        }

        if(isRemember){
            login_count.setText(count);
            login_password.setText(password);
            Glide.with(this).load("https://www.hualigs.cn/image/607536286a9f1.jpg")
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(imageView);
        }
    }
}