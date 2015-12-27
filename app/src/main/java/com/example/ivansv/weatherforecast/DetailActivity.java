package com.example.ivansv.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by ivansv on 26.12.2015.
 */
public class DetailActivity extends AppCompatActivity {
    ForecastItem forecastItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        forecastItem = intent.getParcelableExtra(MainActivity.DATA);

        char degree = 0x00B0;
        TextView tvWhen = (TextView) findViewById(R.id.detailWhen);
        TextView tvTemperatureValue = (TextView) findViewById(R.id.detailTemperatureValue);
        TextView tvRealFeelValue = (TextView) findViewById(R.id.detailRealFeelValue);
        TextView tvCloudinessValue = (TextView) findViewById(R.id.detailCloudinessValue);
        TextView tvPrecipitationValue = (TextView) findViewById(R.id.detailPrecipitationValue);
        TextView tvWindValue = (TextView) findViewById(R.id.detailWindValue);
        TextView tvPressureValue = (TextView) findViewById(R.id.detailPressureValue);
        TextView tvWetnessValue = (TextView) findViewById(R.id.detailWetnessValue);

        String when = forecastItem.getDayTime() + " " + forecastItem.getDate();
        String temperature = forecastItem.getTemperature() + degree + "C";
        String realFeel = forecastItem.getRealFeel() + degree + "C";
        String cloudiness = forecastItem.getCloudiness();
        String precipitation = forecastItem.getPrecipitation() + " мм";
        String wind = forecastItem.getWindDirection() + "   " + forecastItem.getWindSpeed() + " м/с";
        String pressure = forecastItem.getPressure() + " мм рт. ст.";
        String wetness = forecastItem.getWetness() + " %";

        tvWhen.setText(when);
        tvTemperatureValue.setText(temperature);
        tvRealFeelValue.setText(realFeel);
        tvCloudinessValue.setText(cloudiness);
        tvPrecipitationValue.setText(precipitation);
        tvWindValue.setText(wind);
        tvPressureValue.setText(pressure);
        tvWetnessValue.setText(wetness);
    }
}
