package com.example.qq.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.R;
import com.example.qq.util.HttpUtil;
import com.example.qq.widget.TitleLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class webview_activity extends AppCompatActivity {
    private static String TAG="网页页面";
    private TitleLayout titleLayout;
    private TextView tv_title;
    private String title="";
    private final Handler uiHandler=new Handler()
    {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    tv_title.setText(title);
                    break;
            }
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_webview);
        WebView webView=(WebView) findViewById(R.id.web_view);
        titleLayout=findViewById(R.id.tl_title);
        tv_title=titleLayout.findViewById(R.id.tv_title);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(getIntent().getStringExtra("url"));
        getConnect(getIntent().getStringExtra("url"));
    }
    private void getConnect(String url)
    {
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans=response.body().string();
                title=getTitle(ans);
                Log.d(TAG, ans);
                Log.d(TAG, title);
                Message message=new Message();
                message.what=1;
                uiHandler.sendMessage(message);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    public String getTitle(String s)
    {
        String regex;
        String title = "";
        final List<String> list = new ArrayList<>();
        regex = "<title>.*?</title>";
        final Pattern pa = Pattern.compile(regex);
        final Matcher ma = pa.matcher(s);
        while (ma.find())
        {
            list.add(ma.group());
        }
        for (int i = 0; i < list.size(); i++)
        {
            title = title + list.get(i);
        }
        return outTag(title);
    }
    public String outTag(final String s)
    {
        return s.replaceAll("<.*?>", "");
    }
}