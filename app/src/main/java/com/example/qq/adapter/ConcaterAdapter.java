package com.example.qq.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qq.R;
import com.example.qq.db.model.Concater;

import java.util.List;

public class ConcaterAdapter extends ArrayAdapter<Concater> {
    private final View.OnClickListener listener;
    //resourceID指定ListView的布局方式
    private int resourceID;
    public ConcaterAdapter(@NonNull Context context, int resource, @NonNull List<Concater> objects, View.OnClickListener listener1) {
        super(context, resource, objects);
        resourceID=resource;
        this.listener = listener1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Concater concater=getItem(position);
        @SuppressLint("ViewHolder") View view= LayoutInflater.from(getContext()).inflate(resourceID,null);
        ImageView iv_pic=view.findViewById(R.id.iv_pic);
        TextView tv_name=view.findViewById(R.id.tv_name);
        TextView tv_phone=view.findViewById(R.id.tv_phone);
        TextView tv_time=view.findViewById(R.id.tv_time);
        iv_pic.setImageResource(concater.getPic()==0?R.mipmap.ic_launcher:concater.getPic());
        tv_name.setText(concater.getName());
        tv_phone.setText(concater.getPhone());
        tv_time.setText(concater.getTime());
        return view;
    }
}
