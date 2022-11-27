package com.example.qq.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.qq.R;

import java.io.IOException;

public class MusicService extends Service {
    private static final String TAG="MusicService";
    public MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder{
        //播放音乐
        public void plays()
        {
            play();
        }
        //暂停播放
        public void pauses()
        {
            pause();
        }
        //重新播放
        public void replays()
        {
            repaly();
        }
        //停止播放
        public void stops()
        {
            stop();
        }
        //获取当前播放进度
        public int getCurrentPosition()
        {
            return getCurrentProgress();
        }
        //获取音乐文件传唱度
        public int getMusicWidth()
        {
            return getMusicLength();
        }

    }
    //获取音乐长度
    private int getMusicLength() {
        if(mediaPlayer!=null)
        {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    //当前播放位置
    private int getCurrentProgress() {
        if(mediaPlayer!=null&&mediaPlayer.isPlaying())
        {
            Log.i(TAG,"获取当前进度");
            return mediaPlayer.getCurrentPosition();
        }
        else {

            if(mediaPlayer!=null&&(!mediaPlayer.isPlaying()))
            {
                return mediaPlayer.getCurrentPosition();
            }
        }
        return 0;
    }
    //重新播放音乐
    private void repaly() {
        if (mediaPlayer!=null)
        {
            Log.i(TAG,"重新开始播放");
            mediaPlayer.seekTo(0);
            try {
                mediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }
    //停止播放音乐
    private void stop() {
        if(mediaPlayer!=null)
        {
            Log.i(TAG,"停止播放");
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        else
        {
            Toast.makeText(getApplicationContext(),"已停止",Toast.LENGTH_SHORT).show();
        }
    }
    //暂停播放音乐
    private void pause() {
        if(mediaPlayer!=null &&mediaPlayer.isPlaying())
        {
            Log.i(TAG,"播放暂停");
            mediaPlayer.pause();
        }
        else if(mediaPlayer!=null&&(!mediaPlayer.isPlaying()))
        {
            mediaPlayer.start();
        }
    }
    //开始播放音乐
    private void play() {
        if(mediaPlayer==null)
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.write);
            mediaPlayer.start();
        }
        else {
            int position=getCurrentProgress();
            mediaPlayer.seekTo(position);
            try {
                mediaPlayer.prepare();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            mediaPlayer.start();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }
}
