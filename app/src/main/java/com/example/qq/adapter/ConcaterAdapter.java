package com.example.qq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.R;
import com.example.qq.model.Concater;

import java.util.List;

public class ConcaterAdapter extends RecyclerView.Adapter<ConcaterAdapter.ConcaterViewHolder>{

    private Context context;
    private List<Concater> list;
    public ConcaterAdapter(Context context, List<Concater> list)
    {
        this.context=context;
        this.list=list;
    }
    @NonNull
    @Override
    public ConcaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConcaterViewHolder holder=new ConcaterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recview_main,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConcaterViewHolder holder, int position) {
        Concater item = list.get(position);
        holder.iv_pic.setImageResource(item.getPic()==0?R.mipmap.ic_launcher:item.getPic());
        holder.tv_name.setText(item.getName());
        holder.tv_phone.setText(item.getPhone());
        holder.tv_time.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        if (list==null) return 0;
        return list.size();
    }

    public void setList(List<Concater> list) {
        this.list = list;
    }

    class ConcaterViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_pic;
        TextView tv_name;
        TextView tv_phone;
        TextView tv_time;
        public ConcaterViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_pic=itemView.findViewById(R.id.iv_pic);
            tv_name=itemView.findViewById(R.id.tv_name);
            tv_phone=itemView.findViewById(R.id.tv_phone);
            tv_time=itemView.findViewById(R.id.tv_time);
        }
    }
}
