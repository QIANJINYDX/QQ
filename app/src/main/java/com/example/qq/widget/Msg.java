package com.example.qq.widget;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;

public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    public static final int STYLE_TXT=1;
    public static final int STYLE_IMG=2;
    public static final int STYLE_FILE=3;
    public static final int STYLE_WEIZHI=4;
    private String content;
    private int type;
    private int style;
    private Bitmap bitmap;
    private String img_path;
    private String file_path;
    private Uri uri;
    private File file;
    double latitude;
    double longitude;
    public Msg(String content,int type)
    {
        this.content=content;
        this.type=type;
    }

    public Msg() {
    }

    @NonNull
    @Override
    public String toString() {
        return "Msg{" +
                "content='" + content + '\'' +
                ", type=" + type +
                ", style=" + style +
                ", bitmap=" + bitmap +
                ", img_path='" + img_path + '\'' +
                ", file_path='" + file_path + '\'' +
                ", uri=" + uri +
                ", file=" + file +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getContent()
    {
        return content;
    }
    public int getType()
    {
        return type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
