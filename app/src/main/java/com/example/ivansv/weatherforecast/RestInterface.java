package com.example.ivansv.weatherforecast;

import com.example.ivansv.weatherforecast.ForecastModel.Forecast;

import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.Callback;


/**
 * Created by ivansv on 10.02.2016.
 */

public interface RestInterface {
    @GET("/weather")
    void getWeatherReport(@Query("lat") String lat,
                          @Query("lon") String lon,
                          @Query("APPID") String APPID,
                          Callback<Forecast> cb);

}
