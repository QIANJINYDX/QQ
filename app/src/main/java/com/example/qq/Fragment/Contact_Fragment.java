package com.example.qq.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qq.R;
import com.example.qq.activity.AddPeople_Activity;
import com.example.qq.activity.Chat_Activity;
import com.example.qq.adapter.ConcaterAdapter;
import com.example.qq.dao.ConcaterDao;
import com.example.qq.db.LoginUser;
import com.example.qq.db.model.Concater;
import com.example.qq.util.HttpUtil;
import com.example.qq.widget.RoundImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Contact_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contact_Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG= Contact_Fragment.class.getSimpleName();
    private final Activity mContext=getActivity();
    private ListView rec;
    private ConcaterAdapter adapter;
    private ConcaterDao concaterDao;
    private List<Concater> friends;
    private RoundImageView riv;
    private TextView tv_title, tv_fun;
    private LoginUser loginUser = LoginUser.getInstance();
    public Contact_Fragment() {
        // Required empty public constructor
    }
    private final Handler uiHandler=new Handler()
    {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    //结束查找朋友
                    Log.d(TAG, "Friends"+friends);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public static Contact_Fragment newInstance() {
        Contact_Fragment fragment = new Contact_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        friends.clear();
        FindFriend(loginUser.getAccount());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void FindFriend(String number)
    {
        RequestBody formBody = new FormBody.Builder()
                .add("number", number)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/findfriend", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String ans=response.body().string();
                    Log.d(TAG, "ans:"+ans);
                    JSONArray jsonArray=new JSONArray(ans);
                    Log.d(TAG, "JsonArray"+jsonArray);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        Concater concater=new Concater();
                        concater.setName(jsonObject.getString("name"));
                        concater.setNumber(jsonObject.getString("account"));
                        concater.setPhone(jsonObject.getString("phone"));
                        concater.setPic(0);
                        friends.add(concater);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    uiHandler.sendMessage(msg);
                } catch (JSONException e) {
                    Log.d(TAG, "请求体报错");
                    e.printStackTrace();
                }
                catch (Exception ignored)
                {
                    Log.d(TAG, "客户端请求失败！重新请求！");
                    FindFriend(loginUser.getAccount());
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        friends=new ArrayList<>();
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_qq_contact, container, false);
        LinearLayout maintitle=view.findViewById(R.id.mtl_title);
        tv_title=maintitle.findViewById(R.id.tv_title);
        tv_fun=maintitle.findViewById(R.id.tv_fun);
        tv_title.setText("联系人");
        tv_fun.setText("添加");
        tv_fun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),AddPeople_Activity.class);
                startActivity(intent);
            }
        });
        concaterDao=new ConcaterDao(getActivity());
        adapter= new ConcaterAdapter(requireActivity(),R.layout.item_recview_main,friends,this);


        rec=view.findViewById(R.id.rec);
        rec.setAdapter(adapter);
        loginUser=LoginUser.getInstance();

        // 绑定点击事件

        rec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Concater concater=friends.get(position);
                Intent intent =new Intent(getActivity(), Chat_Activity.class);
                intent.putExtra("account",concater.getNumber());
                intent.putExtra("name",concater.getName());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}