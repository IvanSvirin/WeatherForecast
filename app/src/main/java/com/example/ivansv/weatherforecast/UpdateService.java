package com.example.ivansv.weatherforecast;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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

import com.example.ivansv.weatherforecast.CurrentWeatherModel.CurrentWeather;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateService extends Service {
    private static final String API_KEY = "138b3901759ae9758770c6acef29b4a7";
    private Location location;
    private static final int ACCESS_LOCATION_PERMISSION = 1;
    private String url = "http://api.openweathermap.org/data/2.5";
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String placeName;
    private String temperature;
    private String pressure;
    private String wind;
    private String icon;
    private char degree = 0x00B0;
    private static Intent restartIntent;
    private static PendingIntent restartPendingIntent = null;
    private static AlarmManager alarmManager;
    private boolean isConnect = false;
    public static final String CONNECTION_STATE = "CONNECTION_STATE";
    public static final String ACTION_RETRY = "ACTION_RETRY";


    public UpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        restartIntent = new Intent(this, UpdateService.class);
//        restartPendingIntent = PendingIntent.getService(this, 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        location = getLocation();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                .build();
        RestInterface restInterface = restAdapter.create(RestInterface.class);
        restInterface.getWeatherReport(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                API_KEY, new Callback<CurrentWeather>() {
                    @Override
                    public void success(CurrentWeather currentWeather, Response response) {
                        Toast.makeText(UpdateService.this, placeName, Toast.LENGTH_SHORT).show();
                        placeName = currentWeather.getName();
                        temperature = String.valueOf((int) (currentWeather.getMain().getTemp() - 273.15)) + degree + "C";
                        wind = getWindDirection(currentWeather.getWind().getDeg()) + " " +
                                String.valueOf((int) (currentWeather.getWind().getSpeed() * 1)) + " m/s";
                        pressure = String.valueOf((int) (currentWeather.getMain().getPressure() * 0.75006375541921)) + " mm Hg";
                        icon = currentWeather.getWeather().get(0).getIcon() + ".png";
                        isConnect = true;

                        if (placeName != null) {
                            sendSuccessBroadcast();
                            updateWidget();
                        } else {
                            sendErrorBroadcast();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        isConnect = false;
                        Toast.makeText(UpdateService.this, "No connection", Toast.LENGTH_SHORT).show();
                        sendErrorBroadcast();
                    }
                });

//        if (placeName != null && isConnect) {
//            RemoteViews view = new RemoteViews(getPackageName(), R.layout.weather_widget);
//            AppWidgetManager manager = AppWidgetManager.getInstance(this);
//            ComponentName thisWidget = new ComponentName(this, WeatherWidget.class);
//            Picasso.with(getApplicationContext())
//                    .load(imageUrl + icon)
//                    .resizeDimen(R.dimen.icon_width, R.dimen.icon_height)
//                    .into(view, R.id.weatherIcon, manager.getAppWidgetIds(thisWidget));
//            view.setTextViewText(R.id.name, placeName);
//            view.setTextViewText(R.id.temperature, temperature);
//            view.setTextViewText(R.id.pressure, pressure);
//            view.setTextViewText(R.id.wind, wind);
//            manager.updateAppWidget(thisWidget, view);

//            alarmManager.cancel(restartPendingIntent);
//            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 30 * 1000, restartPendingIntent);
//        } else {
//            isConnect = false;

//            alarmManager.cancel(restartPendingIntent);
//            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, restartPendingIntent);
//        }
//        Intent retryIntent = new Intent(this, WeatherWidget.class);
//        retryIntent.setAction(ACTION_RETRY);
//        retryIntent.putExtra(CONNECTION_STATE, isConnect);
//        try {
//            PendingIntent.getBroadcast(this, 0, retryIntent, 0).send();
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void updateWidget() {
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.weather_widget_new2);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, WeatherWidget.class);
        Picasso.with(getApplicationContext())
                .load(imageUrl + icon)
                .resizeDimen(R.dimen.main_icon_width, R.dimen.main_icon_height)
                .into(view, R.id.weatherIcon, manager.getAppWidgetIds(thisWidget));
        view.setTextViewText(R.id.name, placeName);
        view.setTextViewText(R.id.temperature, temperature);
        view.setTextViewText(R.id.pressure, pressure);
        view.setTextViewText(R.id.wind, wind);
        manager.updateAppWidget(thisWidget, view);
    }

    private void sendErrorBroadcast() {
        Intent retryIntent = new Intent(this, WeatherWidget.class);
        retryIntent.setAction(ACTION_RETRY);
        retryIntent.putExtra(CONNECTION_STATE, false);
        sendBroadcast(retryIntent);
    }

    private void sendSuccessBroadcast() {
        Intent retryIntent = new Intent(this, WeatherWidget.class);
        retryIntent.setAction(ACTION_RETRY);
        retryIntent.putExtra(CONNECTION_STATE, true);
        sendBroadcast(retryIntent);
    }

    @Override
    public void onDestroy() {
//        alarmManager.cancel(restartPendingIntent);
        super.onDestroy();
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
