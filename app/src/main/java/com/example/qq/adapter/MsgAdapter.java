package com.example.qq.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.widget.Msg;
import com.example.qq.R;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_left;
        LinearLayout ll_right;
        LinearLayout ll_txt_left;
        LinearLayout ll_txt_right;
        ImageView iv_left;
        ImageView iv_right;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(@NonNull View view) {
            super(view);
            ll_left=view.findViewById(R.id.ll_left);
            ll_right=view.findViewById(R.id.ll_right);
            ll_txt_left=view.findViewById(R.id.left_layout);
            ll_txt_right=view.findViewById(R.id.right_layout);
            leftMsg=view.findViewById(R.id.left_msg);
            rightMsg=view.findViewById(R.id.right_msg);
            iv_left=view.findViewById(R.id.iv_left);
            iv_right=view.findViewById(R.id.iv_right);
        }
    }
    public MsgAdapter(List<Msg> msgList)
    {
        mMsgList=msgList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }
    public void setOnItemClickListener(OnItemClickListener listenser) {
        this.listener = listenser;
    }
    @Override
    public void onBindViewHolder(@NonNull MsgAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Msg msg=mMsgList.get(position);
        if(msg.getType()==Msg.TYPE_RECEIVED)
        {
            holder.ll_left.setVisibility(View.VISIBLE);
            if(msg.getStyle()==1)
            {
                //发送文本
                holder.ll_txt_left.setVisibility(View.VISIBLE);
                holder.iv_left.setVisibility(View.GONE);
            }
            else if(msg.getStyle()==2)
            {
                holder.iv_left.setImageURI(msg.getUri());
                holder.ll_txt_left.setVisibility(View.GONE);
                holder.iv_left.setVisibility(View.VISIBLE);
            }
            holder.ll_right.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        }
        else if(msg.getType()==Msg.TYPE_SENT)
        {
            holder.ll_right.setVisibility(View.VISIBLE);
            if(msg.getStyle()==1)
            {
                holder.ll_txt_right.setVisibility(View.VISIBLE);
                holder.iv_right.setVisibility(View.GONE);
            }
            else
            {
                holder.ll_txt_right.setVisibility(View.GONE);
                holder.iv_right.setImageURI(msg.getUri());
                holder.iv_right.setVisibility(View.VISIBLE);
            }
            holder.ll_left.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null)
                {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
