package com.example.ablesson1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.core.app.ActivityCompat;
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

import static android.content.Context.LOCATION_SERVICE;
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

    private String lang;  //выбор языка
    private String units; //выбор системы измерений
    private WeatherRequestByCity weatherRequestByCity;
    private WeatherRequestByLocation weatherRequestByLocation;

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
        if (Locale.getDefault().getLanguage().equals("ru")) {
            lang = "ru";
            units = "metric";
        } else {
            lang = "en";
            units = "imperial";
        }
        //проверка сети
        checkConnection();

        if (getArguments() == null) {
            //Запрашиваем Permission’ы и координаты
            requestPermissions();
        } else {
            //установка соединения по средствам Retrofit
            initRetrofit();
            requestRetrofit(currentCity, units, lang);
        }
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

    //проверка доступности сети
    private void checkConnection() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        Intent intent = new Intent(NETWORK_IS_CONNECTED);
        if (!Objects.requireNonNull(networkInfo).isConnected()) {
            getActivity().sendBroadcast(intent);
        }
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")  //базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //конвертер для преобразования из json в объкект
                .client(getLoggingOkHttpClient())
                .build();
        weatherRequestByCity = retrofit.create(WeatherRequestByCity.class);   //создаем объект при помощи которогобудем выполнять запросы
        weatherRequestByLocation = retrofit.create(WeatherRequestByLocation.class);   //создаем объект при помощи которогобудем выполнять запросы
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
        weatherRequestByCity.loadWeather(city, units, lang, CityFragment.WEATHER_API_KEY)
                .enqueue(getCallback());
    }

    private void requestRetrofit(Double lat, Double lng, String units, String lang) {
        weatherRequestByLocation.loadWeather(lat, lng, units, lang, CityFragment.WEATHER_API_KEY)
                .enqueue(getCallback());
    }

    private Callback<WeatherRequest> getCallback() {
        return new Callback<WeatherRequest>() {
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
                    String message = getResources().getString(R.string.error_msg_part_1) + currentCity + getResources().getString(R.string.error_msg_part_2);
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
        };
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

    // Запрашиваем Permission’ы
    private void requestPermissions() {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у пользователя
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем координаты
            requestLocation();
            Log.d("MyLog", "requestPermissions: requestLocation() - Запрашиваем координаты;");
        } else {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
            Log.d("MyLog", "requestPermissions: requestLocationPermissions() - Запрашиваем Permission’ы у пользователя");
        }
    }

    // Запрашиваем координаты
    private void requestLocation() {
        // Еще раз прверяем  Permission есди их нет, просто выходим.
        //Без этой проверки locationManager будет выдавать здесь ошибку
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        // Получаем менеджер геолокаций
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
        //Устанавливаем критерий на приблизительное местоположение
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // Получаем наиболее подходящий провайдер геолокации по критериям.
        // Можно и самостоятельно: LocationManager.GPS_PROVIDER, NETWORK_PROVIDER или PASSIVE_PROVIDER.
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 10000, 10,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();    //широта
                            double longitude = location.getLongitude(); //долгота

                            Log.d("MyLog", "onLocationChanged: latitude" + latitude);
                            Log.d("MyLog", "onLocationChanged: longitude" + longitude);
                            //выполняем запрос по координатам
                            initRetrofit();
                            requestRetrofit(latitude, longitude, units, lang);
                            //получив координаты, дальнейшие запросы нам не нужны
                            locationManager.removeUpdates(this);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.CALL_PHONE)) {
            // Запрашиваем эти два Permission’а у пользователя
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
        Log.d("MyLog", "requestLocationPermissions: запрос");
    }


    // Результат запроса Permission’а у пользователя:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Проверка соответсвия ответа запросу
            // проверка ответа
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Если любая из пермиссий дана, то запросим координаты
                requestLocation();
                Log.d("MyLog", "onRequestPermissionsResult: ответ");
            }
        }
    }
}