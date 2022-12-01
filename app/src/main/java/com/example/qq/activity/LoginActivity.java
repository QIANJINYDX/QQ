package com.example.qq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qq.R;
import com.example.qq.dao.UserDao;
import com.example.qq.db.LoginUser;
import com.example.qq.db.model.User;
import com.example.qq.util.MD5;

import org.litepal.LitePal;

public class LoginActivity extends Base_Activity implements View.OnClickListener{
    private Button btn_login;
    private Button btn_reg;
    private Button btn_forget;

    private EditText et_account;
    private EditText et_password;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox cb_rememberPass;

    private UserDao userDao;
    //保存所有注册用户
//    public static List<User> list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
//        list.add(new User("ydx","123456","13142485710"));

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

    @Override
    protected void onRestart() {
        super.onRestart();
//        System.out.println(list);
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        Intent intent=new Intent();
        switch (id){
            case R.id.btn_login:
                String account=et_account.getText().toString();
                String password=et_password.getText().toString();
                //判断数据合法性
                if(account.trim().length()==0)
                {
                    Toast.makeText(LoginActivity.this,"账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()==0)
                {
                    Toast.makeText(LoginActivity.this,"密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = LitePal.where("name=?",account).findFirst(User.class);

                if(user==null)
                {
                    //登录失败
                    Toast.makeText(LoginActivity.this,"账号或密码错误", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //登录成功,跳转到首页
                    password= MD5.md5(password);
                    if(user.checkPassword(password))
                    {
                        user.update(user.getId());
                        //用户登入，存入LoginUser
                        LoginUser.getInstance().login(user);
                    }
                    //保存密码功能
                    editor=pref.edit();
                    if(cb_rememberPass.isChecked())
                    {
                        editor.putBoolean("remember_password",true);
                        editor.putString("account",account);
                        editor.putString("password",password);
                    }
                    else
                    {
                        editor.clear();
                    }
                    editor.apply();

                    //跳转首页
                    intent.setClass(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
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