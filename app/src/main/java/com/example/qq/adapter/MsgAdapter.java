package com.example.qq.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qq.util.RecyclerViewOnItemLongClickListener;
import com.example.qq.widget.Msg;
import com.example.qq.R;

import java.util.List;
import java.util.Objects;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>implements View.OnLongClickListener,View.OnClickListener {
    private List<Msg> mMsgList;
    private OnItemClickListener listener;

    @Override
    public boolean onLongClick(View v) {
        //回调长按事件
        ViewHolder holder = (ViewHolder) v.getTag(R.id.view_tag);
        if (listener != null) {
            this.listener.onItemLongClick(v, holder.getAdapterPosition());
        }
        return true;
    }
    @Override
    public void onClick(View view) {
        //回调点击事件
        ViewHolder holder = (ViewHolder) view.getTag(R.id.view_tag);
        if (listener != null) {
            this.listener.onItemClick(view, holder.getAdapterPosition());
        }
    }
    //自定义点击接口类
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_right_expend;
        LinearLayout ll_left_expend;
        LinearLayout ll_left;
        LinearLayout ll_right;
        LinearLayout ll_txt_left;
        LinearLayout ll_txt_right;
        //左文件
        LinearLayout ll_left_wenjian;
        ImageView ll_left_wenjian_img;
        TextView ll_left_wenjian_txt;
        //右文件
        LinearLayout ll_right_wenjian;
        ImageView ll_right_wenjian_img;
        TextView ll_right_wenjian_txt;

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
            ll_left_wenjian=view.findViewById(R.id.ll_left_wenjian);
            ll_left_wenjian_img=view.findViewById(R.id.ll_left_wenjian_img);
            ll_left_wenjian_txt=view.findViewById(R.id.ll_left_wenjian_txt);
            ll_right_wenjian=view.findViewById(R.id.ll_right_wenjian);
            ll_right_wenjian_img=view.findViewById(R.id.ll_right_wenjian_img);
            ll_right_wenjian_txt=view.findViewById(R.id.ll_right_wenjian_txt);
            ll_right_expend=view.findViewById(R.id.ll_right_expend);
            ll_left_expend=view.findViewById(R.id.ll_left_expend);
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
        ViewHolder holder=new ViewHolder(view);
        view.setTag(R.id.view_tag,holder);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
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
                holder.ll_txt_left.setVisibility(View.VISIBLE);//文本可见
                holder.iv_left.setVisibility(View.GONE);//图片不可见
                holder.ll_left_wenjian.setVisibility(View.GONE);//文件不可见
            }
            else if(msg.getStyle()==2)
            {
                holder.iv_left.setImageURI(msg.getUri());//设置图片
                holder.ll_txt_left.setVisibility(View.GONE);//文本不可见
                holder.iv_left.setVisibility(View.VISIBLE);//图片可见
                holder.ll_left_wenjian.setVisibility(View.GONE);//文件不可见
            }
            else if(msg.getStyle()==3)
            {
                holder.ll_left_wenjian_txt.setText(msg.getContent());
                holder.ll_left_wenjian_img.setImageBitmap(msg.getBitmap());
                holder.ll_left_wenjian.setVisibility(View.VISIBLE);//文件可见
                holder.ll_txt_left.setVisibility(View.GONE);//文本不可见
                holder.iv_left.setVisibility(View.GONE);//图片不可见
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
                holder.ll_right_wenjian.setVisibility(View.GONE);//文件不可见
            }
            else if(msg.getStyle()==2)
            {
                holder.iv_right.setImageURI(msg.getUri());
                holder.ll_txt_right.setVisibility(View.GONE);
                holder.iv_right.setVisibility(View.VISIBLE);
                holder.ll_right_wenjian.setVisibility(View.GONE);//文件不可见
            }
            else if(msg.getStyle()==3)
            {
                holder.ll_right_wenjian_txt.setText(msg.getContent());
                holder.ll_right_wenjian_img.setImageBitmap(msg.getBitmap());
                holder.ll_right_wenjian.setVisibility(View.VISIBLE);//文件可见
                holder.ll_txt_right.setVisibility(View.GONE);//文本不可见
                holder.iv_right.setVisibility(View.GONE);//图片不可见
            }
            holder.ll_left.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
