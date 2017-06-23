package com.coolweather.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.ActivityCollector;
import com.coolweather.android.util.BaseActivity;
import com.coolweather.android.util.DrawAQIBowView;
import com.coolweather.android.util.DrawSunBowView;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "WeatherActivity";
    private static final String NOTIFICATION_FLAG = "is_notification_on";

    private static final int APP_VERSION = 2;
    private static final int APP_SUB_VERSION = 1;

    private static final int NOTIFICATION_ID = 1;

    private int weatherIconId;
    private Weather mWeather;

    private static Drawable[] drawables = new Drawable[8];
    private boolean isNotificationOn = false;

    private String weatherShareString;

    public DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private NavigationView navigationView;

    public SwipeRefreshLayout swipeRefresh;

    private ImageView bingPicImg;

    private ScrollView weatherLayout;

    private ImageView weatherIconImageView;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private DrawAQIBowView drawAQIBowView;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView aqiQualityText;

    private TextView pm10Text;

    private TextView so2Text;

    private TextView no2Text;

    private TextView coText;

    private TextView o3Text;

    private TextView windDirText;

    private TextView windSpeedDegreeText;

    private TextView windSpeedText;

    private ImageView imageSmallView;

    private ImageView imageLargeView;

    private TextView humidity;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

//    private TextView airConditionText;

    private TextView dressText;

    private TextView fluText;

    private TextView travelText;

    private TextView sunscreenText;

    private TextView sunRiseText;

    private TextView sunSetText;

    private DrawSunBowView drawSunBowView;

    private Switch startNotificationBarSwitch;

    public static void actionStart(Context context, String weatherId, String activityName) {
        Intent intent = new Intent(context, WeatherActivity.class);
        intent.putExtra("weather_id", weatherId);
        intent.putExtra("activity_name", activityName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将背景图与状态栏融合在一起
        if (Build.VERSION.SDK_INT >= 21) {
            //当系统版本号>=21时，即5.0及以上系统才会执行
            View decorView = getWindow().getDecorView();                                                                            //取得当前活动的 DecorView
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);   //改变系统 UI 显示，传入的参数会使得活动的布局显示在状态栏的上方
            getWindow().setStatusBarColor(Color.TRANSPARENT);                                                                     //将状态栏设置为透明色
        }
        setContentView(R.layout.activity_weather);

        /* 初始化控件 */
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        weatherIconImageView = (ImageView) findViewById(R.id.weather_icon_image_view);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        drawAQIBowView = (DrawAQIBowView) findViewById(R.id.draw_aqi_bow_view);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        aqiQualityText = (TextView) findViewById(R.id.quality_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        pm10Text = (TextView) findViewById(R.id.pm10_text);
        so2Text = (TextView) findViewById(R.id.so2_text);
        no2Text = (TextView) findViewById(R.id.no2_text);
        coText = (TextView) findViewById(R.id.co_text);
        o3Text = (TextView) findViewById(R.id.o3_text);
        windDirText = (TextView) findViewById(R.id.wind_direction_text);
        windSpeedDegreeText = (TextView) findViewById(R.id.wind_speed_degree_text);
        windSpeedText = (TextView) findViewById(R.id.wind_speed_text);
        imageSmallView = (ImageView) findViewById(R.id.windmill_small_image);
        imageLargeView = (ImageView) findViewById(R.id.windmill_large_image);
        humidity = (TextView) findViewById(R.id.hum_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
//        airConditionText = (TextView) findViewById(R.id.air_condition);
        dressText = (TextView) findViewById(R.id.dress_suggestion);
        fluText = (TextView) findViewById(R.id.flu_index);
        travelText = (TextView) findViewById(R.id.travel_index);
        sunscreenText = (TextView) findViewById(R.id.sunscreen_index);
        sunRiseText = (TextView) findViewById(R.id.sun_rise_time_text);
        sunSetText = (TextView) findViewById(R.id.sun_set_time_text);
        drawSunBowView = (DrawSunBowView) findViewById(R.id.draw_sun_row_view);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);                             //设置下拉刷新进度条的颜色

        //ToolBar 初始化
        actionBarInit();

        //NavigationView 初始化
        navigationViewInit();

        initDrawables();

        //风车 svg 图初始化
        //windmillInit();

        //获取背景图
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this)
                    .load(bingPic)
                    .crossFade(1000)
                    .bitmapTransform(new BlurTransformation(this, 23, 4))                          // 设置高斯模糊，“23”：设置模糊度(在0.0到25.0之间)，默认"25"; "4":图片缩放比例,默认“1”
                    .into(bingPicImg);
        } else {
            loadBingPic();
        }

        //展示天气数据
        String activityName = getIntent().getStringExtra("activity_name");
        String weatherId = getIntent().getStringExtra("weather_id");;
        String weatherString;
        if (activityName.equals("AddAreaActivity") | activityName.equals("CityManageActivity")) {
            LogUtil.d(TAG, activityName);
            weatherLayout.setVisibility(View.INVISIBLE);                                        //隐藏 ScrollView
            //向网络请求最新的天气数据
            requestWeather(weatherId);
        } else if (activityName.equals("MainActivity")){
            weatherString = preferences.getString("weather", null);
            if (weatherString != null) {
                //有缓存时直接解析数据
                Weather weather = Utility.handleWeatherResponse(weatherString);
                showWeatherInfo(weather);
            } else {
                //无缓存时去服务器查询天气
                weatherLayout.setVisibility(View.INVISIBLE);                                     //隐藏 ScrollView
                requestWeather(weatherId);
            }
        }

        //下拉刷新监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeather();
            }
        });
    }

    private void initDrawables() {
        for (int i = 1; i <= 8; i++) {
            int id = getResources().getIdentifier("wm" + i, "drawable", getPackageName());
            drawables[i-1] = getDrawable(id);
        }
    }

    private void refreshWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherString = preferences.getString("weather", null);
        Weather weather = Utility.handleWeatherResponse(weatherString);
        String weatherNewId = weather.basic.weatherId;
        requestWeather(weatherNewId);
    }

    private void navigationViewInit() {
        navigationView.setCheckedItem(R.id.nav_city);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void actionBarInit() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
        }
    }

    private void windmillInit() {
        if (Build.VERSION.SDK_INT >= 25) {
            AnimatedVectorDrawable vectorSDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.windmill_vector_animator, null);
            AnimatedVectorDrawable vectorLDrawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.windmill_vector_animator, null);
            imageSmallView.setImageDrawable(vectorSDrawable);
            imageLargeView.setImageDrawable(vectorLDrawable);
            if (vectorSDrawable != null) {
                vectorSDrawable.start();
            }
            if (vectorLDrawable != null) {
                vectorLDrawable.start();
            }
        } else {
            Glide.with(this).load(R.drawable.windmill).into(imageLargeView);
            Glide.with(this).load(R.drawable.windmill).into(imageSmallView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.quit :
                ActivityCollector.finishAll();
                LogUtil.d(TAG, "You click quit");
                break;
            case R.id.share :
                shareWeather();
                break;
            case R.id.fresh :
                swipeRefresh.setRefreshing(true);
                refreshWeather();
                LogUtil.d(TAG, "You click fresh");
                break;
        }
        return true;
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "加载图片失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this)
                                .load(bingPic)
                                .crossFade(1000)
                                .bitmapTransform(new BlurTransformation(WeatherActivity.this, 23, 4))                          // “23”：设置模糊度(在0.0到25.0之间)，默认"25"; "4":图片缩放比例,默认“1”
                                .into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 根据天气 id 请求城市天气信息
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=3763dbce7be5488ebb14cde35213b557";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            Log.d(TAG, "save city");
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();          //在每次刷新天气信息时也刷新背景图片
    }

    /**
     * 处理并展示 Weather 实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        String currentDate;
        String sunRise = null;
        String sunSet = null;

        if (weather != null && "ok".equals(weather.status)) {
            mWeather = weather;

            String cityName = weather.basic.cityName;

            int weatherCode = Integer.parseInt(weather.now.more.weatherConditionCode);
            String weatherCodeStr = getString(R.string.weather_icon_id_text, weatherCode);
            weatherIconId = getResources().getIdentifier(weatherCodeStr, "drawable", this.getPackageName());
            if (weatherIconId != 0) {
                weatherIconImageView.setImageResource(weatherIconId);
            }
            Log.d(TAG, "showWeatherInfo: " + weatherCodeStr + " " + weatherIconId + " " + getResources().getResourceName(R.drawable.w100));

            currentDate = weather.basic.update.updateTime.split(" ")[0];
            String updateTime = weather.basic.update.updateTime.split(" ")[1];          //将格式为 2017-04-27 11:52 的通过分割，选择时间部分显示
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            String windDir = weather.now.wind.windDir;
            String windSpeedDegree = weather.now.wind.windSpeedDegree + "级";
            String hum = weather.now.humidity + "%";
            String windSpeed = weather.now.wind.windSpeed + "km/h";
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            windDirText.setText(windDir);
            windSpeedDegreeText.setText(windSpeedDegree);
            windSpeedText.setText(windSpeed);
            humidity.setText(hum);

            final AnimationDrawable animationLDrawable = new AnimationDrawable();
            final AnimationDrawable animationSDrawable = new AnimationDrawable();
            final int wsd = Integer.parseInt(weather.now.wind.windSpeed);
            if (wsd > 0) {
                int frameTime = 100 / wsd + 50;
                for (int i = 0; i < 8; i++) {
                    animationLDrawable.addFrame(drawables[i], frameTime);
                    animationSDrawable.addFrame(drawables[i], frameTime);
                }
                animationLDrawable.setOneShot(false);
                animationSDrawable.setOneShot(false);
                imageLargeView.setImageDrawable(animationLDrawable);
                animationLDrawable.start();
                imageSmallView.setImageDrawable(animationSDrawable);
                animationSDrawable.start();
            } else {
                imageLargeView.setImageDrawable(animationLDrawable);
                animationLDrawable.stop();
                imageSmallView.setImageDrawable(animationLDrawable);
                animationLDrawable.stop();
            }

            forecastLayout.removeAllViews();
            //动态加载 forecast_item.xml 布局并设置相应的数据，然后添加到父布局当中
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                if (forecast.date.equals(currentDate)) {
                    sunRise = forecast.astronomy.sunRise;
                    sunSet = forecast.astronomy.sunSet;
                }
                LogUtil.d(TAG, "showWeatherInfo: " + forecast.date + " " + forecast.more.info + " " + forecast.temperature.max + " " + forecast.temperature.min + " " + sunRise + " " + sunSet);
                forecastLayout.addView(view);
            }

            sunRiseText.setText("日出 " + sunRise);
            sunSetText.setText("日落 " + sunSet);
            drawSunBowView.setSunRise(sunRise);
            drawSunBowView.setSunSet(sunSet);
            drawSunBowView.setUpdate(updateTime);
            LogUtil.d(TAG, "sunRiseTime " + sunRise + " sunSetTime " + sunSet + " updateTime " + updateTime);
            drawSunBowView.start();

            if (weather.aqi != null) {
                drawAQIBowView.setAqiQuality(Integer.parseInt(weather.aqi.city.aqi));
                aqiText.setText(weather.aqi.city.aqi);
                aqiQualityText.setText(weather.aqi.city.quality);
                pm25Text.setText(weather.aqi.city.pm25);
                pm10Text.setText(weather.aqi.city.pm10);
                so2Text.setText(weather.aqi.city.so2);
                no2Text.setText(weather.aqi.city.no2);
                coText.setText(weather.aqi.city.co);
                o3Text.setText(weather.aqi.city.o3);

                //组装天气分享的内容
                weatherShareString = getString(R.string.weather_share_text, cityName, weatherInfo, degree, weather.aqi.city.quality, weather.basic.update.updateTime);
            }

            String comfort = "舒适指数： " + weather.suggestion.comfortIndex.brief + "\n" + weather.suggestion.comfortIndex.info;
            String carWash = "洗车指数： " + weather.suggestion.carWashIndex.brief + "\n" + weather.suggestion.carWashIndex.info;
            String sport = "运动指数： " + weather.suggestion.sportIndex.brief + "\n" + weather.suggestion.sportIndex.info;
//            String air = "空气指数： " + weather.suggestion.airIndex.brief + "\n" + weather.suggestion.airIndex.info;
            String dress = "穿衣指数： " + weather.suggestion.dressIndex.brief + "\n" + weather.suggestion.dressIndex.info;
            String flu = "流感指数： " + weather.suggestion.fluIndex.brief + "\n" + weather.suggestion.fluIndex.info;
            String travel = "旅行指数： " + weather.suggestion.travelIndex.brief + "\n" + weather.suggestion.travelIndex.info;
            String sunscreen = "防晒指数： " + weather.suggestion.sunscreenIndex.brief + "\n" + weather.suggestion.sunscreenIndex.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
//            airConditionText.setText(air);
            dressText.setText(dress);
            fluText.setText(flu);
            travelText.setText(travel);
            sunscreenText.setText(sunscreen);

            weatherLayout.setVisibility(View.VISIBLE);                          //设置 ScrollView 为可见

            //通知栏初始化
            boolean isOn = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).getBoolean(NOTIFICATION_FLAG, false);
            if (isOn) {
                createNotification();
            }

            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
            startService(intent);
        }else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_city:
                Intent cityIntent = new Intent(this, CityManageActivity.class);
                startActivity(cityIntent);
                break;
            case R.id.nav_theme:

                break;
            case R.id.nav_share:
                shareWeather();
                break;
            case R.id.nav_setting:
                View view = LayoutInflater.from(this).inflate(R.layout.setting_dialog, null);
                startNotificationBarSwitch = (Switch) view.findViewById(R.id.start_notification_bar_switch);
                boolean isOn = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).getBoolean(NOTIFICATION_FLAG, false);
                Log.d(TAG, "onNavigationItemSelected: isON " + isOn);
                startNotificationBarSwitch.setChecked(isOn);

                AlertDialog.Builder settingBuilder = new AlertDialog.Builder(this);
                settingBuilder.setTitle(getString(R.string.setting_title_text))
                        .setView(view)
                        .setIcon(getDrawable(R.drawable.ic_action_setting_nav))
                        .create()
                        .show();
                break;
            case R.id.nav_about:
                AlertDialog.Builder infoBuilder = new AlertDialog.Builder(this);
                infoBuilder.setTitle("版本信息：")
                        .setMessage(getString(R.string.debug_info, APP_VERSION, APP_SUB_VERSION, Build.VERSION.SDK_INT, getString(R.string.debug_bug_info)))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    public void onSwitchClicked(View view) {
        startNotificationBarSwitch = (Switch) view.findViewById(R.id.start_notification_bar_switch);

        switch (view.getId()) {
            case R.id.start_notification_bar_switch:
                if (startNotificationBarSwitch != null) {
                    Log.d(TAG, "onSwitchClicked: isChecked " + startNotificationBarSwitch.isChecked());
                    if (startNotificationBarSwitch.isChecked()) {
                        isNotificationOn = true;
                        createNotification();
                        Toast.makeText(this, "click on", Toast.LENGTH_SHORT).show();
                    } else {
                        isNotificationOn = false;
                        closeNotification();
                        Toast.makeText(this, "click off", Toast.LENGTH_SHORT).show();
                    }
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putBoolean(NOTIFICATION_FLAG, isNotificationOn);
                editor.apply();
                break;
        }
    }

    private void closeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    /**
     * 创建通知栏
     */
    private void createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        String contentText = getString(R.string.notification_weather_now_text, mWeather.now.more.info, mWeather.now.temperature + "℃",mWeather.aqi.city.quality + " " + getString(R.string.notification_refresh_time_text, mWeather.basic.update.updateTime.split(" ")[1]));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(mWeather.basic.cityName)
                .setContentText(contentText)
                .setContentIntent(pi)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_cloud)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_cloud))     //以上设置是为了兼容3.0之前版本
                .setContent(getRemoteViews())                                                                 //自定义通知栏view的api是在3.0以后
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 创建RemoteViews，提供给3.0版本之后的通知栏使用
     */
    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.weather_notification);

        if (weatherIconId != 0) {
            remoteViews.setImageViewResource(R.id.notification_weather_icon_image_view, weatherIconId);
        } else {
            remoteViews.setImageViewResource(R.id.notification_weather_icon_image_view, R.drawable.w999);
        }

        String weatherNowText = getString(R.string.notification_weather_now_text, mWeather.now.more.info, mWeather.now.temperature + "℃",mWeather.aqi.city.quality);
        remoteViews.setTextViewText(R.id.notification_now_text_view, weatherNowText);

        remoteViews.setTextViewText(R.id.notification_city_text_view, mWeather.basic.cityName);

        String weatherRefreshTime = mWeather.basic.update.updateTime.split(" ")[1];
        remoteViews.setTextViewText(R.id.notification_refresh_time_text_view, getString(R.string.notification_refresh_time_text, weatherRefreshTime));

        remoteViews.setOnClickPendingIntent(R.id.notification_layout, getClickPendingIntent());

        return remoteViews;
    }

    /**
     * 获取点击自定义通知栏上面的按钮或者视图时的延时意图
     */
    private PendingIntent getClickPendingIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        return pendingIntent;
    }

    private void shareWeather() {
        if (weatherShareString == null) {
            Toast.makeText(this, "天气信息获取不完整", Toast.LENGTH_SHORT).show();
        } else {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.weather_share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, weatherShareString);
            shareIntent = Intent.createChooser(shareIntent, getString(R.string.weather_share_subject));            startActivity(shareIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.finishAll();
    }
}
