package com.example.ivansv.weatherforecast;

import android.Manifest;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ivansv.weatherforecast.ForecastModel.Forecast;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateService extends Service {
    public static final String API_KEY = "138b3901759ae9758770c6acef29b4a7";
    private Location location;
    public static final int ACCESS_LOCATION_PERMISSION = 1;
    private String url = "http://api.openweathermap.org/data/2.5";
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String placeName;
    private String temperature;
    private String pressure;
    private String wind;
    private String icon;
    private char degree = 0x00B0;

    public UpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        location = getLocation();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        RestInterface restInterface = restAdapter.create(RestInterface.class);
        restInterface.getWeatherReport(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                API_KEY, new Callback<Forecast>() {
                    @Override
                    public void success(Forecast forecast, Response response) {
                        Toast.makeText(UpdateService.this, placeName, Toast.LENGTH_SHORT).show();
                        placeName = forecast.getName();
                        temperature = String.valueOf((int) (forecast.getMain().getTemp() - 273.15)) + degree + "C";
                        wind = getWindDirection(forecast.getWind().getDeg()) + " " +
                                String.valueOf((int) (forecast.getWind().getSpeed() * 1)) + " m/s";
                        pressure = String.valueOf((int) (forecast.getMain().getPressure() * 0.75006375541921)) + " mm Hg";
                        icon = forecast.getWeather().get(0).getIcon() + ".png";
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });

        if (placeName != null) {
            RemoteViews view = new RemoteViews(getPackageName(), R.layout.weather_widget);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            ComponentName thisWidget = new ComponentName(this, WeatherWidget.class);
            Picasso.with(getApplicationContext())
                    .load(imageUrl + icon)
                    .resizeDimen(R.dimen.icon_width, R.dimen.icon_height)
                    .into(view, R.id.weatherIcon, manager.getAppWidgetIds(thisWidget));
            view.setTextViewText(R.id.name, placeName);
            view.setTextViewText(R.id.temperature, temperature);
            view.setTextViewText(R.id.pressure, pressure);
            view.setTextViewText(R.id.wind, wind);
            manager.updateAppWidget(thisWidget, view);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_PERMISSION);
        }
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }

    private String getWindDirection(Double deg) {
        String windDirection = null;
        switch ((int) (deg + 22.5) / 45) {
            case 0:
                windDirection = "N";
                break;
            case 1:
                windDirection = "NE";
                break;
            case 2:
                windDirection = "E";
                break;
            case 3:
                windDirection = "SE";
                break;
            case 4:
                windDirection = "S";
                break;
            case 5:
                windDirection = "SW";
                break;
            case 6:
                windDirection = "W";
                break;
            case 7:
                windDirection = "NW";
                break;
            case 8:
                windDirection = "N";
                break;
        }
        return windDirection;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}