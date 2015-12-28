package com.example.ivansv.weatherforecast;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ivansv.weatherforecast.ListFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<ForecastItem> forecastItems;
    private final OnListFragmentInteractionListener interactionListener;

    public MyItemRecyclerViewAdapter(List<ForecastItem> forecastItems, OnListFragmentInteractionListener interactionListener) {
        this.forecastItems = forecastItems;
        this.interactionListener = interactionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        char degree = 0x00B0;
        holder.item = forecastItems.get(position);
        holder.dateTextView.setText(forecastItems.get(position).getDate());
        holder.dayTimeTextView.setText(forecastItems.get(position).getDayTime());
        String temperature = forecastItems.get(position).getTemperature() + degree + "C";
        holder.temperatureTextView.setText(temperature);
        switch (forecastItems.get(position).getCloudiness()) {
            case "ясно":
                holder.cloudinessImageView.setImageResource(R.drawable.one);
                break;
            case "малооблачно":
                holder.cloudinessImageView.setImageResource(R.drawable.two);
                break;
            case "облачно":
                holder.cloudinessImageView.setImageResource(R.drawable.four);
                break;
            case "пасмурно":
                holder.cloudinessImageView.setImageResource(R.drawable.six);
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != interactionListener) {
                    interactionListener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView dateTextView;
        public final TextView dayTimeTextView;
        public final TextView temperatureTextView;
        public final ImageView cloudinessImageView;
        public ForecastItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            dateTextView = (TextView) view.findViewById(R.id.date);
            dayTimeTextView = (TextView) view.findViewById(R.id.dayTime);
            temperatureTextView = (TextView) view.findViewById(R.id.temperature);
            cloudinessImageView = (ImageView) view.findViewById(R.id.cloudiness);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
