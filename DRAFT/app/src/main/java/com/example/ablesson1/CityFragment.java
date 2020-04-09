package com.example.ablesson1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.example.ablesson1.history.App;
import com.example.ablesson1.history.HistoryDao;
import com.example.ablesson1.history.HistorySource;
import com.example.ablesson1.history.LineOfHistory;
import com.example.ablesson1.model.WeatherRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

//import java.util.ArrayList; //неиспользуется после подключения Room

public class CityFragment extends Fragment implements Constants {

    private static final String WEATHER_API_KEY = "14f34cd242746f2d76bb04739d7485fe"; //временный Api
    private String currentCity;  //будет привязана геолокация

/*    private static ArrayList<String> citiesList = new ArrayList<>();        //для экрана истории //неиспользуется после подключения Room
    private static ArrayList<String> temperatureList = new ArrayList<>();   //для экрана истории //неиспользуется после подключения Room*/

    private TextView currentName;
    private TextView currentTemperature;
    private TextView currentHumidity;
    private TextView sunrise;
    private TextView sunset;
    private TextView currentPressure;
    private TextView windSpeed;

    private OpenWeather openWeather;

    //версия 2 использовалась с классом Connection и DataParsing
/*    public interface OnDataLoadedListener {
        void onLoaded(String strName, String strTemperature, String strHumidity, String strSunrise,
                      String strSunset, String strPressure, String strWindSpeed);
    }*/

    //вешаем лисенер, который будет сетить наши данные после их загрузки и парсинга
    //версия 2 использовалась с классом Connection и DataParsing
/*    private final OnDataLoadedListener onDataLoadedListener = new OnDataLoadedListener() {
        @Override
        public void onLoaded(String strName, String strTemperature, String strHumidity, String strSunrise,
                             String strSunset, String strPressure, String strWindSpeed) {
            currentTemperature.setText(strTemperature);
            currentHumidity.setText(strHumidity);
            sunrise.setText(strSunrise);
            sunset.setText(strSunset);
            currentPressure.setText(strPressure);
            windSpeed.setText(strWindSpeed);
            currentName.setText(strName);
            saveHistory(strTemperature);
        }
    };*/

    //версия 2 использовалась с классом Connection и DataParsing
/*    public interface exceptionListener {
        void setException(int code);
    }*/

    //слушатель - обработчик ошибок
    //версия 2 использовалась с классом Connection и DataParsing
/*    private final exceptionListener exceptionListener = new exceptionListener() {
        @Override
        public void setException(int code) {
            String message;
            if (code == 1) {
                message = Objects.requireNonNull(getActivity()).getString(R.string.file_not_found);
            } else {
                message = Objects.requireNonNull(getActivity()).getString(R.string.fail_connection);
            }
            if (getFragmentManager() != null) {
                //создаем диалоговое окно с необходимым нам сообщением
                MyDialogFragment.create(message).show(getFragmentManager(), "Exception");
            }
        }
    };*/

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
        Parcel parcel = null;
        if (getArguments() != null) {
            parcel = (Parcel) getArguments().getSerializable(PARCEL);
        }
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
        //Если это 1 запуск, то берем настройки из SharedPreference, если нет - из Parcel
        //В дальнейшем Parcel можно убрать вообще и использовать только SharedPreference
        if (parcel == null) {
            SharedPreferences sPref = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
            currentCity = sPref.getString(CITY, "Москва");
        } else {
            currentCity = parcel.getCityName();
        }
        textViewCity.setText(currentCity);
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
        SharedPreferences sharedPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        if (sharedPref.getBoolean(IS_DARK_THEME, false)) {
            scrollView.setBackgroundResource(R.drawable.sky_night);
        } else {
            scrollView.setBackgroundResource(R.drawable.sky_day);
        }
        //отображение текущей даты
        setDate(view);
        //инициализация текстовых полей
        init(view);
        //устанавливаем соединение версия 2 использовалась с классом Connection и DataParsing
        //new Connection(currentCity, onDataLoadedListener, exceptionListener);
        // установка параметров для запроса
        String lang;  //выбор языка
        final String units; //выбор системы измерений
        if (Locale.getDefault().getLanguage().equals("ru")) {
            lang = "ru";
            units = "metric";
        } else {
            lang = "en";
            units = "imperial";
        }
        //установка соединения по средствам Retrofit
        initRetrofit();
        requestRetrofit(currentCity, units, lang);
    }

    //метод отрисовки необходимых настроек
    private void changeSettings(View v) {
        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
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

/*    //метод сохранения истории
    private void saveHistory(String temp) { //неиспользуется после подключения Room
        citiesList.add(currentCity);
        temperatureList.add(temp);
    }

    static ArrayList<String> getCitiesList() { //неиспользуется после подключения Room
        return citiesList;
    }

    static ArrayList<String> getTemperatureList() { //неиспользуется после подключения Room
        return temperatureList;
    }*/

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")  //базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //конвертер для преобразования из json в объкект
                .client(getLoggingOkHttpClient())
                .build();
        openWeather = retrofit.create(OpenWeather.class);   //создаем объект при помощи которогобудем выполнять запросы
    }

    private OkHttpClient getLoggingOkHttpClient() {
        //Сначала создаем HttpLoggingInterceptor. В нем настраиваем уровень логирования.
        // Если у нас Debug билд, то выставляем максимальный уровень (BODY), иначе - ничего
        // не логируем, чтобы не палить в логах релизные запросы.
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        //HttpLoggingInterceptor мы не можем напрямую передать в Retrofit билдер. Поэтому
        // сначала создаем OkHttpClient, ему передаем HttpLoggingInterceptor, и уже этот
        // OkHttpClient используем в Retrofit билдере
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    private void requestRetrofit(String city, String units, String lang) {
        openWeather.loadWeather(city, units, lang, CityFragment.WEATHER_API_KEY)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            String temp = String.format(Locale.getDefault(), "%d", response.body().getMain().getTemp());
                            currentTemperature.setText(temp);
                            currentHumidity.setText(String.format(Locale.getDefault(), "%d", response.body().getMain().getHumidity()));
                            currentPressure.setText(String.format(Locale.getDefault(), "%d", response.body().getMain().getPressure()));
                            windSpeed.setText(String.format(Locale.getDefault(), "%d", response.body().getWind().getSpeed()));
                            currentCity = String.format(Locale.getDefault(), "%s", response.body().getName());
                            currentName.setText(currentCity);
                            //Время восхода и заката приводим к привычному виду
                            SimpleDateFormat smp = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            sunrise.setText(String.format(Locale.getDefault(), "%s", smp.format(response.body().getSys().getSunrise() * 1000L)));
                            sunset.setText(String.format(Locale.getDefault(), "%s", smp.format(response.body().getSys().getSunset() * 1000L)));
                            //saveHistory(temp);    //неиспользуется после подключения Room
                            saveHistoryRoom(temp);  //сохранение истории в БД Room
                            saveCity(); //сохранение текущего города в SharedPreference
                        } else {
                            Log.e("MyLog", "onResponse: Город не был найден на сервере code=" + response.code() + " message=" + response.message());
                            String message = getResources().getString(R.string.error_msg_part_1) + city + getResources().getString(R.string.error_msg_part_2);
                            //создаем диалоговое окно с необходимым нам сообщением
                            if (getFragmentManager() != null) {
                                MyDialogFragment.create(message).show(getFragmentManager(), "Exception");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        Log.e("MyLog", "onFailure: Ошибка соединения", t);  //код в случае ошибки соединения
                        String message = getResources().getString(R.string.fail_connection);
                        //создаем диалоговое окно с необходимым нам сообщением
                        if (getFragmentManager() != null) {
                            MyDialogFragment.create(message).show(getFragmentManager(), "Exception");
                        }
                    }
                });
    }

    //сохранение истории в БД Room
    private void saveHistoryRoom(String temp) {
        HistoryDao historyDao = App.getInstance().getHistoryDao();
        HistorySource historySource = new HistorySource(historyDao);
        LineOfHistory lineOfHistory = new LineOfHistory();
        lineOfHistory.cityName = currentCity;
        lineOfHistory.cityTemp = temp;
        lineOfHistory.date = (System.currentTimeMillis() / 1000);
        historySource.addLine(lineOfHistory);
    }

    //сохранение текущего города в SharedPreference
    private void saveCity() {
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CITY, currentCity);
        editor.apply();
    }
}