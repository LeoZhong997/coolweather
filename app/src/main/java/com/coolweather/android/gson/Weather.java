package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

public class Weather {

    public String status;                   //返回的天气数据中还会包含一项 status 数据，成功返回 ok，失败则会返回具体的原因

    public Basic basic;                     //对 Basic 类进行引用

    public AQI aqi;                         //对 AQI 类进行引用

    public Now now;                         //对 Now 类进行引用

    public Suggestion suggestion;          //对 Suggestion 类进行引用

    /* 由于 JSON 中的一些字段可能不太适合直接作为 Java 字段来命名，因此使用 @SerializedName 注解的方式来让 JSON 字段和 Java 字段之间建立映射关系*/
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;   //使用 List 集合来引用 Forecast 类

}
