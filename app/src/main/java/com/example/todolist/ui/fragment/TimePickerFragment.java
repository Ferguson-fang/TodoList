package com.example.todolist.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.R;


public class TimePickerFragment extends android.app.Fragment {
    private TimePicker mTimePicker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timepicker,container,false);
        mTimePicker = view.findViewById(R.id.tp);
        //设置点击时间不弹键盘
        mTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        //设置显示时间为24小时
        mTimePicker.setIs24HourView(true);
        //获取当前选择的时间
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Log.e("time", i+":"+i1);
            }
        });

        /**
         * 好像有api在，自动把当前时间设好
         * */
        //设置当前小时
        //mTimePicker.setHour(8);
        //设置当前分钟（0-59）
        //mTimePicker.setMinute(10);


        return view;
    }
}
