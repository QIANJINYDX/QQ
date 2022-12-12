package com.example.qq.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static void sendHttpRequest(String address,final HttpCallbackListener listener) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null)
                    {
                        response.append(line);
                    }
                    if(listener!=null)
                    {
                        listener.onFinish(response.toString());
                    }
                }
                catch (Exception e)
                {
                    if(listener!=null)
                    {
                        listener.onError(e);
                    }
                }
                finally {
                    if(connection!=null)
                    {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback)
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).addHeader("Connection", "close").build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendOkHttpRequestPost(String address, RequestBody formBody, Callback callback)
    {
        OkHttpClient client=new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
        Request request=new Request
                .Builder()
                .url(address)
                .header("Connection", "close")
                .addHeader("Connection", "close")
                .addHeader("Transfer-Encoding","chunked")
                .addHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
