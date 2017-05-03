package com.coolweather.android;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.WeatherWidgetService;
import com.coolweather.android.util.MyApplication;
import com.coolweather.android.util.Utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    private static final String TAG = "WeatherWidget";

//    private final Intent WEATHER_WIDGET_SERVICE = new Intent("android.appwidget.action.WEATHER_WIDGET_SERVICE");           //启动WeatherWidgetService对应的action

    private final String WEATHER_INFO_UPDATE = "android.appwidget.action.WEATHER_INFO_UPDATE";                           //更新widget的广播对应的action

    private static Set idsSet = new HashSet();

    private Weather weather;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }
        updateAppWidgets(context, appWidgetManager, idsSet);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled");
        //在第一个widget被创建时开启服务
//        context.startService(WEATHER_WIDGET_SERVICE);
        Intent intent = new Intent(context, WeatherWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled");
        //在最后一个widget被删除时关闭服务
//        context.stopService(WEATHER_WIDGET_SERVICE);
        Intent intent = new Intent(context, WeatherWidgetService.class);
        context.stopService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "onReceive: Action is " + action);
        if (WEATHER_INFO_UPDATE.equals(action)) {
            updateAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
        }
        super.onReceive(context, intent);
    }

    private void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        Log.d(TAG, "updateAppWidgets");

        int widgetId;

        Iterator it = set.iterator();               //迭代器，遍历所有保存的widget的id

        updateWeather();

        while (it.hasNext()) {
            widgetId = ((Integer)it.next()).intValue();

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            remoteView.setTextViewText(R.id.widget_city_text, weather.basic.cityName);

            remoteView.setTextViewText(R.id.widget_degree_text, weather.now.temperature);

            remoteView.setTextViewText(R.id.widget_pm25_text, weather.aqi.city.pm25);

            remoteView.setTextViewText(R.id.widget_aqi_text,weather.aqi.city.aqi);

            appWidgetManager.updateAppWidget(widgetId, remoteView);        //更新widget

        }
    }

    /**
     * 获得天气信息类
     */
    private void updateWeather() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String weatherString = preference.getString("weather", null);
        if (weatherString != null) {
            //有缓冲时直接解析数据
            weather = Utility.handleWeatherResponse(weatherString);
        }
    }
}

