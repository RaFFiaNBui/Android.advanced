package com.example.ablesson_1;

import android.os.Handler;
import android.util.Log;

import com.example.ablesson_1.model.WeatherRequest;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Locale;

//данный класс работал в паре с Connection до подключения библиотеки Retrofit
/*class DataParsing {

    DataParsing(final CityFragment.OnDataLoadedListener onDataLoadedListener, final Handler handler, final String result) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("MyLog", "run: Thread from DataParsing is started");
                // преобразование данных запроса в модель
                Gson gson = new Gson();
                final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                //парсинг данных
                parsing(onDataLoadedListener, handler, weatherRequest);
            }
        }).start();
    }

    private void parsing(final CityFragment.OnDataLoadedListener onDataLoadedListener, Handler handler, WeatherRequest weatherRequest) {
        final String currentTemperature = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getTemp());
        final String currentHumidity = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getHumidity());
        SimpleDateFormat smp = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final String sunrise = String.format(Locale.getDefault(), "%s", smp.format(weatherRequest.getSys().getSunrise() * 1000L));
        final String sunset = String.format(Locale.getDefault(), "%s", smp.format(weatherRequest.getSys().getSunset() * 1000L));
        final String currentPressure = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getPressure());
        final String windSpeed = String.format(Locale.getDefault(), "%d", weatherRequest.getWind().getSpeed());
        final String currentName = String.format(Locale.getDefault(), "%s", weatherRequest.getName());
        //передаем данные в основной поток
        handler.post(new Runnable() {
            @Override
            public void run() {
                onDataLoadedListener.onLoaded(currentName, currentTemperature, currentHumidity, sunrise, sunset, currentPressure, windSpeed);
            }
        });

    }
}*/
