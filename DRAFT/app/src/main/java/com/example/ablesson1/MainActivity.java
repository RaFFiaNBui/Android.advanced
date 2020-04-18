package com.example.ablesson1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 40404;

    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

    // Клиент для регистрации пользователя через Гугл
    private GoogleSignInClient googleSignInClient;

    // Кнопка регистрации через гугл
    private com.google.android.gms.common.SignInButton buttonSignIn;
    // Кнопка выхода из Гугл
    private MaterialButton buttonSingOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMenu();
        //вывод на начальный экран главного фрагмента
        showFirstScreen(savedInstanceState);
        //инициализация канала нотификаций
        initNotificationChannel();
        //програмная регистрация ресиверов о низком заряде батареи и доступности сети
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        registerReceiver(receiver, new IntentFilter(NETWORK_IS_CONNECTED));
        //получение токена
        initGetToken();

        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл (регулируется параметром)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Получить клиента для регистрации, а также данных по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // кнопка регистрации пользователя
        buttonSignIn = findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // кнопка выхода
        buttonSingOut = findViewById(R.id.sing_out_button);
        buttonSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableSign();
        // Проверим, заходил ли пользователь в этом приложении через Гугл
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Пользователь уже заходил, сделаем кнопку недоступной
            disableSign();
            // Обновим почтовый адрес этого пользователя и выведем его на экран
            Toast.makeText(this, "OnStart:" + account.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты по нему уже готовы.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
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

    //разрегистрируем ресивер при закрытии приложения
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    // Инициация регистрации пользователя
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Выход из учетной записи в приложении
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Sign out", Toast.LENGTH_SHORT).show();
                        enableSign();
                    }
                });
    }

    private void enableSign() {
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign() {
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Регистрация прошла успешно
            disableSign();
            Toast.makeText(this, account.getEmail(), Toast.LENGTH_LONG).show();
        } catch (ApiException e) {
            Log.w("MyLog", "handleSignInResult: failed code=" + e.getStatusCode());
            Toast.makeText(this, "Ошибка" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
