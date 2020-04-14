package com.example.ablesson1;

import com.example.ablesson1.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRequestByCity {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("q") String cityName,
                                     @Query("units") String units,
                                     @Query("lang") String lang,
                                     @Query("appid") String keyApi);
}
