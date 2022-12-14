package com.example.qq.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.R;
import com.example.qq.activity.AddPeople_Activity;
import com.example.qq.widget.TitleLayout;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Directory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Directory extends Fragment implements View.OnClickListener {
    //UI定义
    View rootView;
    TitleLayout tl_title;
    ImageView iv_backward;
    TextView tv_title, tv_forward;
    //变量定义
    String TAG="通讯录页面";
    public Directory() {
        // Required empty public constructor
    }

    public static Directory newInstance() {
        Directory fragment = new Directory();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(rootView==null)
        {
            rootView=inflater.inflate(R.layout.fragment_blank, container, false);
        }
        initView();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.toolbar,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.addpeople:
                Toast.makeText(getActivity(),"添加好友",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        tl_title=rootView.findViewById(R.id.tl_title);
        iv_backward = (ImageView) tl_title.findViewById(R.id.iv_backward);
        tv_title = (TextView) tl_title.findViewById(R.id.tv_title);
        tv_forward = (TextView) tl_title.findViewById(R.id.tv_forward);
        iv_backward.setVisibility(View.INVISIBLE);
        tv_title.setText("通讯录");
        tv_forward.setText("+");
        tv_forward.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.tv_forward:
                Log.d(TAG, "点击扩展");
                showPopupMenu(tv_forward);
        }
    }
    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.toolbar, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getTitle().toString().equals("添加好友"))
                {
                    Intent intent=new Intent(getActivity(), AddPeople_Activity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Log.d(TAG, "关闭PopupMenu");
            }
        });

        popupMenu.show();
    }
}