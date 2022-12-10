package com.example.qq.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.R;
import com.example.qq.activity.AddPeople_Activity;
import com.example.qq.activity.PersonInfo;
import com.example.qq.activity.webview_activity;

public class TitleLayout extends LinearLayout {
    private ImageView iv_backward;
    private TextView tv_title, tv_forward;

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout bar_title = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.bar_title, this);
        iv_backward = (ImageView) bar_title.findViewById(R.id.iv_backward);
        tv_title = (TextView) bar_title.findViewById(R.id.tv_title);
        tv_forward = (TextView) bar_title.findViewById(R.id.tv_forward);
        if(!isInEditMode())
        {
            if(ActivityCollector.getCurrentActivity().getClass().equals(PersonInfo.class)){
                tv_forward.setText("保存");
                tv_title.setText("编辑资料");
            }
            if(ActivityCollector.getCurrentActivity().getClass().equals(webview_activity.class)){
                tv_forward.setText("");
                tv_title.setText("外部网页");
            }
            if(ActivityCollector.getCurrentActivity().getClass().equals(AddPeople_Activity.class))
            {
                tv_forward.setText("");
                tv_title.setText("添加好友");
            }
        }

        //设置监听器
        //如果点击back则结束活动
        iv_backward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }
    public TextView getTextView_forward(){
        return tv_forward;
    }
}