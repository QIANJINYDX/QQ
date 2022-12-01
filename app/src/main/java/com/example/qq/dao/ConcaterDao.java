package com.example.qq.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qq.db.AppSqliteHelper;
import com.example.qq.db.model.Concater;
import com.example.qq.db.model.User;

import java.util.ArrayList;
import java.util.List;

public class ConcaterDao {
    private AppSqliteHelper helper;
    private String tableName="friends";
    public ConcaterDao(Context context)
    {
        helper=new AppSqliteHelper(context);
    }

    @SuppressLint("Range")
    public List<Concater> queryAll()
    {
        List<Concater> list=null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Concater bean=null;
        Cursor c=db.query(tableName,null,null,null,null,null,null,null);
        if(c!=null&&c.getCount()>0)
        {
            list=new ArrayList<>();
            while (c.moveToNext())
            {
                bean=new Concater();
                bean.setId(c.getInt(c.getColumnIndex("id")));
                bean.setPhone(c.getString(c.getColumnIndex("phone")));
                bean.setName(c.getString(c.getColumnIndex("name")));
                bean.setPic(c.getInt(c.getColumnIndex("pic")));
                bean.setTime("2022-11-19");
                list.add(bean);
            }
        }


        return list;
    }
    public boolean isExistByPhone(String phone)
    {
        User user=null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(tableName, null, "phone=?", new String[]{phone}, null, null, null, null);
        if(c!=null &&c.getCount()>0)
        {
            return c.moveToNext();
        }
        return false;
    }
    public boolean insert(Concater bean)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("name",bean.getName());
        contentValues.put("pic",bean.getPic());
        contentValues.put("phone",bean.getPhone());
        long insert = db.insert(tableName, null, contentValues);
        return insert>0;

    }
}
