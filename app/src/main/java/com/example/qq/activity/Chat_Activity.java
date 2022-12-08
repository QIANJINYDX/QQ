package com.example.qq.activity;

import static com.example.qq.util.JudgeURL.judge;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.adapter.OnItemClickListener;
import com.example.qq.db.LoginUser;
import com.example.qq.util.HttpUtil;
import com.example.qq.widget.Msg;
import com.example.qq.adapter.MsgAdapter;
import com.example.qq.R;
import com.example.qq.db.model.ChatInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Chat_Activity extends Base_Activity {
    private String TAG="聊天页面";
    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String friend_account;
    private LoginUser loginUser = LoginUser.getInstance();
    private final Handler uiHandler=new Handler()
    {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActivityCollector.addActivity(this);
        loginUser=LoginUser.getInstance();
        friend_account=getIntent().getStringExtra("account");
        LinearLayout title=findViewById(R.id.tl_title);
        TextView name=title.findViewById(R.id.tv_title);
        name.setText(getIntent().getStringExtra("name"));
        initMsgs();
        TextView info=title.findViewById(R.id.tv_forward);
        info.setText("···");
        inputText=(EditText) findViewById(R.id.input_text);
        send=(Button) findViewById(R.id.send);
        msgRecyclerView=(RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "消息被点击了"+position);
                //获取消息内容，判断消息中是否包含网页
                String connect=msgList.get(position).getContent();
                if(judge(connect))
                {
                    Intent intent=new Intent(Chat_Activity.this,webview_activity.class);
                    intent.putExtra("url",connect);
                    startActivity(intent);
                }

            }
        });
        msgRecyclerView.setAdapter(adapter);

        //获取聊天数据
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString();
                if(!"".equals(content))
                {
                    //添加入数据库
                    SendMsg(loginUser.getAccount(),friend_account,content,"1");
                    //将消息对象显示在页面
                    Msg msg=new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);//当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size()-1);//将ListView定位到最后一行
                    inputText.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    private void getMsg(String my_account,String friend_account)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("my_account", my_account)
                .add("friend_account", friend_account)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/getMsg", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans=response.body().string();
                try {
                    JSONArray jsonans=new JSONArray(ans);
                    for (int i = 0; i < jsonans.length(); i++) {
                        JSONObject jsonObject=jsonans.getJSONObject(i);
                        int msgType=0;
                        if(friend_account.equals(jsonObject.getString("send_number")))
                        {
                            msgType=1;
                        }
                        Msg msg=new Msg(jsonObject.getString("content"),msgType);
                        msgList.add(msg);
                    }
                    Message message=new Message();
                    message.what=1;
                    uiHandler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void SendMsg(String send_number,String accept_number,String content,String msg_type)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("send_number", send_number)
                .add("msg_type", msg_type)
                .add("accept_number", accept_number)
                .add("content", content)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/sendMsg", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans=response.body().string();
                Log.d(TAG, "发送数据："+ans);
            }
        });
    }
    private void initMsgs() {
        getMsg(friend_account,loginUser.getAccount());
    }
}