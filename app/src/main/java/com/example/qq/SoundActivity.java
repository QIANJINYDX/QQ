package com.example.qq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.Service.MusicService;

import java.io.File;

public class SoundActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG="SoundActivity";
    private Intent intent;
    private SeekBar mSeekBar;
    private myConn conn;
    MusicService.MyBinder binder;
    private Thread mThread;
    private TextView tv_time;
    private ObjectAnimator animator;
    private ImageView iv_disk;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what)
            {
                case 100:
                    int curretnPosition=(Integer) msg.obj;
                    mSeekBar.setProgress(curretnPosition);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);
//        path=findViewById(R.id.et_inputpath);
        findViewById(R.id.btn_paly).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_replay).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        tv_time=findViewById(R.id.tv_time);
        iv_disk=(ImageView) findViewById(R.id.disc);
        mSeekBar=(SeekBar) findViewById(R.id.music_seek_bar);
        intent=new Intent(this, MusicService.class);
        conn=new myConn();

        animator=ObjectAnimator.ofFloat(iv_disk,"rotation",0,360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);//-1表示一直转动

        bindService(intent,conn,BIND_AUTO_CREATE);

    }
    //初始化进度条的长度，获取音乐文件的长度
    private void initSeekBar()
    {
        int musicWidth=binder.getMusicWidth();
        mSeekBar.setMax(musicWidth);
    }
    private String gettime(int timem)
    {
        int fen=timem / 60000;
        int miao=(int) ((timem/60000.0-fen)*60);
        @SuppressLint("DefaultLocale") String s=String.format("%02d", fen); //25为int型
        @SuppressLint("DefaultLocale") String d=String.format("%02d", miao); //25为int型
        return s+":"+d;
    }
    //更新音乐播放的进度
    private void UpdateProgress()
    {
        mThread=new Thread(){
            @SuppressLint("SetTextI18n")
            public void run()
            {
                while (!interrupted())
                {
                    //调用服务中的获取当前播放进度
                    int currentPosition=binder.getCurrentPosition();
                    int musicLeng=binder.getMusicWidth();
                    tv_time.setText(gettime(currentPosition)+"/"+gettime(musicLeng));
                    Message message=Message.obtain();
                    message.obj=currentPosition;
                    message.what=100;
                    handler.sendMessage(message);
                }
            }
        };
        mThread.start();
    }
    private class myConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder=(MusicService.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_paly:
                binder.plays();
                animator.start();
                initSeekBar();
                UpdateProgress();
                break;
            case R.id.btn_pause:
                binder.pauses();
                animator.pause();
                break;
            case R.id.btn_replay:
                binder.replays();
                break;
            case R.id.btn_stop:
                //停止音乐之前首先要退出子线程
                mThread.interrupt();
                if(mThread.isInterrupted())
                {
                    binder.stops();
                }
                animator.pause();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //如果线程没有退出，则退出
        if(mThread!=null&!mThread.isInterrupted())
        {
            mThread.interrupt();
        }
        unbindService(conn);
        super.onDestroy();
    }
}
