package com.example.qq.db;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

//连接mySql数据库方法
public class DBUtils {
    private static final String TAG ="Dbutils";
    private static String driver="com.mysql.jdbc.Driver";
    //这里是MySQL的用户名
    private static String user="root";
    //这里是我的MySQL密码，本文作了隐藏处理，
    private static String password = "123456yY.";

    public static Connection getConn(String dbName){
        Connection connection = null;
        try{
            Class.forName(driver);
//数据的IP地址，本文中的地址不是我的真实地址，请换为你的真实IP地址。
            String ip="116.62.110.5";
            String port="3306";
            String url = "jdbc:mysql://" + ip + ":" + port +"/" + dbName+"?useSSL=false";
            connection= DriverManager.getConnection(url, user, password);
            Log.e("数据库连接", "成功!");
        } catch (Exception e) {
            Log.e("数据库连接", "失败!");
            e.printStackTrace();
        }
        return connection;
    }
}

