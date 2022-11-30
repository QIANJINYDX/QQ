package com.example.qq.Provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.qq.db.AppSqliteHelper;

public class MyContentProvider extends ContentProvider {

    //表示User所有数据
    public static final int USER_DIR=0;
    //表示User单条数据
    public static final int USER_ITEM=1;
    public static final int FRIENDS_DIR=2;
    public static final int FRIENDS_ITEM=3;

    public static final String AUTHORITY="com.example.qq.provider";
    private static UriMatcher uriMatcher;
    private AppSqliteHelper dbHelper;
    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"user",USER_DIR);
        uriMatcher.addURI(AUTHORITY,"user/#",USER_ITEM);
        uriMatcher.addURI(AUTHORITY,"friends",FRIENDS_DIR);
        uriMatcher.addURI(AUTHORITY,"friends/#",FRIENDS_ITEM);
    }
    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int deletedRow=0;
        switch (uriMatcher.match(uri))
        {
            case USER_DIR:
                deletedRow=db.delete("user",selection,selectionArgs);
                break;
            case USER_ITEM:
                String userid=uri.getPathSegments().get(1);
                deletedRow=db.delete("user","id = ?",new String[]{userid});
                break;
            case FRIENDS_DIR:
                deletedRow=db.delete("friends",selection,selectionArgs);
                break;
            case FRIENDS_ITEM:
                String friendsid=uri.getPathSegments().get(1);
                deletedRow=db.delete("friends","id = ?",new String[]{friendsid});
                break;
            default:
                break;
        }
        return deletedRow;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        //throw new UnsupportedOperationException("Not yet implemented");
        switch (uriMatcher.match(uri))
        {
            case USER_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.qq.provider.user";
            case USER_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.qq.provider.user";
            case FRIENDS_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.qq.provider.friends";
            case FRIENDS_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.qq.provider.friends";
            default:
                break;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        //throw new UnsupportedOperationException("Not yet implemented");
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Uri uriReturn=null;
        switch (uriMatcher.match(uri))
        {
            case USER_DIR:
            case USER_ITEM:
                long newUserid=db.insert("user",null,values);
                uriReturn=Uri.parse("content://"+AUTHORITY+"/user/"+newUserid);
                break;
            case FRIENDS_DIR:
            case FRIENDS_ITEM:
                long newFriendsID=db.insert("friends",null,values);
                uriReturn=Uri.parse("content://"+AUTHORITY+"/friends/"+newFriendsID);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        //创建数据库实例
        dbHelper=new AppSqliteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=null;
        switch (uriMatcher.match(uri))
        {
            case USER_DIR:
                cursor=db.query("user",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case USER_ITEM:
                String userid=uri.getPathSegments().get(1);
                cursor=db.query("user",projection,"id = ?",new String[]{userid},null,null,sortOrder);
                break;
            case FRIENDS_DIR:
                cursor=db.query("friends",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case FRIENDS_ITEM:
                String friendsid=uri.getPathSegments().get(1);
                cursor=db.query("friends",projection,"id = ?",new String[]{friendsid},null,null,sortOrder);
                break;
            default:
                break;
        }
        return cursor;
        // TODO: Implement this to handle query requests from clients.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        int updateRows=0;
        switch (uriMatcher.match(uri))
        {
            case USER_DIR:
                updateRows=db.update("user",values,selection,selectionArgs);
                break;
            case USER_ITEM:
                String userid=uri.getPathSegments().get(1);
                updateRows=db.update("user",values,"id = ?",new String[]{userid});
                break;
            case FRIENDS_DIR:
                updateRows=db.update("friends",values,selection,selectionArgs);
                break;
            case FRIENDS_ITEM:
                String Friendid=uri.getPathSegments().get(1);
                updateRows=db.update("friends",values,"id = ?",new String[]{Friendid});
            default:
                break;

        }
        return updateRows;
    }
}