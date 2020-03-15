package com.example.ablesson_1;

import android.util.Log;

import com.example.ablesson_1.model.WeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

class Connection {
    private static final String WEATHER_API_KEY = "14f34cd242746f2d76bb04739d7485fe"; //временный Api
    private String WEATHER_URL_PART1 = "https://api.openweathermap.org/data/2.5/weather?q=";
    private String WEATHER_URL_PART2 = "&units=metric&appid=";

    private WeatherRequest weatherRequest;

    Connection(String city) {
        try {
            final URL uri = new URL(WEATHER_URL_PART1 + city + WEATHER_URL_PART2 + WEATHER_API_KEY);
            Thread thread = new Thread(new Runnable() {
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
                        weatherRequest = gson.fromJson(result, WeatherRequest.class);
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
            });
            thread.start();
            thread.join();
        } catch (MalformedURLException e) {
            Log.e("Exc", "Fail URL", e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }

    WeatherRequest getWeatherRequest() {
        return weatherRequest;
    }
}
