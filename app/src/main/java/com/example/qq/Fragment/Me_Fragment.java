package com.example.qq.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qq.R;
import com.example.qq.activity.PersonInfo;
import com.example.qq.activity.Setting;
import com.example.qq.db.LoginUser;
import com.example.qq.util.PhotoUtils;
import com.example.qq.widget.RoundImageView;

import java.io.InputStream;

public class Me_Fragment extends Fragment implements View.OnClickListener{
    private ImageView setting;
    private LinearLayout info;
    private TextView info_name,info_account;
    private RoundImageView portrait;
    private LoginUser loginUser = LoginUser.getInstance();
    private final PhotoUtils photoUtils = new PhotoUtils();
    public Me_Fragment() {
        // Required empty public constructor
    }
    public static Me_Fragment newInstance() {
        Me_Fragment fragment = new Me_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loginUser.reinit();
        initInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_me, container, false);
        setting = (ImageView)view.findViewById(R.id.setting);
        info = (LinearLayout)view.findViewById(R.id.info);
        info_name = (TextView)view.findViewById(R.id.info_name);
        portrait = (RoundImageView)view.findViewById(R.id.portrait);

        info.setOnClickListener((View.OnClickListener) this);
        setting.setOnClickListener((View.OnClickListener) this);
        //登录则初始化用户的信息
        initInfo();

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //点击设置按钮的逻辑
            case R.id.setting:
                Intent intent = new Intent(getActivity(), Setting.class);
                startActivity(intent);
                break;
            case R.id.info:
                Intent intent1 = new Intent(getActivity(), PersonInfo.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
    private void initInfo(){
        info_name.setText(loginUser.getName());
        if(loginUser.getPortrait()==null)
        {
            @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.mipmap.default_header);
            portrait.setImageBitmap(BitmapFactory.decodeStream(is));
        }
        else
        {
            portrait.setImageBitmap(photoUtils.byte2bitmap(loginUser.getPortrait()));
        }
    }
}