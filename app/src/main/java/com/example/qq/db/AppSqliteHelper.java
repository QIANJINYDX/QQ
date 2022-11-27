package com.example.qq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AppSqliteHelper extends SQLiteOpenHelper {
    public AppSqliteHelper(@Nullable Context context)
    {
        //创建数据库
        super(context, "qq.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        //创建表
        sqLiteDatabase.execSQL("create table user(id Integer primary key autoincrement,name varchar(40),password varchar(40),phone varchar(11))");
        sqLiteDatabase.execSQL("create table friends(id Integer primary key autoincrement,name varchar(40),pic Integer,phone varchar(11))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        //修改表
    }
}
