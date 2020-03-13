package com.example.ablesson_1;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ablesson_1.model.WeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

class Connection {

    private static final String WEATHER_API_KEY = "14f34cd242746f2d76bb04739d7485fe"; //временный Api
    private String WEATHER_URL_PART1 = "https://api.openweathermap.org/data/2.5/weather?q=";
    private String WEATHER_URL_PART2 = "&units=metric&appid=";

    private TextView currentName;
    private TextView currentTemperature;
    private TextView currentHumidity;
    private TextView sunrise;
    private TextView sunset;
    private TextView currentPressure;
    private TextView windSpeed;

    Connection(final View view, String city) {
        try {
            final URL uri = new URL(WEATHER_URL_PART1 + city + WEATHER_URL_PART2 + WEATHER_API_KEY);
            final Handler handler = new Handler(); //запоминаем основной поток
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
                        // возвращаемся к основному потоку
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                init(view);
                                displayWeather(weatherRequest);
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

    //инициализация полей
    private void init(View view) {
        currentTemperature = view.findViewById(R.id.t_day);
        currentHumidity = view.findViewById(R.id.humidity);
        sunrise = view.findViewById(R.id.sunrise);
        sunset = view.findViewById(R.id.sunset);
        currentPressure = view.findViewById(R.id.pressure);
        windSpeed = view.findViewById(R.id.wind);
        currentName = view.findViewById(R.id.city);
    }

    //заполнение полей
    private void displayWeather(WeatherRequest weatherRequest) {
        currentTemperature.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getTemp()));
        currentHumidity.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getHumidity()));
        SimpleDateFormat smp = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sunrise.setText(String.format(Locale.getDefault(), "%s", smp.format(weatherRequest.getSys().getSunrise() * 1000L)));
        sunset.setText(String.format(Locale.getDefault(), "%s", smp.format(weatherRequest.getSys().getSunset() * 1000L)));
        currentPressure.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getPressure()));
        windSpeed.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getWind().getSpeed()));
        currentName.setText(String.format(Locale.getDefault(), "%s", weatherRequest.getName()));
    }
}
