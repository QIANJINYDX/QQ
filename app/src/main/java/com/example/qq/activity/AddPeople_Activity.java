package com.example.qq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.Entities.AddPeople;
import com.example.qq.R;
import com.example.qq.adapter.AddPeopleAdapter;
import com.example.qq.dao.ConcaterDao;
import com.example.qq.db.LoginUser;
import com.example.qq.db.model.Concater;
import com.example.qq.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPeople_Activity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG= AddPeople_Activity.class.getSimpleName();
    private final Activity mContext= AddPeople_Activity.this;
    private AddPeopleAdapter adapter;
    private ConcaterDao concaterDao;
    private Button btn_save;
    private Button btn_back;
    private Button btn_find;
    private ListView lv_people;

    public TextView tv_title;
    public EditText ed_name;
    public EditText et_phone;
    public ImageView iv_pic;

    private List<AddPeople> addPeopleList;
    private List<AddPeople> current_addPeopleList;
    private LoginUser loginUser = LoginUser.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        loginUser=LoginUser.getInstance();
        setContentView(R.layout.activity_add_people);
        initUI();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public static String getType(Object obj){
        return obj.getClass().toString();
    }
    private void AddFriend(String number1,String number2)
    {
        RequestBody fromBody=new FormBody.Builder()
                .add("number1",number1)
                .add("number2",number2)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/addfriend", fromBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans=response.body().string();
            }
        });
    }
    private void FindPeople(String txt)
    {

        RequestBody formBody = new FormBody.Builder()
                .add("res", txt)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/addpeople", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String ans=response.body().string();
                try {
                    current_addPeopleList.clear();
                    JSONArray jsonArray=new JSONArray(ans);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        String account=jsonObject.getString("account");
                        String phone=jsonObject.getString("phone");
                        AddPeople addPeople=new AddPeople(name,phone,account);
                        current_addPeopleList.add(addPeople);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onResponse: "+current_addPeopleList);

            }
        });
    }
    private void initUI()
    {
        btn_find=findViewById(R.id.btn_find);
        btn_find.setOnClickListener(this);
        tv_title=findViewById(R.id.tv_title);
        ed_name=findViewById(R.id.ed_name);
        et_phone=findViewById(R.id.et_phone);
        iv_pic=findViewById(R.id.iv_pic);
        lv_people=findViewById(R.id.lv_people);
        lv_people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddPeople addPeople=addPeopleList.get(position);
                Toast.makeText(AddPeople_Activity.this,addPeople.getName(),Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onItemClick: 点击");
            }
        });
    }
    private void initData()
    {
        concaterDao=new ConcaterDao(mContext);
        addPeopleList=new ArrayList<>();
        current_addPeopleList=new ArrayList<>();
        adapter=new AddPeopleAdapter(AddPeople_Activity.this,R.layout.add_friend_item,addPeopleList, this);
        lv_people.setAdapter(adapter);
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.btn_save:
                //获取输入框的内容
                String name = ed_name.getText().toString();
                String phone = et_phone.getText().toString();
                if(name.trim().length()==0)
                {
                    Toast.makeText(mContext,"账号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phone.trim().length()==0)
                {
                    Toast.makeText(mContext,"手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断手机号是否存在
                if(concaterDao.isExistByPhone(phone))
                {
                    Toast.makeText(mContext,"手机已经存在，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                Concater bean=new Concater(0,name,phone,null);
//                LoginActivity.list.add(bean);
                //添加到数据库中
                concaterDao.insert(bean);
                finish();
                break;
            case R.id.btn_find:
                String res=ed_name.getText().toString();
                if (!res.equals(""))
                {
                    FindPeople(res);
                    Log.d(TAG, "onClick: "+addPeopleList);
                    //五秒钟后执行run方法
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //这里不能直接修改UI，需要使用runOnUiThread
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    addPeopleList.clear();
                                    addPeopleList.addAll(current_addPeopleList);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    };
                    timer.schedule(task, 1000);
                }
                break;
            case R.id.btn_add:
                final int  position= (int) v.getTag();
                AddPeople addPeople=addPeopleList.get(position);
                String number1=loginUser.getAccount();
                String number2=addPeople.getNumber();
                Log.d(TAG, "账号1："+number1+"账号2："+number2);
                //向服务器发送POST添加
                AddFriend(number1,number2);
                Toast.makeText(AddPeople_Activity.this,"添加成功",Toast.LENGTH_LONG).show();
        }
    }
}