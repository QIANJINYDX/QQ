package com.example.qq.activity;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.example.qq.Application.MyApplication.RESULT_CODE_SEND_FILE;
import static com.example.qq.Application.MyApplication.RESULT_CODE_SEND_IMAGE_PAISHE;
import static com.example.qq.Application.MyApplication.RESULT_CODE_SEND_IMAGE_XIANGCE;
import static com.example.qq.Application.MyApplication.RESULT_CODE_SEND_LOCATION;
import static com.example.qq.util.JudgeURL.judge;
import static com.example.qq.widget.Msg.STYLE_IMG;
import static com.example.qq.widget.Msg.STYLE_TXT;
import static com.example.qq.widget.Msg.STYLE_WEIZHI;
import static com.example.qq.widget.Msg.TYPE_SENT;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.Service.DownloadService;
import com.example.qq.adapter.OnItemClickListener;
import com.example.qq.db.LoginUser;
import com.example.qq.util.DownloadUtil;
import com.example.qq.util.FileUpload;
import com.example.qq.util.HttpCallbackListener;
import com.example.qq.util.HttpDownFileUtils;
import com.example.qq.util.HttpUtil;
import com.example.qq.util.ImageUpload;
import com.example.qq.util.KeyboardChangeListener;
import com.example.qq.util.PhotoUtils;
import com.example.qq.util.RecyclerViewOnItemLongClickListener;
import com.example.qq.util.StorageType;
import com.example.qq.util.StorageUtil;
import com.example.qq.util.StringUtil;
import com.example.qq.widget.Msg;
import com.example.qq.adapter.MsgAdapter;
import com.example.qq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.jpush.im.android.api.content.LocationContent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;


public class Chat_Activity extends Base_Activity implements View.OnClickListener{
    //????????????
    private String TAG="????????????";
    private List<Msg> msgList=new ArrayList<>();
    private MsgAdapter adapter;
    private String friend_account;
    private LoginUser loginUser = LoginUser.getInstance();
    private static final int TAKE_PHOTO = 1;
    private static final int FROM_ALBUMS = 2;
    public static final String JPG = ".jpg";
    private Uri imageUri;  //?????????????????????
    private PhotoUtils photoUtils = new PhotoUtils();
    private String imagePath;  //????????????????????????
    private String file_path;
    private int position;
    //UI??????
    private EditText inputText;
    private ImageView iv_send;
    private RecyclerView msgRecyclerView;
    private ImageView iv_expand;
    private ImageView iv_Expressions;
    private LinearLayout ll_expend;
    private LinearLayout ll_xiangce;
    private LinearLayout ll_wenjian;
    private LinearLayout ll_paishe;
    private LinearLayout ll_weizhi;
    private TextView tv_send;
    private TextView tv_cancel;
    // ????????????????????????????????????AndroidManifest.xml????????????Android 6??????????????????????????????
    // ??????????????????
    private boolean xiangce;
    private boolean wenjian;
    private boolean paishe;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    List<String> mPermissionList = new ArrayList<>();
    private File f;
    //????????????
    private DownloadService.DownloadBinder downloadBinder;
    private Intent intent;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder= (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
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
                    for (int i = 0; i < msgList.size(); i++) {
                        Msg cur_msg=msgList.get(i);
                        if(cur_msg.getStyle()==2)
                        {
                            //????????????
                            Log.d(TAG, "handleMessage: ??????????????????");
                            String urlname="";
                            char[] img_path=cur_msg.getImg_path().toCharArray();
                            for (int j = img_path.length-1; j>=0; j--) {
                                if(img_path[j]!='/')
                                {
                                    urlname=img_path[j]+urlname;
                                }
                                else
                                {
                                    break;
                                }
                            }
                            Log.d(TAG, "ImgName:"+urlname);
                            String url="http://116.62.110.5:5000/download/"+urlname;
//                            Log.d(TAG, "onDownloadSuccess: ????????????");
//                            Log.d(TAG, "onDownloadSuccess: ????????????");
                            int finalI1 = i;
                            HttpDownFileUtils.getInstance().downFileFromServiceToPublicDir(url,Chat_Activity.this,DIRECTORY_PICTURES,((status, object, proGress, currentDownProGress, totalProGress) -> {
                                if(status==1)
                                {
                                    Log.d(TAG, "????????????: ????????????");
                                }
                                if (object instanceof File){
                                    File file = (File) object;
                                }else if (object instanceof Uri){
                                    Uri uri = (Uri) object;
                                    cur_msg.setUri(uri);
                                    msgList.set(finalI1,cur_msg);
                                    Message message=new Message();
                                    message.what=2;
                                    uiHandler.sendMessage(message);
                                }
                            }));
                        }
                        else if(cur_msg.getStyle()==3)
                        {
                            //????????????

                            String urlname="";
                            char[] file_path=cur_msg.getFile_path().toCharArray();
                            for (int j = file_path.length-1; j>=0; j--) {
                                if(file_path[j]!='/')
                                {
                                    urlname=file_path[j]+urlname;
                                }
                                else
                                {
                                    break;
                                }
                            }
                            Log.d(TAG, "FileName:"+urlname);
                            String url="http://116.62.110.5:5000/download_file/"+urlname;
                            int finalI1 = i;
                            Log.d(TAG, "??????????????????");
                            HttpDownFileUtils.getInstance().downFileFromServiceToPublicDir(url,Chat_Activity.this,DIRECTORY_DOWNLOADS,((status, object, proGress, currentDownProGress, totalProGress) -> {
                                if(status==1)
                                {
                                    Log.d(TAG, "??????????????????");
                                }
                                if (object instanceof File){
                                    File file = (File) object;
                                    Log.d(TAG, "handleMessage: "+file.getAbsolutePath());}
                                else if(object instanceof Uri)
                                {
                                    Uri uri=(Uri) object;
                                    cur_msg.setUri(uri);
                                    msgList.set(finalI1,cur_msg);
                                    Log.d(TAG, "MSGURI: "+msgList.get(finalI1).getUri());
                                    Message message=new Message();
                                    message.what=2;
                                    uiHandler.sendMessage(message);
                                }
                            }));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
                    break;
                case 2:
                    adapter.notifyDataSetChanged();
                    msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActivityCollector.addActivity(this);
        //????????????
        intent=new Intent(this,DownloadService.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            startForegroundService(intent);
        }
        else
        {
            startService(intent);
        }

        if(ContextCompat.checkSelfPermission ( Chat_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions ( Chat_Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        bindService(intent,connection,BIND_ABOVE_CLIENT);
        loginUser=LoginUser.getInstance();
        xiangce=false;
        wenjian=false;
        paishe=false;
        friend_account=getIntent().getStringExtra("account");
        LinearLayout title=findViewById(R.id.tl_title);
        TextView name=title.findViewById(R.id.tv_title);
        name.setText(getIntent().getStringExtra("name"));
        initMsgs();
        TextView info=title.findViewById(R.id.tv_forward);
        info.setText("??????");
        inputText=(EditText) findViewById(R.id.input_text);
        iv_expand=findViewById(R.id.iv_expand);
        iv_Expressions=findViewById(R.id.iv_Expressions);
        ll_expend=findViewById(R.id.ll_expend);

        ll_xiangce=findViewById(R.id.ll_xiangce);
        ll_wenjian=findViewById(R.id.ll_wenjian);
        ll_paishe=findViewById(R.id.ll_paishe);
        ll_weizhi=findViewById(R.id.ll_weizhi);
        iv_send=findViewById(R.id.iv_send);
        ll_expend.setVisibility(View.GONE);
        msgRecyclerView=(RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        //item????????????
        adapter.setOnItemClickListener(new MsgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "??????????????????"+msgList.get(position).toString());
                //??????????????????????????????????????????????????????
                //?????????????????????
                if (msgList.get(position).getStyle()==STYLE_TXT)
                {
                    String connect=msgList.get(position).getContent();
                    if(judge(connect))
                    {
                        Intent intent=new Intent(Chat_Activity.this,webview_activity.class);
                        intent.putExtra("url",connect);
                        startActivity(intent);
                    }
                }
                else if(msgList.get(position).getStyle()==STYLE_WEIZHI)
                {
                    //????????????????????????
                    Intent intent = new Intent(Chat_Activity.this, MapPickerActivity.class);
                    intent.putExtra("latitude", msgList.get(position).getLatitude());
                    intent.putExtra("longitude", msgList.get(position).getLongitude());
                    intent.putExtra("locDesc", msgList.get(position).getContent());
                    intent.putExtra("sendLocation", false);
                    Chat_Activity.this.startActivity(intent);
                }
            }
            @Override
            public void onItemLongClick(View view, int position) {
                int msgtype=msgList.get(position).getType();
                if(msgtype==1)
                {
                    //???????????????
                    LinearLayout ll_expend=view.findViewById(R.id.ll_right_expend);
                    ll_expend.setVisibility(View.VISIBLE);
                    TextView down=ll_expend.findViewById(R.id.tv_right_down);//??????
                    if(msgList.get(position).getStyle()==1)
                    {
                        down.setVisibility(View.GONE);
                    }
                    TextView delete=ll_expend.findViewById(R.id.tv_right_delete);//??????
                    TextView fenxiang=ll_expend.findViewById(R.id.tv_right_fenxiang);//??????
                    TextView quxiao=ll_expend.findViewById(R.id.tv_right_quxiao);//??????
                    quxiao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ll_expend.setVisibility(View.GONE);
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(View v) {
                            ll_expend.setVisibility(View.GONE);
                            Log.d(TAG, "????????????");
                            msgList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    assert down != null;
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "????????????");
                            Msg cur_msg=msgList.get(position);
                            if(cur_msg.getStyle()==2) {
                                //????????????
                                Log.d(TAG, "handleMessage: ??????????????????");
                                String urlname = "";
                                char[] img_path = cur_msg.getImg_path().toCharArray();
                                for (int j = img_path.length - 1; j >= 0; j--) {
                                    if (img_path[j] != '/') {
                                        urlname = img_path[j] + urlname;
                                    } else {
                                        break;
                                    }
                                }
                                Log.d(TAG, "ImgName:" + urlname);
                                String url = "http://116.62.110.5:5000/download/" + urlname;
                                downloadBinder.startDownload(url);
                            }
                            else
                            {
                                String urlname="";
                                char[] file_path=cur_msg.getFile_path().toCharArray();
                                for (int j = file_path.length-1; j>=0; j--) {
                                    if(file_path[j]!='/')
                                    {
                                        urlname=file_path[j]+urlname;
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                Log.d(TAG, "FileName:"+urlname);
                                String url="http://116.62.110.5:5000/download_file/"+urlname;
                                downloadBinder.startDownload(url);
                            }
                        }
                    });
                }
                else
                {
                    //???????????????
                    LinearLayout ll_expend=view.findViewById(R.id.ll_left_expend);
                    ll_expend.setVisibility(View.VISIBLE);
                    TextView down=ll_expend.findViewById(R.id.tv_left_down);//??????
                    if(msgList.get(position).getStyle()==1)
                    {
                        down.setVisibility(View.GONE);
                    }
                    TextView delete=ll_expend.findViewById(R.id.tv_left_delete);//??????
                    TextView fenxiang=ll_expend.findViewById(R.id.tv_left_fenxiang);//??????
                    TextView quxiao=ll_expend.findViewById(R.id.tv_left_quxiao);//??????
                    quxiao.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ll_expend.setVisibility(View.GONE);
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(View v) {
                            ll_expend.setVisibility(View.GONE);
                            Log.d(TAG, "????????????");
                            msgList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    assert down != null;
                    down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "????????????");
                        }
                    });
                }
            }
        });

        msgRecyclerView.setAdapter(adapter);
        msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
        iv_send.setOnClickListener(this);
        iv_expand.setOnClickListener(this);

        ll_xiangce.setOnClickListener(this);
        ll_wenjian.setOnClickListener(this);
        ll_paishe.setOnClickListener(this);
        ll_weizhi.setOnClickListener(this);

        KeyboardChangeListener mKeyboardChangeListener = new KeyboardChangeListener(this);
        mKeyboardChangeListener.setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                if (isShow) {
                    Log.d(TAG, "onKeyboardChange: ????????????");
                    ll_expend.setVisibility(View.GONE);
                    iv_expand.setVisibility(View.GONE);
                    iv_send.setVisibility(View.VISIBLE);
                }
                else
                {
                    Log.d(TAG, "onKeyboardChange: ????????????");
                    iv_expand.setVisibility(View.VISIBLE);
                    iv_send.setVisibility(View.GONE);
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(Chat_Activity.this,"?????????????????????????????????",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onContextItemSelected:??????");
        Msg msg=this.msgList.get(position);
        Log.d(TAG, "onContextItemSelected: ??????"+position);
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        unbindService(connection);
    }
    private void getMsg(String my_account,String friend_account) {
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
                try {
                    String ans=response.body().string();
                    JSONArray jsonans=new JSONArray(ans);
                    Log.d(TAG, "onResponse: "+jsonans);
                    for (int i = 0; i < jsonans.length(); i++) {
                        JSONObject jsonObject=jsonans.getJSONObject(i);
                        String style=jsonObject.getString("style");
                        int msgType=0;
                        if(friend_account.equals(jsonObject.getString("send_number")))
                        {
                            msgType=1;
                        }
                        Msg msg=new Msg(jsonObject.getString("content"),msgType);
                        if(style.equals("2"))
                        {
                            msg.setImg_path(jsonObject.getString("image"));
                        }
                        else if (style.equals("3"))
                        {
                            msg.setFile_path(jsonObject.getString("file"));
                            msg.setContent(jsonObject.getString("file_name"));
                            if(Objects.equals(getfilestyle(msg.getContent()), "xlsx"))
                            {
                                msg.setBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.excel));
                            }
                        }
                        else if(style.equals("4"))
                        {
                            msg.setStyle(STYLE_WEIZHI);
                            msg.setLongitude(jsonObject.getDouble("longitude"));
                            msg.setLatitude(jsonObject.getDouble("latitude"));
                            msg.setContent(jsonObject.getString("address"));
                        }
                        msg.setStyle(Integer.parseInt(style));
                        msgList.add(msg);
                    }
                    Message message=new Message();
                    message.what=1;
                    uiHandler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception ignored)
                {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(3000);//??????3???
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getMsg(friend_account,loginUser.getAccount());
                        }
                    }.start();
                }

            }
        });
    }
    private void SendMsg(String send_number, String accept_number, String content,String style) {
        RequestBody formBody = new FormBody.Builder()
                .add("send_number", send_number)
                .add("style", style)
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
                Log.d(TAG, "???????????????"+ans);
            }
        });
    }
    private void initMsgs() {
        getMsg(friend_account,loginUser.getAccount());
    }
    //????????????????????????
    public boolean fileIsExists(String strFile) {
        try {
            f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /*
     * ????????????
     */
    private void showDialog(Context context, Bitmap bitmap){
        Dialog dia = new Dialog(context, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.dialog);
        ImageView imageView = (ImageView) dia.findViewById(R.id.ivdialog);
        tv_send=dia.findViewById(R.id.tv_send);
        tv_cancel=dia.findViewById(R.id.tv_cancel);
        tv_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                dia.cancel();
                Log.d(TAG, "onClick: "+"????????????");
                Msg msg=new Msg();
                msg.setStyle(STYLE_IMG);
                msg.setBitmap(bitmap);
                Log.d(TAG, "onClick: "+bitmap);
                msg.setType(Msg.TYPE_SENT);
                msgList.add(msg);
                adapter.notifyDataSetChanged();
                msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
                //???????????????????????????????????????
                mPermissionList.clear();
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(Chat_Activity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                        mPermissionList.add(permission);
                    }
                }
                if(mPermissionList.isEmpty())
                {
                    boolean fileExist=fileIsExists(imagePath);
                    if(fileExist)
                    {
                        Log.d(TAG, "onClick: ????????????");
                        try {
                            ImageUpload.run(f,loginUser.getAccount(),friend_account,"2");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.cancel();
            }
        });
        //??????set??????????????????
        imageView.setImageBitmap(bitmap);
        dia.show();
        //??????true?????????????????????????????????dialog????????????false??????????????????
        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        dia.onWindowAttributesChanged(lp);
    }
    private String getfilestyle(String name)
    {
        return name.split("\\.")[1];
    }
    private void showDialog_file(Context context, String file_name,File file) {
        Dialog dia = new Dialog(context, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.dialog_file);
        ImageView iv_fileimg = (ImageView) dia.findViewById(R.id.iv_fileimg);
        TextView tv_filename=dia.findViewById(R.id.tv_filename);
        tv_send=dia.findViewById(R.id.tv_send);
        tv_cancel=dia.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.cancel();
            }
        });
        String type=getfilestyle(file_name);
        tv_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                dia.cancel();
                //?????????????????????
                Msg msg=new Msg();
                msg.setStyle(3);
                msg.setFile(file);
                msg.setType(Msg.TYPE_SENT);
                msg.setContent(file_name);
                if(Objects.equals(type, "xlsx"))
                {
                    msg.setBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.excel));
                }
                msgList.add(msg);
                adapter.notifyDataSetChanged();
                msgRecyclerView.scrollToPosition(adapter.getItemCount()-1);
                //??????????????????
                //??????????????????
                try {
                    Log.d(TAG, "?????????????????????"+file_name);
                        FileUpload.run(file,loginUser.getAccount(),friend_account,"3",type,file_name);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        tv_filename.setText(file_name);
        if(Objects.equals(type, "xlsx"))
        {
            iv_fileimg.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.excel));
        }
        dia.show();
        //??????true?????????????????????????????????dialog????????????false??????????????????
        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        dia.onWindowAttributesChanged(lp);


    }
    //????????????????????????
    @SuppressLint({"Range", "NotifyDataSetChanged"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        Log.d(TAG, "requestCode"+requestCode);
        Log.d(TAG, "resultCode"+resultCode);
        if(data==null)
        {
            Toast.makeText(Chat_Activity.this,"????????????",Toast.LENGTH_SHORT).show();
            return;
        }
        if(requestCode==RESULT_CODE_SEND_LOCATION)
        {
            //????????????
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            int mapview = data.getIntExtra("mapview", 0);
            String address = data.getStringExtra("address");
            String path = data.getStringExtra("path");
            Msg msg=new Msg();
            msg.setLatitude(latitude);
            msg.setLongitude(longitude);
            msg.setContent(address);
            msg.setFile_path(path);
            msg.setStyle(STYLE_WEIZHI);
            msg.setType(TYPE_SENT);
            msgList.add(msg);
            adapter.notifyDataSetChanged();
            //?????????????????????????????????.
            SendMsg_weizhi(loginUser.getAccount(),friend_account,latitude,longitude,address);

        }
        else if (requestCode==RESULT_CODE_SEND_IMAGE_PAISHE)
        {
            Log.d(TAG, "??????????????????");
            if(resultCode == RESULT_OK){
                try {
                    //??????????????????????????????????????????
                    imageUri=data.getData();
                    Bitmap bitmap = BitmapFactory.decodeStream((getContentResolver().openInputStream(imageUri)));
                    showDialog(this,bitmap);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode==RESULT_CODE_SEND_IMAGE_XIANGCE)
        {
            Log.d(TAG, "??????????????????");
            int y=1;
            if(y==1)
            {
                //????????????1
                if(resultCode == RESULT_OK){
                    //?????????????????????
                    imagePath =  photoUtils.handleImageOnKitKat(this, data);
                }
                if(imagePath != null){
                    //??????????????????????????????????????????
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    showDialog(this,bitmap);
                }else{
                    Log.d("QQ","??????????????????");
                }
            }
            else if(y==2)
            {

            }

        }
        else if(requestCode==RESULT_CODE_SEND_FILE)
        {
            Log.d(TAG, "????????????");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File res=uriToFileApiQ(this,data.getData());
                Log.d(TAG, "File:"+res.getName());
                showDialog_file(this,res.getName(),res);
            }
            Log.d(TAG, "??????????????????");
        }
    }

    private void SendMsg_weizhi(String account, String friend_account, double latitude, double longitude, String address) {
        RequestBody formBody = new FormBody.Builder()
                .add("send_number", account)
                .add("style", String.valueOf(STYLE_WEIZHI))
                .add("accept_number", friend_account)
                .add("latitude", String.valueOf(latitude))
                .add("longitude", String.valueOf(longitude))
                .add("address", address)
                .build();
        HttpUtil.sendOkHttpRequestPost("http://116.62.110.5:5000/SendMsg_weizhi", formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ans=response.body().string();
                Log.d(TAG, "???????????????????????????");
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id=v.getId();
        xiangce=false;
        wenjian=false;
        paishe=false;
        switch (id)
        {
            case R.id.iv_send:
                String content=inputText.getText().toString();
                if(!"".equals(content))
                {
                    //??????????????????
                    SendMsg(loginUser.getAccount(),friend_account,content, String.valueOf(1));
                    //??????????????????????????????
                    Msg msg=new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);//???????????????????????????ListView????????????
                    msgRecyclerView.scrollToPosition(msgList.size()-1);//???ListView?????????????????????
                    inputText.setText("");
                }
                break;
            case R.id.iv_expand:
                Log.d(TAG, "onClick: ????????????");
                if(ll_expend.getVisibility()== View.VISIBLE)
                {
                    ll_expend.setVisibility(View.GONE);
                }
                else
                {
                    ll_expend.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_xiangce:
                int y=2;
                Log.d(TAG, "????????????");
                if(y==1)
                {
                    //????????????1
                    //????????????
                    if(ContextCompat.checkSelfPermission(Chat_Activity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Chat_Activity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else {
                        //????????????
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent, RESULT_CODE_SEND_IMAGE_XIANGCE);
                    }
                }
                else
                {
                    //????????????2
                }


                break;
            case R.id.ll_wenjian:
                wenjian=true;
                Log.d(TAG, "???????????????");
                pickFile();
                break;
            case R.id.ll_weizhi:
                Log.d(TAG, "????????????");
                if (ContextCompat.checkSelfPermission(Chat_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Chat_Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    Toast.makeText(this, "??????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                } else {
                    intent = new Intent(this, MapPickerActivity.class);
                    intent.putExtra("sendLocation", true);
                    startActivityForResult(intent,RESULT_CODE_SEND_LOCATION);
                }
                break;

        }
    }
    private String tempFile() {
        String filename = StringUtil.get32UUID() + JPG;
        return StorageUtil.getWritePath(filename, StorageType.TYPE_TEMP);
    }
    // ??????????????????????????????
    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");
        this.startActivityForResult(intent, RESULT_CODE_SEND_FILE);
    }
    //???uri??????????????????????????????????????????????????????????????????????????????File???
    @RequiresApi(Build.VERSION_CODES.Q)
    public File uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        if (uri == null) return file;
        //android10????????????
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //??????????????????????????????
            ContentResolver contentResolver = context.getContentResolver();
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
            String displayName="";
            if(documentFile!=null)
            {
                String[]strs= Objects.requireNonNull(documentFile.getName()).split("\\.");
                displayName = strs[0]
                        + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
            }
            else
            {
                displayName = "uritofile"
                        + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
            }
            InputStream is = null;
            try {
                is = contentResolver.openInputStream(uri);
                File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                byte[] b = new byte[1024];
                while ((is.read(b)) != -1) {
                    fos.write(b);// ????????????
                }
                file = cache;
                fos.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}