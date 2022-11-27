package com.example.qq;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.dao.ConcaterDao;
import com.example.qq.model.Concater;
import com.example.qq.model.User;

public class AddPeople extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG=AddPeople.class.getSimpleName();
    private final Activity mContext=AddPeople.this;

    private ConcaterDao concaterDao;
    private Button btn_save;
    private Button btn_back;

    public TextView tv_title;
    public EditText ed_name;
    public EditText et_phone;
    public ImageView iv_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        initUI();
        initData();
    }

    private void initUI()
    {
        btn_save=findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_back=findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        tv_title=findViewById(R.id.tv_title);
        ed_name=findViewById(R.id.ed_name);
        et_phone=findViewById(R.id.et_phone);
        iv_pic=findViewById(R.id.iv_pic);
    }
    private void initData()
    {
        concaterDao=new ConcaterDao(mContext);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.btn_save:
                //获取输入框的内容
                String name = ed_name.getText().toString();
                String phone = et_phone.getText().toString();
                if(name.trim().length()==0)
                {
                    Toast.makeText(mContext,"账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phone.trim().length()==0)
                {
                    Toast.makeText(mContext,"手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断手机号是否存在
                if(concaterDao.isExistByPhone(phone))
                {
                    Toast.makeText(mContext,"手机已经存在，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                Concater bean=new Concater(0,name,phone,null);
//                LoginActivity.list.add(bean);
                //添加到数据库中
                concaterDao.insert(bean);
                finish();
                break;
            case R.id.btn_back:
                break;
        }
    }
}