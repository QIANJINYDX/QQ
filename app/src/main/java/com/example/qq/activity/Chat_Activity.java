package com.example.qq.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.qq.widget.Msg;
import com.example.qq.adapter.MsgAdapter;
import com.example.qq.R;
import com.example.qq.db.model.ChatInfo;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class Chat_Activity extends Base_Activity {
    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        insertMsgs();
        initMsgs();
        inputText=(EditText) findViewById(R.id.input_text);
        send=(Button) findViewById(R.id.send);
        msgRecyclerView=(RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        Date date=new Date();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=inputText.getText().toString();
                if(!"".equals(content))
                {
                    //添加入数据库
                    Date date = new Date(System.currentTimeMillis());
                    ChatInfo chat=new ChatInfo();
                    chat.setMsg(content);
                    chat.setSend_time(date);
                    chat.setTpye(1);
                    chat.save();

                    Msg msg=new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);//当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size()-1);//将ListView定位到最后一行
                    inputText.setText("");
                }
            }
        });
    }
    private void insertMsgs()
    {
        ChatInfo chat=new ChatInfo();
        chat.setMsg("你好 叶东欣！");
        chat.setTpye(0);
        chat.save();

        ChatInfo chat1=new ChatInfo();
        chat1.setMsg("你好，你是谁呀？");
        chat1.setTpye(1);
        chat1.save();

        ChatInfo chat2=new ChatInfo();
        chat2.setMsg("我是小明，很高兴和你聊天");
        chat2.setTpye(0);
        chat2.save();
    }
    private void initMsgs() {
        List<ChatInfo> chatList= LitePal.findAll(ChatInfo.class);
        for (ChatInfo chat : chatList) {
            Msg msg=new Msg(chat.getMsg(),chat.getTpye());
            msgList.add(msg);
        }
    }
}