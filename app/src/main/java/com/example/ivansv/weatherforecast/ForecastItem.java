package com.example.ivansv.weatherforecast;

import android.os.Parcel;
import android.os.Parcelable;

public class ForecastItem implements Parcelable{
    private String dayTime;
    private String date;
    private String temperature;
    private String cloudiness;
    private String precipitation;
    private String pressure;
    private String windSpeed;
    private String windDirection;
    private String wetness;
    private String realFeel;

    public ForecastItem() {}

    protected ForecastItem(Parcel in) {
        dayTime = in.readString();
        date = in.readString();
        temperature = in.readString();
        cloudiness = in.readString();
        precipitation = in.readString();
        pressure = in.readString();
        windSpeed = in.readString();
        windDirection = in.readString();
        wetness = in.readString();
        realFeel = in.readString();
    }

    public static final Creator<ForecastItem> CREATOR = new Creator<ForecastItem>() {
        @Override
        public ForecastItem createFromParcel(Parcel in) {
            return new ForecastItem(in);
        }

        @Override
        public ForecastItem[] newArray(int size) {
            return new ForecastItem[size];
        }
    };

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setCloudiness(String cloudiness) {
        this.cloudiness = cloudiness;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setWetness(String wetness) {
        this.wetness = wetness;
    }

    public void setRealFeel(String realFeel) {
        this.realFeel = realFeel;
    }

    public String getDayTime() {
        return dayTime;
    }

    public String getDate() {
        return date;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getWetness() {
        return wetness;
    }

    public String getRealFeel() {
        return realFeel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dayTime);
        dest.writeString(date);
        dest.writeString(temperature);
        dest.writeString(cloudiness);
        dest.writeString(precipitation);
        dest.writeString(pressure);
        dest.writeString(windSpeed);
        dest.writeString(windDirection);
        dest.writeString(wetness);
        dest.writeString(realFeel);
    }
}
