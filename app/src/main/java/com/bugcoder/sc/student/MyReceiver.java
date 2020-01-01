package com.bugcoder.sc.student;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("---------------接收动态广播");
        if (intent.getAction().equals("com.bugcoder.sc.student.myreceiver")) {
            System.out.println("进来了");

            String id = "channel_001";
            String name = "name";
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//判断API
                NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(mChannel);
                notification = new Notification.Builder(context,id)
                        .setChannelId(id)
                        .setContentTitle("活动")
                        .setContentText("您有一条新通知")
                        .setSmallIcon(R.mipmap.ic_launcher_round).build();
            }else{
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,id)
                        .setContentTitle("活动")
                        .setContentText("您有一项新活动")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setOngoing(true)
                        .setChannelId(id);//无效
                notification = notificationBuilder.build();
            }

            notificationManager.notify(1,notification);
        }
    }
}
