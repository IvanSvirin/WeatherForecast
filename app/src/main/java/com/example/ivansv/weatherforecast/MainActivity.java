package com.example.ivansv.weatherforecast;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static final String API_KEY = "138b3901759ae9758770c6acef29b4a7";
    private Location location;
    public static final int ACCESS_LOCATION_PERMISSION = 1;
    private String url = "http://api.openweathermap.org/data/2.5";


    private String placeName;
    private String temperature;
    private String pressure;
    private String wind;
    private String icon;
    private char degree = 0x00B0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_fragment);

//        final TextView tvName = (TextView) findViewById(R.id.placeTextView);
//        final TextView tvTemperature = (TextView) findViewById(R.id.temperatureValue);
//        final TextView tvCloudiness = (TextView) findViewById(R.id.cloudinessValue);
//        final TextView tvWindSpeed = (TextView) findViewById(R.id.windValue);
//        final TextView tvPressure = (TextView) findViewById(R.id.pressureValue);
//        final TextView tvWetness = (TextView) findViewById(R.id.wetnessValue);

//        location = getLocation();

//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(url)
//                .build();
//        RestInterface restInterface = restAdapter.create(RestInterface.class);
//        restInterface.getWeatherReport(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),
//                API_KEY, new Callback<Forecast>() {
//            @Override
//            public void success(Forecast forecast, Response response) {
//                tvName.setText(forecast.getName());
//                tvTemperature.setText(String.valueOf((int) (forecast.getMain().getTemp() - 273.15)));
//                tvCloudiness.setText(String.valueOf(forecast.getClouds().getAll()));
//                tvWindSpeed.setText(String.valueOf((int) (forecast.getWind().getSpeed() * 1)));
//                tvPressure.setText(String.valueOf((int) (forecast.getMain().getPressure() * 0.75006375541921)));
//                tvWetness.setText(String.valueOf(forecast.getMain().getHumidity()));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
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
