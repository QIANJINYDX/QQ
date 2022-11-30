package com.example.qq.activity;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.Receiver.ForceOfflineReceiver;

/*
创建BaseActivity作为所有活动的父类
 */
public class Base_Activity extends AppCompatActivity {
    private ForceOfflineReceiver receiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.qq.FORCE_OFFLINE");
        receiver=new ForceOfflineReceiver();
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver!=null)
        {
            unregisterReceiver(receiver);
            receiver=null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
