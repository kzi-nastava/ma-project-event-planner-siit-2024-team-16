package com.example.evenmate.clients;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.evenmate.BuildConfig;
import com.example.evenmate.R;
import com.example.evenmate.models.user.Notification;
import com.google.gson.Gson;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class NotificationService extends Service {

    private StompClient mStompClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long userId = intent.getLongExtra("USER_ID", -1);
        connectWebSocket(userId);
        return START_STICKY;
    }
    private void connectWebSocket(long userId) {
        String wsUrl = "ws://" + BuildConfig.IP_ADDR + ":8080/ws";
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);

        mStompClient.connect();

        mStompClient.topic("/topic/notifications/" + userId)
                .subscribe(topicMessage -> {
                    Notification notification = new Gson().fromJson(topicMessage.getPayload(), Notification.class);
                    showSystemNotification(notification);
                });
    }
    private void showSystemNotification(Notification notification) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notifications_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New Notification")
                .setContentText(notification.getText())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStompClient != null) mStompClient.disconnect();
    }
}
