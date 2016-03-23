package com.example.ivansv.weatherforecast;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ivansv.weatherforecast.CurrentWeatherModel.CurrentWeather;
import com.example.ivansv.weatherforecast.ForecastModel.Forecast;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateService extends IntentService {
    private static final String API_KEY = "138b3901759ae9758770c6acef29b4a7";
    private static final String DEFAULT_LOCATION = "default_location";
    private static final String DEFAULT_LOCATION_KEY = "default_location_key";
    private Location location;
    private static final int ACCESS_LOCATION_PERMISSION = 1;
    private String url = "http://api.openweathermap.org/data/2.5/";
    private String imageUrl = "http://openweathermap.org/img/w/";
    private String placeName;
    private String temperature;
    private String pressure;
    private String wind;
    private String icon;
    private char degree = 0x00B0;
    public static final String CONNECTION_STATE = "CONNECTION_STATE";
    public static final String ACTION_RETRY = "ACTION_RETRY";
    private String[] weekDays = new String[5];
    private String[] temperatures = new String[5];
    private String[] icons = new String[5];
    private GregorianCalendar calendar = new GregorianCalendar();
    private RestInterface restInterface;
    public static PendingIntent restartServicePendingIntent;
    public static AlarmManager restartServiceAlarmManager;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent restartServiceIntent;
        restartServiceAlarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        restartServiceIntent = new Intent(getApplicationContext(), UpdateService.class);
        restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 0, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        restartServiceAlarmManager.cancel(restartServicePendingIntent);
        restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60 * 1000, restartServicePendingIntent);
        Toast.makeText(getApplicationContext(), "Start Service", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sp = getSharedPreferences(DEFAULT_LOCATION, MODE_PRIVATE);
        location = getLocation();
        if (!sp.contains(DEFAULT_LOCATION_KEY) && location != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(DEFAULT_LOCATION_KEY, true);
            editor.apply();
            writeFile(location);
        }
        if (location == null && readFile() != null) {
            location = readFile();
        }
        if (location == null) {
//            sendErrorBroadcast();
        } else {
            requestWeather();
        }
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        SharedPreferences sp = getSharedPreferences(DEFAULT_LOCATION, MODE_PRIVATE);
//        location = getLocation();
//        if (!sp.contains(DEFAULT_LOCATION_KEY) && location != null) {
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putBoolean(DEFAULT_LOCATION_KEY, true);
//            editor.apply();
//            writeFile(location);
//        }
//        if (location == null && readFile() != null) {
//            location = readFile();
//        }
//        if (location == null) {
//            sendErrorBroadcast();
//        } else {
//            requestWeather();
//        }
////        return START_STICKY;
//        return START_REDELIVER_INTENT;
//    }

    private void requestWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restInterface = retrofit.create(RestInterface.class);
        restInterface.getWeatherReport(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                API_KEY).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                placeName = response.body().getName();
                temperature = String.valueOf((int) (response.body().getMain().getTemp() - 273.15)) + degree;
                wind = getWindDirection(response.body().getWind().getDeg()) + " " +
                        String.valueOf((int) (response.body().getWind().getSpeed() * 1)) + " m/s";
                pressure = String.valueOf((int) (response.body().getMain().getPressure() * 0.75006375541921)) + " mm Hg";
                icon = response.body().getWeather().get(0).getIcon() + ".png";
                if (placeName != null) {
                    requestForecast();
                    updateWidgetTop();
                } else {
//                    sendErrorBroadcast();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
//                sendErrorBroadcast();
            }
        });
    }

    private void requestForecast() {
//        Retrofit retrofit2 = new Retrofit.Builder()
//                .baseUrl(url)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        RestInterface restInterface2 = retrofit2.create(RestInterface.class);
        String cnt = "5";
        restInterface.getForecast(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                cnt, API_KEY).enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                for (int i = 0; i < 5; i++) {
                    calendar.setTimeInMillis((long) (response.body().getList().get(i).getDt()) * 1000);
                    weekDays[i] = getWeekDayName(calendar.get(Calendar.DAY_OF_WEEK));
                    temperatures[i] = String.valueOf((int) (response.body().getList().get(i).getTemp().getMin() - 273.15)) + degree +
                            "/" + String.valueOf((int) (response.body().getList().get(i).getTemp().getMax() - 273.15)) +
                            degree;
                    icons[i] = response.body().getList().get(i).getWeather().get(0).getIcon() + ".png";
                }
                if (icons[4] != null) {
//                    sendSuccessBroadcast();
                    updateWidgetBottom();
//                    stopSelf();
                } else {
//                    sendErrorBroadcast();
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
//                sendErrorBroadcast();
            }
        });
    }

    private void updateWidgetBottom() {
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.weather_widget_new);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, WeatherWidget.class);
        int[] iconIds = new int[5];
        iconIds[0] = R.id.icon0;
        iconIds[1] = R.id.icon1;
        iconIds[2] = R.id.icon2;
        iconIds[3] = R.id.icon3;
        iconIds[4] = R.id.icon4;
        int[] weekDayIds = new int[5];
        weekDayIds[0] = R.id.weekDay0;
        weekDayIds[1] = R.id.weekDay1;
        weekDayIds[2] = R.id.weekDay2;
        weekDayIds[3] = R.id.weekDay3;
        weekDayIds[4] = R.id.weekDay4;
        int[] temperatureIds = new int[5];
        temperatureIds[0] = R.id.temperature0;
        temperatureIds[1] = R.id.temperature1;
        temperatureIds[2] = R.id.temperature2;
        temperatureIds[3] = R.id.temperature3;
        temperatureIds[4] = R.id.temperature4;
        for (int i = 0; i < 5; i++) {
            Picasso.with(getApplicationContext())
                    .load(imageUrl + icons[i])
                    .resizeDimen(R.dimen.small_icon_width, R.dimen.small_icon_height)
                    .transform(new EmptySpaceCroppingTransformation())
                    .into(view, iconIds[i], manager.getAppWidgetIds(thisWidget));
            view.setTextViewText(weekDayIds[i], weekDays[i]);
            view.setTextViewText(temperatureIds[i], temperatures[i]);
            manager.updateAppWidget(thisWidget, view);
        }
    }

    private void updateWidgetTop() {
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.weather_widget_new);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName thisWidget = new ComponentName(this, WeatherWidget.class);
        Picasso.with(getApplicationContext())
                .load(imageUrl + icon)
                .resizeDimen(R.dimen.main_icon_width, R.dimen.main_icon_height)
                .transform(new EmptySpaceCroppingTransformation())
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

    private String getWeekDayName(int i) {
        String name = null;
        switch (i) {
            case 1:
                name = "SUN";
                break;
            case 2:
                name = "MON";
                break;
            case 3:
                name = "TUE";
                break;
            case 4:
                name = "WED";
                break;
            case 5:
                name = "THU";
                break;
            case 6:
                name = "FRI";
                break;
            case 7:
                name = "SAT";
                break;
        }
        return name;
    }

    private Location readFile() {
        try {
            FileInputStream fileInputStream = openFileInput("location.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Location location = (Location) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return location;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeFile(Location location) {
        try {
            FileOutputStream fileOutputStream = openFileOutput("location.txt", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(location);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
