package com.project.gomawo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.project.gomawo.MainActivity;
import com.project.gomawo.R;

public class SnowDeerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId )
    {
        // QQQ: 두번 이상 호출되지 않도록 조치해야 할 것 같다.
        Intent clsIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clsIntent, 0);

        NotificationCompat.Builder clsBuilder;
        if( Build.VERSION.SDK_INT >= 26 )
        {
            String CHANNEL_ID = "channel_id";
            NotificationChannel clsChannel = new NotificationChannel( CHANNEL_ID, "서비스 앱", NotificationManager.IMPORTANCE_DEFAULT );
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel( clsChannel );

            clsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID );
        }
        else
        {
            clsBuilder = new NotificationCompat.Builder(this );
        }

        // QQQ: notification 에 보여줄 타이틀, 내용을 수정한다.
        clsBuilder.setSmallIcon( R.drawable.pig_icon )
                .setContentTitle( "서비스 앱" ).setContentText( "서비스 앱" )
                .setContentIntent( pendingIntent );

        // foreground 서비스로 실행한다.
        startForeground( 1, clsBuilder.build() );

        // QQQ: 쓰레드 등을 실행하여서 서비스에 적합한 로직을 구현한다.

        return START_STICKY;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String NOTIFICATION_CHANNEL_ID = "com.project.gomawo";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.pig_icon)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        System.out.println("App is running in background");
        startForeground(2, notification);

    }
}

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    void startForegroundService() {

//        String CHANNEL_ID = "channel_1";
//        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                "GoMaWo", NotificationManager.IMPORTANCE_LOW);
//
//        ((NotificationManager)
//                getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("")
//                .setContentText("").build();
//
//        startForeground(2, notification);
//        System.out.println("startForeground worked!");
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        NotificationCompat.Builder builder;
//        String CHANNEL_ID = "snowdeer_service_channel";
//        if (Build.VERSION.SDK_INT >= 26) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "SnowDeer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                    .createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        } else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("My Awesome App")
//                .setContentText("Doing some work...")
//                .setContentIntent(pendingIntent).build();

//        startForeground(1337, "snowdeer_service_channel");


//        NotificationCompat.Builder builder;
//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "snwodeer_service_channel";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "SnowDeer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                    .createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        } else {
//            builder = new NotificationCompat.Builder(this);
//        }
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                .setContent(remoteViews)
//                .setContentIntent(pendingIntent);
//
//        startForeground(1, builder.build());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
//            String channelId = "one-channel";
//            String channelName = "My Channel One";
//            String channelDescription = "My Channel One Description";
//            NotificationChannel channel = new NotificationChannel(channelId, channelName,
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription(channelDescription);
//
////각종 채널에 대한 설정
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300});
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
//
////channel이 등록된 builder
//            builder = new NotificationCompat.Builder(this, channelId);
//        } else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//    }
//}