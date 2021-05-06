package com.example.todolist.ui.fragment;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.AddTipActivity;
import com.example.todolist.Adpter.AddDataAdapter;
import com.example.todolist.AlarmActivity;
import com.example.todolist.HomeActivity;
import com.example.todolist.R;
import com.example.todolist.logic.dao.Target;
import com.example.todolist.service.LongRunningService;
import com.example.todolist.ui.RecyclerItemDecoration;

import org.litepal.LitePalApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AddTipFragment extends android.app.Fragment {
    //InputMethodManager对输入法的控制
    private InputMethodManager imm;
    private EditText editText;
    //private ClickListener mListener;

    private RecyclerView rvAddNewView;
    private List<String> addViewed = new ArrayList<>(16);
    int i = 0;
    private AddDataAdapter addDataAdapter;
    private EditText et;
    private TextView tv;
    private TimePicker mTimePicker;

    private View view;
    //获取目标时间
    private String mTimeTarget;
    private long mTimeTargetSecond;
    //获取目标任务
    private String mTaskTarget;
    private long timeTarget;

//    List<Target>targets;

    Target target = new Target();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_tip, container, false);
        initItem();
        initTp();

        return view;
    }

    private void initItem() {
        rvAddNewView = view.findViewById(R.id.rv_add_new_view);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(LitePalApplication.getContext(), 4);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddNewView.setLayoutManager(linearLayoutManager);
        addDataAdapter = new AddDataAdapter(addViewed);
        rvAddNewView.addItemDecoration(new RecyclerItemDecoration(20, 4));
        rvAddNewView.setAdapter(addDataAdapter);

        addDataAdapter.setLongClickListenerRemove(new AddDataAdapter.LongClickListenerRemove() {
            @Override
            public void setLongClickListener(View view) {
                addViewed.remove(rvAddNewView.getChildLayoutPosition(view));
                addDataAdapter.notifyDataSetChanged();
                target.delete();
            }
        });

        et = view.findViewById(R.id.et);
        tv = view.findViewById(R.id.tv);

        /**
         * 空指针的原因，对应的id都不在activity_add_tip里，都在fragment里
         * ！！！
         * */
        addDataAdapter.setAddDataListener(new AddDataAdapter.AddDataListener() {
            @Override
            public void onAddDataListener(int position) {
                if (et.getText().toString() == null) {
                    Toast.makeText(getActivity(), "关键字为空", Toast.LENGTH_SHORT);
                } else {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("task", et.getText().toString());
                    mTaskTarget = et.getText().toString();
                    target.setTaskTarget(mTaskTarget);
                    target.setTimeTarget(mTimeTarget);
                    target.setProcess(timeTarget);
                    target.save();

                    i++;
                    addViewed.add(mTimeTarget+" "+mTaskTarget);
                    addDataAdapter.notifyDataSetChanged();


                }

            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTp(){

        mTimePicker = view.findViewById(R.id.tp);
        //设置点击时间不弹键盘
        mTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        //设置显示时间为24小时
        mTimePicker.setIs24HourView(true);
        //获取当前选择的时间

        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                mTimeTarget = i+"时"+i1+"分";

                Calendar calendar = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
//                calendar.set(Calendar.HOUR_OF_DAY,mTimePicker.getCurrentHour());
//                calendar.set(Calendar.MINUTE,mTimePicker.getCurrentMinute());
//                mTimeTargetSecond = calendar.getTimeInMillis();
                calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),mTimePicker.getCurrentHour(),mTimePicker.getCurrentMinute());
                timeTarget = SystemClock.elapsedRealtime()+calendar.getTimeInMillis() - now.getTimeInMillis();

            }
        });

        /**
         * 好像有api在，自动把当前时间设好
         * */
        //设置当前小时
        //mTimePicker.setHour(8);
        //设置当前分钟（0-59）
        //mTimePicker.setMinute(10);
    }
}
