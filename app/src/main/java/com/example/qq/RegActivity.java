package com.example.qq;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qq.dao.UserDao;
import com.example.qq.model.User;

public class RegActivity extends BaseActivity implements View.OnClickListener {
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

    @Override
    public void onClick(View view) {
        int id= view.getId();
        switch (id){
            case R.id.btn_save:
                //获取输入框的内容
                String account = et_account.getText().toString();
                String password = et_password.getText().toString();
                String password_cfm = et_password_cfm.getText().toString();
                String phone = et_phone.getText().toString();
                if(account.trim().length()==0)
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
                //判断账号或手机是否存在
                if(userDao.isExistByAccountAndPhone(account,phone))
                {
                    Toast.makeText(RegActivity.this,"账号或手机已经存在，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                User bean=new User(account,password,phone);
//                LoginActivity.list.add(bean);
                //添加到数据库中
                userDao.insert(bean);
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