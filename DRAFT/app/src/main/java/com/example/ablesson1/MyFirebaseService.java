package com.example.ablesson1;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//данная служба созана только для того, чтобы получать уведомления при
//запущенном приложении. Сюда можно прописать поведение
public class MyFirebaseService extends FirebaseMessagingService {

    private int messageId = 0;

    public MyFirebaseService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("MyLog", remoteMessage.getNotification().getBody());
        String title = remoteMessage.getNotification().getTitle();
        if (title == null) {
            title = "Push Message";
        }
        String text = remoteMessage.getNotification().getBody();
        //создаем нотификацию
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // Если надо посылать сообщения этому экземпляру приложения
        // или управлять подписками приложения на стороне сервера,
        // сохраните этот токен в базе данных. отправьте этот токен вашему
        Log.d("Mylog", "Token: " + token);
        sendRegistrationToService(token);
    }

    private void sendRegistrationToService(String token) {
    }
}
