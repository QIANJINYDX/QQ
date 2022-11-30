package com.example.qq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.R;

public class Setting extends AppCompatActivity implements View.OnClickListener{
    private Button exit;
    private com.lqr.optionitemview.OptionItemView iv_person_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_setting);

        exit = (Button) findViewById(R.id.exit);
        iv_person_info = (com.lqr.optionitemview.OptionItemView) findViewById(R.id.iv_person_info);
        exit.setOnClickListener(this);
        iv_person_info.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //按下退出登录按钮的逻辑
            case R.id.exit:
                //结束活动并退出登录
                ActivityCollector.finishAll();
                //返回登录界面
                Intent intent = new Intent(Setting.this, LoginActivity.class);
                startActivity(intent);
                break;
            //按下个人信息框的逻辑
            case R.id.iv_person_info:
                Intent intent1 = new Intent(Setting.this, PersonInfo.class);
                startActivity(intent1);
                break;
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}