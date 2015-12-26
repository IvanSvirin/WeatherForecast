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

        tvWhen.setText(forecastItem.getDayTime() + " " + forecastItem.getDate());
        tvTemperatureValue.setText(forecastItem.getTemperature() + degree + "C");
        tvRealFeelValue.setText(forecastItem.getRealFeel() + degree + "C");
        tvCloudinessValue.setText(forecastItem.getCloudiness());
        tvPrecipitationValue.setText(forecastItem.getPrecipitation() + " мм");
        tvWindValue.setText(forecastItem.getWindDirection() + "   " + forecastItem.getWindSpeed() + " м/с");
        tvPressureValue.setText(forecastItem.getPressure() + " мм рт. ст.");
        tvWetnessValue.setText(forecastItem.getWetness() + " %");
    }
}
