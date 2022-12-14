package com.example.qq.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Target;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.qq.Collector.ActivityCollector;
import com.example.qq.R;
import com.example.qq.db.LoginUser;
import com.example.qq.util.CityBean;
import com.example.qq.util.PhotoUtils;
import com.example.qq.util.ProvinceBean;
import com.example.qq.util.ToastUtils;
import com.example.qq.widget.ItemGroup;
import com.example.qq.widget.RoundImageView;
import com.example.qq.widget.TitleLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static android.provider.MediaStore.EXTRA_OUTPUT;


public class PersonInfo extends AppCompatActivity implements View.OnClickListener {
    private ItemGroup ig_id,ig_name,ig_gender,ig_region,ig_brithday;
    private LoginUser loginUser = LoginUser.getInstance();
    private LinearLayout ll_portrait;
    private ToastUtils mToast = new ToastUtils();

    private ArrayList<String> optionsItems_gender = new ArrayList<>();
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    private OptionsPickerView pvOptions;

    private RoundImageView ri_portrati;
    private Uri imageUri;  //?????????????????????
    private static final int TAKE_PHOTO = 1;
    private static final int FROM_ALBUMS = 2;
    private PopupWindow popupWindow;
    private String imagePath;  //????????????????????????
    private PhotoUtils photoUtils = new PhotoUtils();

    private static final int EDIT_NAME = 3;
    private TitleLayout titleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_person_info);
        initOptionData();
        ig_id = (ItemGroup)findViewById(R.id.ig_id);
        ig_name = (ItemGroup)findViewById(R.id.ig_name);
        ig_gender = (ItemGroup)findViewById(R.id.ig_gender);
        ig_region = (ItemGroup)findViewById(R.id.ig_region);
        ig_brithday = (ItemGroup)findViewById(R.id.ig_brithday);
        ll_portrait = (LinearLayout)findViewById(R.id.ll_portrait);
        ri_portrati = (RoundImageView)findViewById(R.id.ri_portrait);
        titleLayout = (TitleLayout)findViewById(R.id.tl_title);

        ig_name.setOnClickListener(this);
        ig_gender.setOnClickListener(this);
        ig_region.setOnClickListener(this);
        ig_brithday.setOnClickListener(this);
        ll_portrait.setOnClickListener(this);

        //???????????????????????????
        titleLayout.getTextView_forward().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser.update();
                mToast.showShort(PersonInfo.this,"????????????");
                finish();
            }
        });
        initInfo();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //??????????????????loginUser?????????????????????????????????????????????????????????
        loginUser.reinit();
        ActivityCollector.removeActivity(this);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v){
        switch (v.getId()){
            //????????????????????????
            case R.id.ig_region:
                pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //???????????????????????????LoginUser?????????????????????????????????
                        String tx = options1Items.get(options1).getPickerViewText()
                                + options2Items.get(options1).get(options2);
                        ig_region.getContentEdt().setText(tx);
                        loginUser.setRegion(tx);
                    }
                }).setCancelColor(Color.GRAY).build();
                pvOptions.setPicker(options1Items, options2Items);//???????????????
                pvOptions.show();
                break;

            //????????????????????????
            case R.id.ig_gender:
                //???????????????
                pvOptions = new OptionsPickerBuilder(PersonInfo.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        //???????????????????????????LoginUser?????????????????????????????????
                        String tx = optionsItems_gender.get(options1);
                        ig_gender.getContentEdt().setText(tx);
                        loginUser.setGender(tx);
                    }
                }).setCancelColor(Color.GRAY).build();
                pvOptions.setPicker(optionsItems_gender);
                pvOptions.show();
                break;

            //????????????????????????
            case R.id.ig_brithday:
                //???????????????
                //???????????????????????????????????????????????????????????????????????????????????????????????????
                Calendar selectedDate = Calendar.getInstance();
                if (loginUser.getBrithday() != null){
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy???MM???dd???");
                        selectedDate.setTime(sdf.parse(loginUser.getBrithday()));
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
                //?????????picker???show
                TimePickerView pvTime = new TimePickerBuilder(PersonInfo.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //???????????????????????????LoginUser?????????????????????????????????
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy???MM???dd???");
                        ig_brithday.getContentEdt().setText(sdf.format(date));
                        loginUser.setBrithday(sdf.format(date));
                    }
                }).setDate(selectedDate).setCancelColor(Color.GRAY).build();
                pvTime.show();
                break;
            //???????????????????????????
            case R.id.ll_portrait:
                //????????????????????????????????????????????????
                show_popup_windows();
                break;
            //???????????????????????????
            case R.id.ig_name:
                Intent intent  = new Intent(PersonInfo.this, EditName.class);
                startActivityForResult(intent, EDIT_NAME);
                break;
            default:
                break;
        }
    }
    //????????????????????????
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        switch (requestCode){
            //??????????????????
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        //??????????????????????????????????????????
                        Bitmap bitmap = BitmapFactory.decodeStream((getContentResolver().openInputStream(imageUri)));
                        ri_portrati.setImageBitmap(bitmap);
                        loginUser.setPortrait(photoUtils.bitmap2byte(bitmap));
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            //????????????????????????
            case FROM_ALBUMS:
                if(resultCode == RESULT_OK){
                    //?????????????????????
                    if(Build.VERSION.SDK_INT >= 19){
                        imagePath =  photoUtils.handleImageOnKitKat(this, data);
                    }else {
                        imagePath = photoUtils.handleImageBeforeKitKat(this, data);
                    }
                }
                if(imagePath != null){
                    //??????????????????????????????????????????
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ri_portrati.setImageBitmap(bitmap);
                    loginUser.setPortrait(photoUtils.bitmap2byte(bitmap));
                }else{
                    Log.d("food","??????????????????");
                }
                break;
            //???????????????????????????????????????
            case EDIT_NAME:
                if(resultCode == RESULT_OK){
                    ig_name.getContentEdt().setText(loginUser.getName());
                }
                break;
            default:
                break;
        }
    }
    //???????????????????????????????????????
    private void initInfo(){
        LoginUser loginUser = LoginUser.getInstance();
        ig_id.getContentEdt().setText(loginUser.getAccount());  //ID???int??????string
        ig_name.getContentEdt().setText(loginUser.getName());
        Log.d("TAG", loginUser.toString());
        if(loginUser.getPortrait()==null)
        {
            @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.mipmap.default_header);
            ri_portrati.setImageBitmap(BitmapFactory.decodeStream(is));
        }
        else
        {
            ri_portrati.setImageBitmap(photoUtils.byte2bitmap(loginUser.getPortrait()));
        }
        ig_gender.getContentEdt().setText(loginUser.getGender());
        ig_region.getContentEdt().setText(loginUser.getRegion());
        ig_brithday.getContentEdt().setText(loginUser.getBrithday());
    }

    //??????????????????????????????????????????
    private void initOptionData(){
        //?????????????????????
        optionsItems_gender.add(new String("??????"));
        optionsItems_gender.add(new String("???"));
        optionsItems_gender.add(new String("???"));

        //?????????????????????
        String province_data = readJsonFile("province.json");
        String city_data = readJsonFile("city.json");

        Gson gson = new Gson();

        options1Items = gson.fromJson(province_data, new TypeToken<ArrayList<ProvinceBean>>(){}.getType());
        ArrayList<CityBean> cityBean_data = gson.fromJson(city_data, new TypeToken<ArrayList<CityBean>>(){}.getType());
        for(ProvinceBean provinceBean:options1Items){
            ArrayList<String> temp = new ArrayList<>();
            for (CityBean cityBean : cityBean_data){
                if(provinceBean.getProvince().equals(cityBean.getProvince())){
                    temp.add(cityBean.getName());
                }
            }
            options2Items.add(temp);
        }

    }

    //?????????asset????????????json?????????
    //??????????????????String
    private String readJsonFile(String file){
        StringBuilder newstringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getResources().getAssets().open(file);

            InputStreamReader isr = new InputStreamReader(inputStream);

            BufferedReader reader = new BufferedReader(isr);

            String jsonLine;
            while ((jsonLine = reader.readLine()) != null) {
                newstringBuilder.append(jsonLine);
            }
            reader.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data =  newstringBuilder.toString();
        return data;
    }

    //???????????????????????????????????????????????????????????????
    private void show_popup_windows(){
        RelativeLayout layout_photo_selected = (RelativeLayout) getLayoutInflater().inflate(R.layout.photo_select,null);
        if(popupWindow==null){
            popupWindow = new PopupWindow(layout_photo_selected, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        }
        //??????popupwindows
        popupWindow.showAtLocation(layout_photo_selected, Gravity.CENTER, 0, 0);
        //???????????????
        TextView take_photo =  (TextView) layout_photo_selected.findViewById(R.id.take_photo);
        TextView from_albums = (TextView)  layout_photo_selected.findViewById(R.id.from_albums);
        LinearLayout cancel = (LinearLayout) layout_photo_selected.findViewById(R.id.cancel);
        //??????????????????
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupWindow != null && popupWindow.isShowing()) {
                    imageUri = photoUtils.take_photo_util(PersonInfo.this, "com.qq.android.fileprovider", "output_image.jpg");
                    //????????????????????????????????????imageUri?????????outputImage???
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_PHOTO);
                    //???????????????
                    popupWindow.dismiss();
                }
            }
        });
        //??????????????????
        from_albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????????
                if(ContextCompat.checkSelfPermission(PersonInfo.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PersonInfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    //????????????
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, FROM_ALBUMS);
                }
                //???????????????
                popupWindow.dismiss();
            }
        });
        //??????????????????
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }
}
