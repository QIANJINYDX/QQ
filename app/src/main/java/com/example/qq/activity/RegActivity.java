package com.example.qq.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qq.R;
import com.example.qq.dao.UserDao;
import com.example.qq.db.model.User;
import com.example.qq.util.MD5;
import com.example.qq.util.PhotoUtils;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

public class RegActivity extends Base_Activity implements View.OnClickListener {
    private static String TAG="RegActivity";
    private Button btn_save;
    private Button btn_reset;
    private Button btn_back;
    private EditText et_account;
    private EditText et_password;
    private EditText et_password_cfm;
    private EditText et_phone;

    private UserDao userDao;

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
                if(name.trim().length()==0)
                {
                    Toast.makeText(RegActivity.this,"账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()==0)
                {
                    Toast.makeText(RegActivity.this,"密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(password_cfm))
                {
                    Toast.makeText(RegActivity.this,"两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phone.trim().length()==0)
                {
                    Toast.makeText(RegActivity.this,"手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                password= MD5.md5(password);
                List<User> users= LitePal.where("phone=?",phone).find(User.class);
                Toast mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
                if(!users.isEmpty())
                {
                    mToast.setText("该手机号已进行注册");
                    mToast.show();
                }
                else
                {
                    User user = new User();
                    user.setName(name);
                    user.setPassword(password);
                    //默认不记住密码，并设置默认头像
                    PhotoUtils photoUtils=new PhotoUtils();
                    byte[] hand=photoUtils.file2byte(this, "default_portrait.jpg");
                    Log.d(TAG, "onClick: "+ Arrays.toString(hand));
                    user.setRemember(0);
                    user.setPhone(phone);
                    user.setPortrait(hand);
                    user.save();
                    mToast.setText("注册成功");
                    mToast.show();
                    Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
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
//    public boolean isExistByAccountAndPhone(String account,String phone)
//    {
//        for (User item:LoginActivity.list)
//        {
//            if(item.getAccount().equals(account)||item.getPhone().equals(phone))
//            {
//                return true;
//            }
//        }
//        return false;
//    }
}