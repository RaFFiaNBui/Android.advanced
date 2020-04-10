package com.example.ablesson1.history;

import android.app.Application;

import androidx.room.Room;

// Паттерн Singleton, наследуем класс Application, создаём базу данных в методе onCreate

public class App extends Application {

    private static App instance;

    private HistoryDatabase db; // База данных

    // Получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Сохраняем объект приложения (для Singleton’а)
        instance = this;

        //Строим базу
        db = Room.databaseBuilder(getApplicationContext(),
                HistoryDatabase.class,
                "history_database")
                .allowMainThreadQueries()   //Только для примеров и тестирования.
                .build();
    }

    //Получаем EducationDao для составления запросов
    public HistoryDao getHistoryDao() {
        return db.getHistoryDao();
    }
}
