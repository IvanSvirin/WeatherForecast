package com.example.ivansv.weatherforecast;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.ivansv.weatherforecast.ForecastModel.Forecast;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ivansv on 12.02.2016.
 */
public class ConfigureActivity extends AppCompatActivity {
    public static final String API_KEY = "138b3901759ae9758770c6acef29b4a7";
    private Location location;
    public static final int ACCESS_LOCATION_PERMISSION = 1;
    private String url = "http://api.openweathermap.org/data/2.5";

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    public static final String WIDGET_PREF = "widget_pref";
    public static final String  WIDGET_PLACE_NAME = "widget_place_name";
    public static final String WIDGET_TEMPERATURE = "widget_temperature";
    public static final String  WIDGET_PRESSURE = "widget_pressure";
    public static final String  WIDGET_WIND = "widget_wind";

    private String placeName;
    private String temperature;
    private String pressure;
    private String wind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        location = getLocation();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        RestInterface restInterface = restAdapter.create(RestInterface.class);
        restInterface.getWeatherReport(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                API_KEY, new Callback<Forecast>() {
                    @Override
                    public void success(Forecast forecast, Response response) {
                        placeName = forecast.getName();
                        Toast.makeText(ConfigureActivity.this, "This is place " + placeName, Toast.LENGTH_SHORT).show();
                        temperature = String.valueOf((int) (forecast.getMain().getTemp() - 273.15));
                        wind = String.valueOf((int) (forecast.getWind().getSpeed() * 1));
                        pressure = String.valueOf((int) (forecast.getMain().getPressure() * 0.75006375541921));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
//        placeName = "name";
//        temperature = "22";
//        pressure = "750";
//        wind = "5";

        SharedPreferences sharedPreferences = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(WIDGET_PLACE_NAME, placeName);
        editor.putString(WIDGET_TEMPERATURE, temperature);
        editor.putString(WIDGET_PRESSURE, pressure);
        editor.putString(WIDGET_WIND, wind);
        editor.commit();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        WeatherWidget.updateWidget(this, appWidgetManager, sharedPreferences, widgetID);
//        RemoteViews view = new RemoteViews(getPackageName(), R.layout.weather_widget);
//        view.setTextViewText(R.id.placeTextView, placeName);
//        view.setTextViewText(R.id.temperature, temperature);
//        view.setTextViewText(R.id.pressure, pressure);
//        view.setTextViewText(R.id.wind, wind);
//        ComponentName thisWidget = new ComponentName(this, WeatherWidget.class);
//        AppWidgetManager manager = AppWidgetManager.getInstance(this);
//        manager.updateAppWidget(thisWidget, view);

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_PERMISSION);
        }
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }
}
