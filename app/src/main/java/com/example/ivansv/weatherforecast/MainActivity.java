package com.example.ivansv.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListFragment.OnListFragmentInteractionListener, AsyncTaskListener {
    public static ArrayList<ForecastItem> items;
    private StartFragment startFragment = new StartFragment();
    private ListFragment listFragment = new ListFragment();
    public static final String DATA = "DATA";
    public static Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        place = new Place();
        new LoaderTask(this, this, items, place).execute();
    }

    public void update(View view) {
        items = new ArrayList<>();
        new LoaderTask(this, this, items, place).execute();
    }

    @Override
    public void onListFragmentInteraction(ForecastItem item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DATA, item);
        startActivity(intent);
    }

    @Override
    public void onAsyncTaskFinished() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainContainer, startFragment)
                .commit();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.mainContainer, listFragment)
//                .commit();
    }
}
