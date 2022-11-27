package com.example.qq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, SeekBar.OnSeekBarChangeListener {
    private SurfaceView sv;
    private SurfaceHolder holder;
    private MediaPlayer mediaPlayer;
    private int position;
    private RelativeLayout rl;
    private Timer timer;
    private TimerTask task;
    private SeekBar sbar;
    private ImageView play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        sbar=findViewById(R.id.sbar);
        play=findViewById(R.id.play);
        sbar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
        sv=findViewById(R.id.sv);
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer!=null&&mediaPlayer.isPlaying())
                {
                    int progress=mediaPlayer.getCurrentPosition();
                    int total=mediaPlayer.getDuration();
                    sbar.setMax(total);
                    sbar.setProgress(progress);
                }
            }
        };
        timer.schedule(task,500,500);
        rl=findViewById(R.id.rl);
        holder=sv.getHolder();
        holder.addCallback((SurfaceHolder.Callback) this);
    }
    //屏幕触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(rl.getVisibility()== View.INVISIBLE)
                {
                    rl.setVisibility(View.VISIBLE);
                    //倒计时3秒
                    CountDownTimer cdt=new CountDownTimer(3000,1000) {
                        @Override
                        public void onTick(long l) {
                            System.out.println(l);
                        }

                        @Override
                        public void onFinish() {
                            rl.setVisibility(View.INVISIBLE);
                        }
                    };
                    cdt.start();
                }
                else if(rl.getVisibility()==View.VISIBLE)
                {
                    rl.setVisibility(View.INVISIBLE);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        task.cancel();
        timer=null;
        task=null;
        super.onDestroy();
    }
    public void click(View view)
    {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            play.setImageResource(android.R.drawable.ic_media_play);
        }
        else if(mediaPlayer!=null)
        {
            mediaPlayer.start();
            play.setImageResource(android.R.drawable.ic_media_pause);
        }
    }
    //创建完成时触发
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mediaPlayer=MediaPlayer.create(VideoActivity.this,R.raw.video);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(holder);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                if(position>0)
                {
                    mediaPlayer.seekTo(position);
                }
            }
        });
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        position=mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
    }
    //进度发生变化时触发
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }
    //进度条开始拖动时触发
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    //进度条停止拖动时触发
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int position=seekBar.getProgress();
        if(mediaPlayer!=null&&mediaPlayer.isPlaying())
        {
            mediaPlayer.seekTo(position);
        }
    }
}