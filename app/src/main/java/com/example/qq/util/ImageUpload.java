package com.example.qq.util;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageUpload{
    /**
     * The imgur client ID for OkHttp recipes. If you're using imgur for anything other than running
     * these examples, please request your own client ID! https://api.imgur.com/oauth2
     */
    private static final String IMGUR_CLIENT_ID = "123";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final OkHttpClient client = new OkHttpClient();
    public static void run(File f,String send_number,String accept_number,String style) throws Exception {
        final File file=f;
        new Thread() {
            @Override
            public void run() {
                //子线程需要做的工作
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("send_number", send_number)
                        .addFormDataPart("accept_number", accept_number)
                        .addFormDataPart("style", style)
                        .addFormDataPart("file", UUID.randomUUID().toString()+".png",
                                RequestBody.create(MEDIA_TYPE_PNG, file))
                        .build();
                //设置为自己的ip地址
                Request request = new Request.Builder()
                        .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                        .url("http://116.62.110.5:5000/update")
                        .post(requestBody)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
