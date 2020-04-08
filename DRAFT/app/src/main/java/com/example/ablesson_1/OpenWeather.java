package com.example.ablesson_1;

import com.example.ablesson_1.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("q") String cityName,
                                     @Query("units") String units,
                                     @Query("lang") String lang,
                                     @Query("appid") String keyApi);
}
