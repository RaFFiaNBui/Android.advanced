package com.example.ablesson1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver implements Constants{

    private int messageId = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {

        String message = "";
        Log.d("MyLog", "MyBroadcastReceiver: onReceive: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            message = context.getString(R.string.low_battery);
        }
        if (intent.getAction().equals(NETWORK_IS_CONNECTED)) {
            message = context.getString(R.string.network_is_not_connected);
        }
        createNotification(context, message);
    }

    private void createNotification(Context context, String message) {
        //создем нотификацию
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.receiver_title))
                .setContentText(message);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(messageId++, builder.build());
        }
    }
}
