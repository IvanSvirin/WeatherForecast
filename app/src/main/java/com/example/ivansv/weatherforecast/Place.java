package com.example.ivansv.weatherforecast;

/**
 * Created by ivansv on 30.12.2015.
 */
public class Place {
    private String name;
    private double latitude;
    private double longitude;
    private ForecastItem forecastItem;

    public ForecastItem getForecastItem() {
        return forecastItem;
    }

    public void setForecastItem(ForecastItem forecastItem) {
        this.forecastItem = forecastItem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
