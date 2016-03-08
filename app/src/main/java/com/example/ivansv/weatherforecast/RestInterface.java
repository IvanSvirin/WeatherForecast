package com.example.ivansv.weatherforecast;

import com.example.ivansv.weatherforecast.CurrentWeatherModel.CurrentWeather;
import com.example.ivansv.weatherforecast.ForecastModel.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestInterface {
    @GET("weather")
    Call<CurrentWeather> getWeatherReport(@Query("lat") String lat,
                                          @Query("lon") String lon,
                                          @Query("APPID") String APPID);

    @GET("forecast/daily")
    Call<Forecast> getForecast(@Query("lat") String lat,
                               @Query("lon") String lon,
                               @Query("cnt") String cnt,
                               @Query("APPID") String APPID);
}
