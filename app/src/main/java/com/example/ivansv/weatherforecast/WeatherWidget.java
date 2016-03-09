package com.example.ivansv.weatherforecast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class WeatherWidget extends AppWidgetProvider {
    private static PendingIntent restartServicePendingIntent;
    private static AlarmManager restartServiceAlarmManager;
    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Intent restartServiceIntent;
        if (action.equals(BOOT_ACTION)) {
            restartServiceAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            restartServiceIntent = new Intent(context, UpdateService.class);
            restartServicePendingIntent = PendingIntent.getService(context, 0, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            restartServiceAlarmManager.cancel(restartServicePendingIntent);
            restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60 * 1000, restartServicePendingIntent);
        }
        if (action.equals(UpdateService.ACTION_RETRY)) {
            restartServiceAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            restartServiceIntent = new Intent(context, UpdateService.class);
            restartServicePendingIntent = PendingIntent.getService(context, 0, restartServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (intent.getBooleanExtra(UpdateService.CONNECTION_STATE, false)) {
                restartServiceAlarmManager.cancel(restartServicePendingIntent);
                restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 900 * 1000, restartServicePendingIntent);
            } else {
                restartServiceAlarmManager.cancel(restartServicePendingIntent);
                restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60 * 1000, restartServicePendingIntent);
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
        Intent intent = new Intent(context, UpdateService.class);
        context.stopService(intent);
        restartServiceAlarmManager.cancel(restartServicePendingIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

