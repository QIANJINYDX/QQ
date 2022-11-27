package com.example.qq.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qq.db.AppSqliteHelper;
import com.example.qq.model.User;

public class UserDao {
    private AppSqliteHelper helper;
    private String tableName="user";
    public UserDao(Context context)
    {
        helper=new AppSqliteHelper(context);
    }
    //登录
    @SuppressLint("Range")
    public User login(String account, String password)
    {
        User user=null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(tableName, null, "name=? and password=?", new String[]{account, password}, null, null, null, null);
        if(c!=null &&c.getCount()>0)
        {
            if(c.moveToNext())
            {
                user=new User();
                user.setId(c.getInt(c.getColumnIndex("id")));
                user.setAccount(account);
                user.setPassword(password);
                user.setPhone(c.getString(c.getColumnIndex("phone")));
            }
        }
        return user;
    }
    public boolean isExistByAccountAndPhone(String account,String phone)
    {
        User user=null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(tableName, null, "name=? or phone=?", new String[]{account, phone}, null, null, null, null);
        if(c!=null &&c.getCount()>0)
        {
            return c.moveToNext();
        }
        return false;
    }
    //注册
    public boolean insert(User bean)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",bean.getAccount());
        contentValues.put("password",bean.getPassword());
        contentValues.put("phone",bean.getPhone());
        long insert = db.insert(tableName, null, contentValues);
        return insert>0;

    }

}
