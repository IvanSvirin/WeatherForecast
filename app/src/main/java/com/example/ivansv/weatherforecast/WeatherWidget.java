package com.example.ivansv.weatherforecast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by ivansv on 12.02.2016.
 */
public class WeatherWidget extends AppWidgetProvider {
    private PendingIntent updateService = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        SharedPreferences sp = context.getSharedPreferences(
//                ConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE);
//        for (int id : appWidgetIds) {
//            updateWidget(context, appWidgetManager, sp, id);
//        }
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent i = new Intent(context, UpdateService.class);

        if (updateService == null) {
            updateService = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, updateService);
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp,
                             int appWidgetId) {
        String placeName = sp.getString(ConfigureActivity.WIDGET_PLACE_NAME, null);
        String temperature = sp.getString(ConfigureActivity.WIDGET_TEMPERATURE, null);
        String pressure = sp.getString(ConfigureActivity.WIDGET_PRESSURE, null);
        String wind = sp.getString(ConfigureActivity.WIDGET_WIND, null);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        views.setTextViewText(R.id.name, placeName);
        views.setTextViewText(R.id.temperature, temperature);
        views.setTextViewText(R.id.pressure, pressure);
        views.setTextViewText(R.id.wind, wind);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        m.cancel(updateService);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
//        SharedPreferences.Editor editor = context.getSharedPreferences(
//                ConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
//        for (int widgetID : appWidgetIds) {
//            editor.remove(ConfigureActivity.WIDGET_PLACE_NAME);
//            editor.remove(ConfigureActivity.WIDGET_TEMPERATURE);
//            editor.remove(ConfigureActivity.WIDGET_PRESSURE);
//            editor.remove(ConfigureActivity.WIDGET_WIND);
//        }
//        editor.commit();
    }
}

