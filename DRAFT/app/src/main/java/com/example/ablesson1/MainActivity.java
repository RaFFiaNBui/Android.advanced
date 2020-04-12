package com.example.ablesson1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

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
}
