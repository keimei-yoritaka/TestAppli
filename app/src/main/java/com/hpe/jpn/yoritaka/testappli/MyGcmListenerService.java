package com.hpe.jpn.yoritaka.testappli;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;
import com.hpe.jpn.yoritaka.testappli.util.Logger;

/**
 * Created by YoritakaK on 1/12/2017.
 */

public class MyGcmListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        String message = bundle.getString("message");
        Logger.i("Receiving a new notification.");

        //通知領域にメッセージを表示
        sendNotification(message);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent offHookIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        PendingIntent onHookIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("GCM Push Test")
                .setContentText("This is a test message")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 5000});
        notificationBuilder.addAction(R.drawable.offhook, "OffHook", offHookIntent);
        notificationBuilder.addAction(R.drawable.onhook, "OnHook", onHookIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        Logger.i("Now trying to send NEW_MSG_COMING intent to MainActivity!");
        Intent newMsg = new Intent(MyPreferences.NEW_MSG_COMING);
        newMsg.putExtra("message", "Receiving new GCM message.");
        LocalBroadcastManager.getInstance(this).sendBroadcast(newMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("MyGCMListenerService was destroyed.");
    }


}
