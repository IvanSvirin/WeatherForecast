package com.example.ivansv.weatherforecast;

import com.example.ivansv.weatherforecast.CurrentWeatherModel.CurrentWeather;
import com.example.ivansv.weatherforecast.ForecastModel.Forecast;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ivansv on 10.02.2016.
 */

public interface RestInterface {
    @GET("/weather")
    void getWeatherReport(@Query("lat") String lat,
                          @Query("lon") String lon,
                          @Query("APPID") String APPID,
                          Callback<CurrentWeather> cb);

    @GET("/forecast/daily")
    void getForecast(@Query("lat") String lat,
                     @Query("lon") String lon,
                     @Query("cnt") String cnt,
                     @Query("APPID") String APPID,
                     Callback<Forecast> callback);
}
