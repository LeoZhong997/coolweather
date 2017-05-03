package com.coolweather.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.coolweather.android.util.MyApplication;

public class WeatherWidgetService extends Service {

    private static final String TAG = "WeatherWidgetService";

    private final String WEATHER_INFO_UPDATE = "android.appwidget.action.WEATHER_INFO_UPDATE";   //更新widget的广播对应的action

    private static final int UPDATE_TIME = 86400000;                                                    //更新widget的时间间隔(ms)，"86400000"为1个小时,值小于30分钟时，会被设置为30分钟

    private Context mContext;

    private UpdateThread mUpdateThread;

    private int count = 0;                  //发送更新广播的次数

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        //启动线程
        mUpdateThread = new UpdateThread();
        mUpdateThread.start();

        mContext = MyApplication.getContext();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //结束线程
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
        }
        super.onDestroy();
    }

    public class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();

            try {
                count = 0;
                while (true) {
                    Log.d(TAG, "run: count is " + count);
                    count ++ ;
                    Intent updateIntent = new Intent(WEATHER_INFO_UPDATE);
                    mContext.sendBroadcast(updateIntent);

                    Thread.sleep(UPDATE_TIME);
                }
            } catch (InterruptedException e) {
                // 将 InterruptedException 定义在while循环之外，意味着抛出 InterruptedException 异常时，终止线程
                e.printStackTrace();
            }
        }
    }
}
