package com.example.ablesson_1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ablesson_1.model.WeatherRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class CityFragment extends Fragment implements Constants {

    private String currentCity = "Moscow";
    private String temp;
    ArrayList<String> citiesList;
    ArrayList<String> temperatureList;

    private TextView currentName;
    private TextView currentTemperature;
    private TextView currentHumidity;
    private TextView sunrise;
    private TextView sunset;
    private TextView currentPressure;
    private TextView windSpeed;

    // Фабричный метод создания фрагмента
    // Фрагменты рекомендуется создавать через фабричные методы.
    static CityFragment create(Parcel parcel) {
        CityFragment f = new CityFragment();    // создание

        // Передача параметра
        Bundle args = new Bundle();
        args.putSerializable(PARCEL, parcel);
        f.setArguments(args);
        return f;
    }

    // Получить посылку из параметра
    Parcel getParcel() {
        Parcel parcel = (Parcel) getArguments().getSerializable(PARCEL);
        return parcel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Определить какой герб надо показать, и показать его
        View layout = inflater.inflate(R.layout.fragment_city, container, false);
        TextView textViewCity = layout.findViewById(R.id.city);
        changeSettings(layout);
        Parcel parcel = getParcel();
        textViewCity.setText(parcel.getCityName());
        currentCity = parcel.getCityName();
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //подтягиваем наш список городов
        String[] data = getResources().getStringArray(R.array.items_week_array);
        //инициализируем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_week);
        //подсказываем, что наш список конечный
        recyclerView.setHasFixedSize(true);
        //инициализируем LayoutManager (повторно, он был проинициализирован в fragment_main)
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        }
        //инициализируем адаптер
        WeekAdapter weekAdapter = new WeekAdapter(data);
        //устанавливаем нашему списку адаптер
        recyclerView.setAdapter(weekAdapter);
        //инициализация фонового изображения
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        if (sharedPref.getBoolean(IS_DARK_THEME, false)) {
            scrollView.setBackgroundResource(R.drawable.sky_night);
        } else {
            scrollView.setBackgroundResource(R.drawable.sky_day);
        }
        //отображение текущей даты
        setDate(view);
        //инициализация текстовых поле
        init(view);
        //устанавливаем соединение
        Connection connection = new Connection(currentCity);
        if (connection.getWeatherRequest() != null) {
            //сетим полученные данные
            displayWeather(connection.getWeatherRequest());
            //сохраняем историю
            saveHistory();
        } else {
            //alertdialog c исключением
        }
    }

    private void changeSettings(View v) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        //отрисовка восхода и заката
        boolean tempSun = sharedPref.getBoolean(SUN, true);
        TableRow TRSunset = v.findViewById(R.id.sunset_row);
        if (TRSunset != null) {
            TableRow TRSunrise = v.findViewById(R.id.sunrise_row);
            TRSunset.setVisibility(tempSun ? View.VISIBLE : View.GONE);
            TRSunrise.setVisibility(tempSun ? View.VISIBLE : View.GONE);
            //отрисовка давления
            boolean tempPressure = sharedPref.getBoolean(PRESSURE, true);
            TableRow TRPressure = v.findViewById(R.id.pressure_row);
            TRPressure.setVisibility(tempPressure ? View.VISIBLE : View.GONE);
            //отрисовка ветра
            boolean tempWind = sharedPref.getBoolean(WIND, true);
            TableRow TRWind = v.findViewById(R.id.wind_row);
            TRWind.setVisibility(tempWind ? View.VISIBLE : View.GONE);
        }
    }

    //метод отображения текущей даты
    private void setDate(View view) {
        Date currentDate = new Date();
        TextView textViewCurrentDate = view.findViewById(R.id.current_date);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        textViewCurrentDate.setText(dateFormat.format(currentDate));

        //установка дня недели
        TextView textViewDayOfWeek = view.findViewById(R.id.current_day_of_week);
        DateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        textViewDayOfWeek.setText(dayOfWeekFormat.format(currentDate));

        //установка времени
        TextView textViewCurrentTime = view.findViewById(R.id.current_time);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        textViewCurrentTime.setText(timeFormat.format(currentDate));
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
        temp = String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getTemp());
        currentTemperature.setText(temp);
        currentHumidity.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getHumidity()));
        SimpleDateFormat smp = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sunrise.setText(String.format(Locale.getDefault(), "%s", smp.format(weatherRequest.getSys().getSunrise() * 1000L)));
        sunset.setText(String.format(Locale.getDefault(), "%s", smp.format(weatherRequest.getSys().getSunset() * 1000L)));
        currentPressure.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getMain().getPressure()));
        windSpeed.setText(String.format(Locale.getDefault(), "%d", weatherRequest.getWind().getSpeed()));
        currentName.setText(String.format(Locale.getDefault(), "%s", weatherRequest.getName()));
    }
    //метод сохранения истории
    private void saveHistory () {
        citiesList = new ArrayList<>();
        citiesList.add(currentCity);
        temperatureList = new ArrayList<>();
        temperatureList.add(temp);
    }

    ArrayList<String> getCitiesList() {
        return citiesList;
    }

    ArrayList<String> getTemperatureList() {
        return temperatureList;
    }
}