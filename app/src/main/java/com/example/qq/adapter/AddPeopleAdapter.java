package com.example.qq.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.qq.Entities.AddPeople;
import com.example.qq.R;

import java.util.List;

public class AddPeopleAdapter extends ArrayAdapter<AddPeople> {
    private final View.OnClickListener listener;
    //resourceID指定ListView的布局方式
    private int resourceID;
    public AddPeopleAdapter(@NonNull Context context, int resource, @NonNull List<AddPeople> objects, View.OnClickListener listener) {
        super(context, resource, objects);
        resourceID=resource;
        this.listener = listener;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取当前Browser实例
        AddPeople addPeople = getItem(position);
        //使用LayoutInfater为子项加载传入的布局
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceID,null);
        TextView tv_name = (TextView)view.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView)view.findViewById(R.id.tv_phone);
        TextView tv_number=(TextView) view.findViewById(R.id.tv_number);
        Button btn_add=(Button) view.findViewById(R.id.btn_add);
        //引入Browser对象的属性值
        tv_name.setText("昵称："+addPeople.getName());
        tv_phone.setText("手机号："+addPeople.getPhone());
        tv_number.setText("账号："+addPeople.getNumber());
        btn_add.setOnClickListener(listener);
        btn_add.setTag(position);
        return view;
    }
}
