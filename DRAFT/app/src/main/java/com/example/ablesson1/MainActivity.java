package com.example.ablesson1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends BaseActivity {

    MyBroadcastReceiver receiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMenu();
        //вывод на начальный экран главного фрагмента
        showFirstScreen(savedInstanceState);
        //инициализация канала нотификаций
        initNotificationChannel();
        //програмная регистрация ресивера о низком заряде батареи
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY));
        initGetToken();
    }

    //вывод на начальный экран главного фрагмента
    private void showFirstScreen(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            CityFragment cityFragment = new CityFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.first_fragment, cityFragment);
            ft.commit();
        }
    }

    //инициализация канала нотификаций
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "myChannel", importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    //получение токена
    private void initGetToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MyLog", "getInstanceId failed", task.getException());
                            return;
                        }
                        // получим наш token
                        String token = task.getResult().getToken();
                        // token to Log and toast
                        Log.d("MyLog", "My Token:" + token);
                        Toast.makeText(MainActivity.this, "Token received", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
