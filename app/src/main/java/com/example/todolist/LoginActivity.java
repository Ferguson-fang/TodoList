package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton imageButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        imageButton2 = findViewById(R.id.imageButton2);

        imageButton2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.imageButton2:
                intent = new Intent(this,ToLoginActivity.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        startActivity(intent);

    }
}