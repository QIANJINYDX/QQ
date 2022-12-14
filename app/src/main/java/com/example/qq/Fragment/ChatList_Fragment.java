package com.example.qq.Fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qq.activity.Info_Activity;
import com.example.qq.R;
import com.example.qq.activity.SoundActivity;
import com.example.qq.activity.VideoActivity;
import com.example.qq.activity.Chat_Activity;
import com.example.qq.activity.test_Activity;
import com.example.qq.activity.webview_activity;
import com.example.qq.db.DBUtils;
import com.example.qq.db.LoginUser;
import com.example.qq.util.PhotoUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatList_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatList_Fragment extends Fragment {
    private String newId;
    private PhotoUtils photoUtils = new PhotoUtils();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public ChatList_Fragment() {
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
    public static ChatList_Fragment newInstance(String param1, String param2) {
        ChatList_Fragment fragment = new ChatList_Fragment();
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
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelId, channelName, importance);
        }
        NotificationManager notificationManager  = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    //创建一个message通道，名字为消息


    @Override
    public void onResume() {
        super.onResume();

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
        Button btn_Peopleinfo=view.findViewById(R.id.btn_Peopleinfo);
        Button btn_SendNotify=view.findViewById(R.id.btn_SendNotify);
        Button btn_TestMySQL=view.findViewById(R.id.btn_TestMySQL);
        Button btn_WebView=view.findViewById(R.id.btn_WebView);
        EditText et_name=view.findViewById(R.id.et_name);
        EditText et_phone=view.findViewById(R.id.et_phone);


        HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
        SoundPool mSoundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);


        soundID.put(1, mSoundPool.load(getContext(), R.raw.dingdong, 1));

        //点击进入聊天按钮
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), Chat_Activity.class);
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
                Intent intent=new Intent(getActivity(), SoundActivity.class);
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
                Intent intent=new Intent(getActivity(), VideoActivity.class);
                startActivity(intent);
            }
        });
        //进入个人信息页面
        btn_Peopleinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), Info_Activity.class);
                startActivity(intent);
            }
        });
        //测试发送通知

        btn_SendNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser loginUser = LoginUser.getInstance();
                createNotificationChannel("message", "消息", NotificationManager.IMPORTANCE_HIGH);
                Log.d("CHAT", "onClick: "+loginUser.toString());
                sendNotification(loginUser.getName(),"聊天内容",loginUser);
            }
        });

        //测试数据库
        btn_TestMySQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), test_Activity.class);
                startActivity(intent);
            }
        });

        //测试WebView
        btn_WebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),webview_activity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    private void sendNotification(String title, String content,LoginUser loginUser) {
        Intent intent = new Intent(getActivity(), ChatList_Fragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "message");
        builder
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("提示消息")
                .setWhen(System.currentTimeMillis())
                .setLargeIcon( photoUtils.byte2bitmap(loginUser.getPortrait()))
                .setContentTitle(title)
                .setContentText(content);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
    private void SendMessage(String url, final String userName, String passWord) throws IOException {
        // Android 4.0 之后不能在主线程中请求HTTP请求
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBuilder = new FormBody.Builder();
                Request request = new Request.Builder().url(url).build();
                Response response= null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Log.d("CHAT", response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}