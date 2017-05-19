package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.CountyLoaded;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

/**
 * 解析和处理数据
 */
public class Utility {

    private static final String TAG = "Utility";

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    LogUtil.d(TAG, "province name: " + province.getProvinceName());
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的 JSON 数据解析成 Weather 实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            /* 先通过 JSONObject 和 JSONArray 将天气数据的主体内容解析出来 */
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Weather weather = new Gson().fromJson(weatherContent, Weather.class);      //调用 fromJson() 方法直接将 JSON 数据转换成 Weather 对象
            String weatherId = weather.basic.weatherId;
            String countyName = weather.basic.cityName;
            String weatherState = weather.now.more.info;
            String weatherDegree = weather.now.temperature + "℃";
            List<CountyLoaded> countyLoadedList = DataSupport.where("weatherId = ?", weatherId).find(CountyLoaded.class);
            if (countyLoadedList.size() > 0) {
                LogUtil.d(TAG, "countyLoadedList size " + countyLoadedList.size());
                CountyLoaded countyLoaded = new CountyLoaded();
                countyLoaded.setWeatherString(response);
                countyLoaded.setWeatherDegree(weatherDegree);
                countyLoaded.setWeatherState(weatherState);
                countyLoaded.updateAll("weatherId = ?", weatherId);
            } else {
                CountyLoaded countyLoaded = new CountyLoaded();
                countyLoaded.setCountyName(countyName);
                countyLoaded.setWeatherId(weatherId);
                countyLoaded.setWeatherDegree(weatherDegree);
                countyLoaded.setWeatherState(weatherState);
                countyLoaded.setWeatherString(response);
                countyLoaded.save();
            }
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
