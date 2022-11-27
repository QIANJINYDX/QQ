package com.example.qq;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Chat_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Chat_list extends Fragment {
    private String newId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public Chat_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Chat_list.
     */
    // TODO: Rename and change types and number of parameters
    public static Chat_list newInstance(String param1, String param2) {
        Chat_list fragment = new Chat_list();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat_list, container, false);
        Button chat_btn=view.findViewById(R.id.chat_btn);
        Button btn_forback=view.findViewById(R.id.btn_forback);
        Button btn_add_friends=view.findViewById(R.id.btn_add_friends);
        Button btn_query_friends=view.findViewById(R.id.btn_query_friends);
        Button btn_update_friends=view.findViewById(R.id.btn_update_friends);
        Button btn_delete_friends=view.findViewById(R.id.btn_delete_friends);
        Button btn_palysound=view.findViewById(R.id.btn_palysound);
        Button btn_palysoundPoll = view.findViewById(R.id.btn_palysoundPoll);
        Button btn_playvideo = view.findViewById(R.id.btn_playvideo);
        EditText et_name=view.findViewById(R.id.et_name);
        EditText et_phone=view.findViewById(R.id.et_phone);

        HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
        SoundPool mSoundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);


        soundID.put(1, mSoundPool.load(getContext(), R.raw.dingdong, 1));

        //点击进入聊天按钮
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),chat.class);
                startActivity(intent);
            }
        });
        //点击强制下线按钮

        btn_forback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent("com.example.qq.FORCE_OFFLINE");
                getActivity().sendBroadcast(intent);
            }
        });
        //添加数据
        btn_add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse("content://com.example.qq.provider/friends");
                ContentValues values=new ContentValues();
                String name= String.valueOf(et_name.getText());
                String phone= String.valueOf(et_phone.getText());
                values.put("name",name);
                values.put("phone",phone);
                Uri newUri=getActivity().getContentResolver().insert(uri,values);
                newId=newUri.getPathSegments().get(1);
                et_name.setText("");
                et_phone.setText("");
                Toast.makeText(getActivity(),"添加成功",Toast.LENGTH_SHORT).show();
            }
        });
        //查询朋友
        btn_query_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse("content://com.example.qq.provider/friends");
                Cursor cursor=getActivity().getContentResolver().query(uri,null,null,null,null);
                if(cursor!=null)
                {
                    while (cursor.moveToNext())
                    {
                        @SuppressLint("Range") String name=cursor.getString(cursor.getColumnIndex("name"));
                        @SuppressLint("Range") String phone=cursor.getString(cursor.getColumnIndex("phone"));
                        Log.d("CHAT","朋友名字："+name);
                        Log.d("CHAT","朋友电话："+phone);
                    }
                    cursor.close();
                }
                Toast.makeText(getActivity(),"查询成功",Toast.LENGTH_SHORT).show();
            }
        });
        //更新朋友
        btn_update_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse("content://com.example.qq.provider/friends/"+newId);
                ContentValues values=new ContentValues();
                values.put("name","UpdateName");
                values.put("phone","UpdatePhone");
                getActivity().getContentResolver().update(uri,values,null,null);
                Toast.makeText(getActivity(),"更新成功",Toast.LENGTH_SHORT).show();
            }
        });
        //删除朋友
        btn_delete_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse("content://com.example.qq.provider/friends/"+newId);
                getActivity().getContentResolver().delete(uri,null,null);
                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
            }
        });
        //播放音乐界面
        btn_palysound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),SoundActivity.class);
                startActivity(intent);
            }
        });
        //播放btn_palysoundPoll
        btn_palysoundPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放SoundPool
                mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
            }
        });
        //跳转到播放视频界面
        btn_playvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),VideoActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}