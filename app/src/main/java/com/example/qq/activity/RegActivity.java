package com.example.qq.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qq.R;
import com.example.qq.dao.UserDao;
import com.example.qq.db.model.User;
import com.example.qq.util.HttpUtil;
import com.example.qq.util.MD5;
import com.example.qq.util.PhotoUtils;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegActivity extends Base_Activity implements View.OnClickListener {
    private static String TAG="RegActivity";
    private Button btn_save;
    private Button btn_reset;
    private Button btn_back;
    private EditText et_account;
    private EditText et_password;
    private EditText et_password_cfm;
    private EditText et_phone;

    private String ans;
    private UserDao userDao;

    private final Handler uiHandler=new Handler()
    {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    Toast.makeText(RegActivity.this,ans,Toast.LENGTH_LONG).show();
                    Log.d(TAG, "handleMessage: "+ans);
                    if(ans.equals("注册成功"))
                    {
                        Log.d(TAG, "handleMessage: "+ans);
                        Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        btn_save=findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

        btn_reset=findViewById(R.id.btn_reset);
        btn_save.setOnClickListener(this);

        btn_back=findViewById(R.id.btn_logout);
        btn_back.setOnClickListener(this);

        et_account=findViewById(R.id.et_account);
        et_password=findViewById(R.id.et_password);
        et_password_cfm=findViewById(R.id.et_password_cfm);
        et_phone=findViewById(R.id.et_phone);

        userDao=new UserDao(RegActivity.this);
    }

    private void register(String name,String password,String password_cfm,String phone)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("password_cfm", password_cfm)
                .add("phone", phone)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/register", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ans=response.body().string();
                Log.d(TAG, "onResponse: "+ans);
                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id= view.getId();
        switch (id){
            case R.id.btn_save:
                //获取输入框的内容
                String name = et_account.getText().toString();
                String password = et_password.getText().toString();
                String password_cfm = et_password_cfm.getText().toString();
                String phone = et_phone.getText().toString();
                register(name,password,password_cfm,phone);
                break;
            case R.id.btn_reset:
                et_account.setText("");
                et_password.setText("");
                et_password_cfm.setText("");
                et_phone.setText("");
                break;
            case R.id.btn_logout:
                break;
        }
    }
}