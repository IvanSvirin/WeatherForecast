package com.example.ivansv.weatherforecast;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StartFragment extends Fragment {
    TextView placeTextView;
    TextView temperatureTextView;
    TextView cloudinessTextView;
    TextView precipitationTextView;
    TextView windTextView;
    TextView pressureTextView;
    TextView wetnessTextView;
    public StartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);
        placeTextView = (TextView) view.findViewById(R.id.placeTextView);
        temperatureTextView = (TextView) view.findViewById(R.id.temperatureValue);
        cloudinessTextView = (TextView) view.findViewById(R.id.cloudinessValue);
        precipitationTextView = (TextView) view.findViewById(R.id.precipitationValue);
        windTextView = (TextView) view.findViewById(R.id.windValue);
        pressureTextView = (TextView) view.findViewById(R.id.pressureValue);
        wetnessTextView = (TextView) view.findViewById(R.id.wetnessValue);

        placeTextView.setText(MainActivity.place.getName());
        temperatureTextView.setText(MainActivity.place.getForecastItem().getTemperature());
        cloudinessTextView.setText(MainActivity.place.getForecastItem().getCloudiness());
        precipitationTextView.setText(MainActivity.place.getForecastItem().getPrecipitation());
        windTextView.setText(MainActivity.place.getForecastItem().getWindSpeed());
        pressureTextView.setText(MainActivity.place.getForecastItem().getPressure());
        wetnessTextView.setText(MainActivity.place.getForecastItem().getWetness());

        return view;
    }

}
