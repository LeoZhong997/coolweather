package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.coolweather.android.util.BaseActivity;
import com.coolweather.android.util.LogUtil;

public class MainActivity extends BaseActivity implements ChooseAreaFragment.BackHandlerInterface{

    private static final String TAG = "MainActivity";

    private ChooseAreaFragment chooseAreaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LogUtil.d(TAG, "onCreate: " + preferences.getString(WeatherActivity.WEATHER_INFO_BODY, null));
        //在程序一启动时就先从 SharedPreferences 文件读取缓存数据，如果不为 null 则说明之前已经请求过天气数据了，就可以跳过再次选择城市跳转到 WeatherActivity
        if (preferences.getString(WeatherActivity.WEATHER_INFO_BODY, null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("activity_name", "MainActivity");
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void setSelectedFragment(ChooseAreaFragment chooseAreaFragment) {
        this.chooseAreaFragment = chooseAreaFragment;
    }

    @Override
    public void onBackPressed() {
        if (chooseAreaFragment == null || !chooseAreaFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
