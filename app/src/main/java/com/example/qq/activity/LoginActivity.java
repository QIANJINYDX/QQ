package com.example.qq.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.qq.R;
import com.example.qq.dao.UserDao;
import com.example.qq.db.LoginUser;
import com.example.qq.db.model.User;
import com.example.qq.util.HttpUtil;
import com.example.qq.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Base_Activity implements View.OnClickListener{
    private String TAG="LoginActivity";
    private Button btn_login;
    private Button btn_reg;
    private Button btn_forget;

    private EditText et_account;
    private EditText et_password;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox cb_rememberPass;

    private UserDao userDao;
    private String ans;

    private final Handler uiHandler=new Handler()
    {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    Log.d(TAG, "handleMessage: "+ans);
                    if(ans.equals("账号不能为空")||ans.equals("密码不能为空")||ans.equals("账号或密码错误"))
                    {
                        Toast.makeText(LoginActivity.this,ans,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Log.d(TAG, "handleMessage: 登录成功");
                        try {
                            JSONObject jsans=new JSONObject(ans);
                            User user=new User();
                            Log.d(TAG, "handleMessage: "+jsans);
                            user.setAccount(jsans.getString("account"));
                            user.setGender(jsans.getString("gender"));
                            user.setName(jsans.getString("name"));
                            user.setPhone(jsans.getString("phone"));
                            user.setPassword(jsans.getString("password"));
                            LoginUser.getInstance().login(user);
                            editor=pref.edit();
                            if(cb_rememberPass.isChecked())
                            {
                                editor.putBoolean("remember_password",true);
                                editor.putString("account",user.getAccount());
                                editor.putString("password",user.getPassword());
                            }
                            else
                            {
                                editor.clear();
                            }
                            editor.apply();
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
    //保存所有注册用户
//    public static List<User> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref= PreferenceManager.getDefaultSharedPreferences(this);

        btn_login=findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        btn_reg=findViewById(R.id.btn_reg);
        btn_reg.setOnClickListener(this);

        btn_forget=findViewById(R.id.btn_forget);
        btn_forget.setOnClickListener(this);

        et_account=findViewById(R.id.et_account);
        et_password=findViewById(R.id.et_password);

        cb_rememberPass=findViewById(R.id.cb_rem);

        boolean isRemember=pref.getBoolean("remember_password",false);
        if(isRemember)
        {
            String account=pref.getString("account","");
            String password=pref.getString("password","");
            et_account.setText(account);
            et_password.setText(password);
            cb_rememberPass.setChecked(true);
        }
        userDao=new UserDao(LoginActivity.this);
    }
    private void login(String account,String password)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("password", password)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/login", formBody, new Callback() {
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

    @Override
    protected void onRestart() {
        super.onRestart();
//        System.out.println(list);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent intent=new Intent();
        switch (id){
            case R.id.btn_login:
                String account=et_account.getText().toString();
                String password=et_password.getText().toString();
                login(account,password);
                break;
            case R.id.btn_reg:
                intent.setClass(LoginActivity.this,RegActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_forget:
                intent.setClass(LoginActivity.this, Forget_Activity.class);
                startActivity(intent);
                break;
        }

    }
}