package com.example.qq.Application;

import android.content.Context;

import com.example.qq.Service.LocationService;

public class MyApplication {
    public static final String CONV_TYPE = "conversationType";
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final int RESULT_CODE_SEND_IMAGE_PAISHE = 22;
    public static final int RESULT_CODE_SEND_IMAGE_XIANGCE = 23;
    public static final int RESULT_CODE_SEND_FILE = 24;
    public static final int RESULT_CODE_SEND_LOCATION = 25;
    public static String PICTURE_DIR = "sdcard/JChatDemo/pictures/";
    public static LocationService locationService;
    public static Context context;

}
