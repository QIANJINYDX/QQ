package com.example.qq.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.R;

@SuppressLint("ViewConstructor")
public class MainTitleLayout extends LinearLayout {
    private RoundImageView riv;
    private TextView tv_title, tv_fun;
    public MainTitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LinearLayout bar_title = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.main_bar_title, this);
        riv=bar_title.findViewById(R.id.ri_portrait);
        tv_title=bar_title.findViewById(R.id.tv_title);
        tv_fun=bar_title.findViewById(R.id.tv_fun);
    }
}
