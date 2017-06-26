package com.coolweather.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
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
import android.view.ViewTreeObserver;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "WeatherActivity";
    private static final String NOTIFICATION_FLAG = "is_notification_on";
    private static final String NAV_BACKGROUND_IMAGE_STRING = "nav_background_image_string";
    private static final String NAV_PORTRAIT_IMAGE_STRING = "nav_portrait_image_string";
    private static final String BING_PICTURE_STRING = "bing_pic";
    public static final String WEATHER_INFO_BODY = "weather_info_body";

    private static final String STR_FILE_PROVIDER = "com.coolweather.android.fileprovider";

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CUT_PHOTO = 2;
    private static final int REQUEST_FROM_GALLERY = 3;

    private static final int APP_VERSION = 2;
    private static final int APP_SUB_VERSION = 1;

    private static final int NOTIFICATION_ID = 1;

    private static Drawable[] drawables = new Drawable[8];

    private int weatherIconId;

    private Uri imageUri;
    private Uri imageOutputUri;
    private Weather mWeather;
    private String weatherShareString;
    private String BACKGROUND_POPUP = "background_popup";
    private String PORTRAIT_POPUP = "portrait_popup";
    private String NAV_BACKGROUND_IMAGE_ADD = "nav_background_image.jpg";
    private String NAV_PORTRAIT_IMAGE_ADD = "nav_portrait_image.jpg";
    private int imageAspectX;
    private int imageAspectY;
    private String outputImagePopup;
    private boolean isNotificationOn = false;

    private SelectPicturePopupWindow mSelectPortraitPopupWindow;
    private SelectPicturePopupWindow mSelectBackgroundPopupWindow;

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
    private TextView dressText;
    private TextView fluText;
    private TextView travelText;
    private TextView sunscreenText;
    private TextView sunRiseText;
    private TextView sunSetText;
    private DrawSunBowView drawSunBowView;
    private Switch startNotificationBarSwitch;

    private ImageView navHeaderBackground;
    private CircleImageView navHeaderPortrait;

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

        //帧动画初始化
        initDrawables();

        //获取背景图
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = preferences.getString(BING_PICTURE_STRING, null);
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
        String weatherId = getIntent().getStringExtra("weather_id");
        String weatherString;
        if (activityName.equals("AddAreaActivity") | activityName.equals("CityManageActivity")) {
            LogUtil.d(TAG, activityName);
            weatherLayout.setVisibility(View.INVISIBLE);                                        //隐藏 ScrollView
            //向网络请求最新的天气数据
            requestWeather(weatherId);
        } else if (activityName.equals("MainActivity")){
            weatherString = preferences.getString(WEATHER_INFO_BODY, null);
            Log.d(TAG, "onCreate: weatherString " + weatherString);
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

    /**
     * 初始化风车帧动画
     */
    private void initDrawables() {
        for (int i = 1; i <= 8; i++) {
            int id = getResources().getIdentifier("wm" + i, "drawable", getPackageName());
            drawables[i-1] = getDrawable(id);
        }
    }

    /**
     * 刷新天气
     */
    private void refreshWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherString = preferences.getString(WEATHER_INFO_BODY, null);
        Weather weather = Utility.handleWeatherResponse(weatherString);
        String weatherNewId = weather.basic.weatherId;
        requestWeather(weatherNewId);
    }

    /**
     * 初始化侧边导航栏
     */
    private void navigationViewInit() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String backgroundImageString = sp.getString(NAV_BACKGROUND_IMAGE_STRING, null);
        String portraitImageString = sp.getString(NAV_PORTRAIT_IMAGE_STRING, null);
        final int[] backgroundWidth = new int[1];
        final int[] backgroundHeight = new int[1];
        final int[] portraitWidth = new int[1];
        final int[] portraitHeight = new int[1];

        navigationView.setCheckedItem(R.id.nav_city);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        navHeaderBackground = (ImageView) view.findViewById(R.id.nav_header_background);
        navHeaderPortrait = (CircleImageView) view.findViewById(R.id.nav_header_portrait);

        if (backgroundImageString != null) {
            Uri uri = Uri.parse(backgroundImageString);
            Bitmap bm = decodeUriAsBitmap(uri);
            navHeaderBackground.setImageBitmap(bm);
        }

        if (portraitImageString != null) {
            Uri uri = Uri.parse(portraitImageString);
            Bitmap bm = decodeUriAsBitmap(uri);
            navHeaderPortrait.setImageBitmap(bm);
        }

        //测量长宽
        navHeaderBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "navHeaderBackground width " + navHeaderBackground.getWidth() + " " + " height " + navHeaderBackground.getHeight());
                backgroundWidth[0] = navHeaderBackground.getWidth();
                backgroundHeight[0] = navHeaderBackground.getHeight();
                navHeaderBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        navHeaderPortrait.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "navHeaderPortrait width " + navHeaderPortrait.getWidth() + " " + "height " + navHeaderPortrait.getHeight());
                portraitWidth[0] = navHeaderPortrait.getWidth();
                portraitHeight[0] = navHeaderPortrait.getHeight();
                navHeaderPortrait.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        navHeaderBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "navHeaderBackground onClick");
                imageAspectX = backgroundWidth[0];
                imageAspectY = backgroundHeight[0];
                outputImagePopup = BACKGROUND_POPUP;
                if (mSelectBackgroundPopupWindow == null) {
                    mSelectBackgroundPopupWindow = new SelectPicturePopupWindow(WeatherActivity.this, new SelectPicturePopupWindow.PictureCallback() {
                        @Override
                        public void onTakePicture() {
                            startCamera(NAV_BACKGROUND_IMAGE_ADD);
                        }

                        @Override
                        public void onSelectFromGallery() {
                            startGallery();
                        }
                    });
                }
                mSelectBackgroundPopupWindow.show(v);
            }
        });

        navHeaderPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "navHeaderPortrait onClick");
                imageAspectX = portraitWidth[0];
                imageAspectY = portraitHeight[0];
                outputImagePopup = PORTRAIT_POPUP;
                if (mSelectPortraitPopupWindow == null) {
                    mSelectPortraitPopupWindow = new SelectPicturePopupWindow(WeatherActivity.this, new SelectPicturePopupWindow.PictureCallback() {
                        @Override
                        public void onTakePicture() {
                            startCamera(NAV_PORTRAIT_IMAGE_ADD);
                        }

                        @Override
                        public void onSelectFromGallery() {
                            startGallery();
                        }
                    });
                }
                mSelectPortraitPopupWindow.show(v);
            }
        });
    }

    /**
     * 侧边导航栏点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_city:
                Intent cityIntent = new Intent(this, CityManageActivity.class);
                startActivity(cityIntent);
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
                infoBuilder.setTitle(getString(R.string.debug_info_title))
                        .setMessage(getString(R.string.debug_info, APP_VERSION, APP_SUB_VERSION, Build.VERSION.SDK_INT, getString(R.string.debug_bug_info)))
                        .setPositiveButton(getString(R.string.confirm_text), new DialogInterface.OnClickListener() {
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

    /**
     * 启动照相机程序
     * @param outputFileName
     */
    private void startCamera(String outputFileName) {
        File outputImage = new File(getExternalCacheDir(), outputFileName);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            } else {
                outputImage.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(WeatherActivity.this, STR_FILE_PROVIDER, outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        imageOutputUri = Uri.fromFile(outputImage);

        //启动相机程序
        Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        in.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(in, REQUEST_TAKE_PHOTO);
    }

    /**
     * 启动相册
     */
    private void startGallery() {
        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, REQUEST_FROM_GALLERY);
    }

    /**
     * 处理其他Activity返回的结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_TAKE_PHOTO:
                    cutPhoto(imageAspectX, imageAspectY, imageAspectX, imageAspectY, imageUri, REQUEST_CUT_PHOTO);
                    break;
                case REQUEST_FROM_GALLERY:
                    Uri uri = data.getData();
                    if (null != uri) {
                        cutPhoto(imageAspectX, imageAspectY, imageAspectX, imageAspectY, uri, REQUEST_CUT_PHOTO);
                    }
                    break;
                case REQUEST_CUT_PHOTO:
                    if (null != data) {
                        LogUtil.d(TAG, "onActivityResult: " + data.getData().toString());
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        Bitmap bitmap = decodeUriAsBitmap(data.getData());
                        if (outputImagePopup.equals(PORTRAIT_POPUP)) {
                            editor.putString(NAV_PORTRAIT_IMAGE_STRING, data.getData().toString());
                            editor.apply();
                            navHeaderPortrait.setImageBitmap(bitmap);
                        } else {
                            editor.putString(NAV_BACKGROUND_IMAGE_STRING, data.getData().toString());
                            editor.apply();
                            navHeaderBackground.setImageBitmap(bitmap);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 将图片地址的Uri转成bitmap图片
     * @param uri
     * @return
     */
    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "decodeUriAsBitmap: file is not found");
            return null;
        }
        return bitmap;
    }

    /**
     * 裁剪图片
     * @param aspectX
     * @param aspectY
     * @param outputX
     * @param outputY
     * @param imageUri
     * @param requestCode
     */
    private void cutPhoto(int aspectX, int aspectY, int outputX, int outputY, Uri imageUri, int requestCode) {
        LogUtil.d(TAG, "cutPhoto has execute");
        //裁剪图片意图
        Intent in = new Intent("com.android.camera.action.CROP");
        in.setDataAndType(imageUri, "image/*")
                .putExtra("crop", "true")
                .putExtra("aspectX", aspectX)
                .putExtra("aspectY", aspectY)
                .putExtra("outputX", outputX)
                .putExtra("outputY", outputY)
                .putExtra("scale", true)
                .putExtra(MediaStore.EXTRA_OUTPUT, imageOutputUri)
                .putExtra("return-data", false)
                .putExtra("output-format", "PNG")
                .putExtra("noFaceDetection", true);

        in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(in, requestCode);
    }

    /**
     * 标题栏初始化
     */
    private void actionBarInit() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
        }
    }

    /**
     * 创建标题栏菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 标题栏菜单点击事件
     * @param item
     * @return
     */
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

    /**
     * 加载必应首页的图片
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, getString(R.string.fail_to_load_picture_text), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString(BING_PICTURE_STRING, bingPic);
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
        String weatherUrl = getString(R.string.weather_request_url_text, weatherId);                        //weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=3763dbce7be5488ebb14cde35213b557"
        LogUtil.d(TAG, "requestWeather: " + getString(R.string.weather_request_url_text, weatherId));
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, getString(R.string.fail_to_get_weather_info_text), Toast.LENGTH_SHORT).show();
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
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString(WEATHER_INFO_BODY, responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, getString(R.string.fail_to_get_weather_info_text), Toast.LENGTH_SHORT).show();
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
            String degree = getString(R.string.county_degree_text, weather.now.temperature);
            String weatherInfo = weather.now.more.info;
            String windDir = weather.now.wind.windDir;
            String windSpeedDegree = getString(R.string.county_wind_speed_degree_text, weather.now.wind.windSpeedDegree);
            String hum = weather.now.humidity + "%";
            String windSpeed = getString(R.string.county_wind_speed_text, weather.now.wind.windSpeed);
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

            sunRiseText.setText(getString(R.string.sun_rise_text, sunRise));
            sunSetText.setText(getString(R.string.sun_set_text, sunSet));
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

            String comfort = getString(R.string.comfort_text, weather.suggestion.comfortIndex.brief, weather.suggestion.comfortIndex.info);
            String carWash = getString(R.string.car_wash_text, weather.suggestion.carWashIndex.brief, weather.suggestion.carWashIndex.info);
            String sport = getString(R.string.sport_text, weather.suggestion.sportIndex.brief, weather.suggestion.sportIndex.info);
//            String air = "空气指数： " + weather.suggestion.airIndex.brief + "\n" + weather.suggestion.airIndex.info;
            String dress = getString(R.string.dress_text, weather.suggestion.dressIndex.brief, weather.suggestion.dressIndex.info);
            String flu = getString(R.string.flu_text, weather.suggestion.fluIndex.brief, weather.suggestion.fluIndex.info);
            String travel = getString(R.string.travel_text, weather.suggestion.travelIndex.brief, weather.suggestion.travelIndex.info);
            String sunscreen = getString(R.string.sun_screen_text, weather.suggestion.sunscreenIndex.brief, weather.suggestion.sunscreenIndex.info);
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
            Toast.makeText(this, getString(R.string.fail_to_get_weather_info_text), Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(this, getString(R.string.fail_to_get_weather_info_text), Toast.LENGTH_SHORT).show();
        } else {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.weather_share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT, weatherShareString);
            shareIntent = Intent.createChooser(shareIntent, getString(R.string.weather_share_subject));
            startActivity(shareIntent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.finishAll();
    }
}
