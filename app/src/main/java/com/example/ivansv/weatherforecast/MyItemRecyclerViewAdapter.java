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

    private final List<ForecastItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<ForecastItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        holder.mItem = mValues.get(position);
        holder.dateView.setText(mValues.get(position).date);
        holder.dayTimeView.setText(mValues.get(position).dayTime);
        String temperature = mValues.get(position).temperature + degree + "C";
        holder.temperatureView.setText(temperature);
        switch (mValues.get(position).getCloudiness()) {
            case "ясно":
                holder.cloudinessView.setImageResource(R.drawable.one);
                break;
            case "малооблачно":
                holder.cloudinessView.setImageResource(R.drawable.two);
                break;
            case "облачно":
                holder.cloudinessView.setImageResource(R.drawable.four);
                break;
            case "пасмурно":
                holder.cloudinessView.setImageResource(R.drawable.six);
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView dateView;
        public final TextView dayTimeView;
        public final TextView temperatureView;
        public final ImageView cloudinessView;
        public ForecastItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateView = (TextView) view.findViewById(R.id.date);
            dayTimeView = (TextView) view.findViewById(R.id.dayTime);
            temperatureView = (TextView) view.findViewById(R.id.temperature);
            cloudinessView = (ImageView) view.findViewById(R.id.cloudiness);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + dayTimeView.getText() + "'";
        }
    }
}
