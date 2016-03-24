package com.example.ivansv.weatherforecast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class WeatherWidget extends AppWidgetProvider {
    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    private AlarmManager repeatServiceAlarmManager;
    private PendingIntent repeatServicePendingIntent;
    private PendingIntent restartServicePendingIntent;
    private AlarmManager restartServiceAlarmManager;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Intent repeatServiceIntent;
        repeatServiceAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        repeatServiceIntent = new Intent(context, UpdateService.class);
        repeatServicePendingIntent = PendingIntent.getService(context, 0, repeatServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        repeatServiceAlarmManager.cancel(repeatServicePendingIntent);
        repeatServiceAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 300 * 1000,
                300 * 1000, repeatServicePendingIntent);

        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Intent restartServiceIntent;
        final String action = intent.getAction();
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
            restartServicePendingIntent = PendingIntent.getService(context, 0, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            if (intent.getBooleanExtra(UpdateService.CONNECTION_STATE, false)) {
                restartServiceAlarmManager.cancel(restartServicePendingIntent);
                restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 900 * 1000, restartServicePendingIntent);

//                restartServiceAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 900 * 1000,
//                        restartServicePendingIntent);
            } else {
                restartServiceAlarmManager.cancel(restartServicePendingIntent);
                restartServiceAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60 * 1000, restartServicePendingIntent);

//                restartServiceAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60 * 1000,
//                        restartServicePendingIntent);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        context.stopService(intent);

        restartServiceAlarmManager.cancel(restartServicePendingIntent);
        repeatServiceAlarmManager.cancel(repeatServicePendingIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

