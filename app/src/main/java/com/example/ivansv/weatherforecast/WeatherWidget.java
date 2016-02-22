package com.example.ivansv.weatherforecast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by ivansv on 12.02.2016.
 */
public class WeatherWidget extends AppWidgetProvider {
    private static PendingIntent restartServicePendingIntent;
    private static Intent restartServiceIntent;
    private static AlarmManager restartServiceAlarmManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        SharedPreferences sp = context.getSharedPreferences(
//                ConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE);
//        for (int id : appWidgetIds) {
//            updateWidget(context, appWidgetManager, sp, id);
//        }
//        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

//        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        final Calendar TIME = Calendar.getInstance();
//        TIME.set(Calendar.MINUTE, 0);
//        TIME.set(Calendar.SECOND, 0);
//        TIME.set(Calendar.MILLISECOND, 0);
//
////        final Intent updateServiceIntent = new Intent(context, UpdateService.class);
//        updateServiceIntent = new Intent(context, UpdateService.class);
//        if (updateServicePendingIntent == null) {
//            updateServicePendingIntent = PendingIntent.getService(context, 0, updateServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        }
//        alarmManager.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 60 * 1000, updateServicePendingIntent);

        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (action.equals(UpdateService.ACTION_RETRY)) {
            restartServiceAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            restartServiceIntent = new Intent(context, UpdateService.class);
            restartServicePendingIntent = PendingIntent.getService(context, 0, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            Toast.makeText(context, String.valueOf(intent.getBooleanExtra(UpdateService.CONNECTION_STATE, true)), Toast.LENGTH_SHORT).show();
            if (intent.getBooleanExtra(UpdateService.CONNECTION_STATE, false)) {
                restartServiceAlarmManager.cancel(restartServicePendingIntent);
                restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60 * 1000, restartServicePendingIntent);
            } else {
                restartServiceAlarmManager.cancel(restartServicePendingIntent);
                restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 30 * 1000, restartServicePendingIntent);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
//        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(updateServicePendingIntent);
//        final Intent updateServiceIntent = new Intent(context, UpdateService.class);
//        context.stopService(updateServiceIntent);

        Intent intent = new Intent(context, UpdateService.class);
        context.stopService(intent);
        restartServiceAlarmManager.cancel(restartServicePendingIntent);
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

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp,
                             int appWidgetId) {
        String placeName = sp.getString(ConfigureActivity.WIDGET_PLACE_NAME, null);
        String temperature = sp.getString(ConfigureActivity.WIDGET_TEMPERATURE, null);
        String pressure = sp.getString(ConfigureActivity.WIDGET_PRESSURE, null);
        String wind = sp.getString(ConfigureActivity.WIDGET_WIND, null);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_new);
        views.setTextViewText(R.id.name, placeName);
        views.setTextViewText(R.id.temperature, temperature);
        views.setTextViewText(R.id.pressure, pressure);
        views.setTextViewText(R.id.wind, wind);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}

