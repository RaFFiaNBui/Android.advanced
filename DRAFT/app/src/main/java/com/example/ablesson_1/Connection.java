package com.example.ablesson_1;

import android.os.Handler;
import android.util.Log;

import com.example.ablesson_1.model.WeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

class Connection {
    private static final String WEATHER_API_KEY = "14f34cd242746f2d76bb04739d7485fe"; //временный Api
    private static final String WEATHER_URL_PART1 = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String WEATHER_URL_PART2_RU = "&units=metric&lang=ru&appid=";
    private static final String WEATHER_URL_PART2_EN = "&units=metric&lang=en&appid=";

    Connection(String city, final CityFragment.OnDataLoadedListener onDataLoadedListener) {
        try {
            final URL uri;
            if (Locale.getDefault().getLanguage().equals("ru")) {
                uri = new URL(WEATHER_URL_PART1 + city + WEATHER_URL_PART2_RU + WEATHER_API_KEY);
            } else {
                uri = new URL(WEATHER_URL_PART1 + city + WEATHER_URL_PART2_EN + WEATHER_API_KEY);
            }
            final Handler handler = new Handler();  //запоминаем основной поток
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpsURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpsURLConnection) uri.openConnection();
                        urlConnection.setRequestMethod("GET");  //устанавливаем метод получения данных - GET
                        urlConnection.setReadTimeout(10000);    //устанавливаем таймаут - 10 000 миллисекунд
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));  //читаем данные в поток
                        String result = getLines(in);
                        // преобразование данных запроса в модель
                        Gson gson = new Gson();
                        final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onDataLoadedListener.onLoaded(weatherRequest);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e("Exc", "Fail not found", e);
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("Exc", "Fail connection", e);
                        e.printStackTrace();
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            Log.e("Exc", "Fail URL", e);
            e.printStackTrace();
        }
    }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }
}
