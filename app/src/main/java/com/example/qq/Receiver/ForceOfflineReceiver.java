package com.example.qq.Receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.qq.Collector.ActivityCollector;
import com.example.qq.activity.LoginActivity;


public class ForceOfflineReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent intent) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("警告");
        dialogBuilder.setMessage("你被迫离线,请重新登录!");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /**
                 * 调用ActivityCollector的finishAll()方法来销毁掉所有的活动，
                 * 并重新启动LoginActivity这个活动
                 */
                ActivityCollector.finishAll();//销毁所以活动
                Intent intent = new Intent(context, LoginActivity.class);
                /**
                 * 在广播接收器里启动活动，因此一定要给Intent加入FLAG_ACTIVITY_NEW_TASK这个标准
                 */
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        dialogBuilder.show();
//        AlertDialog alertDialog = dialogBuilder.create();
//        //需要设置AlertDialog的类型，保证在广播接收器中可以正常弹出
//        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        alertDialog.show();
    }
}