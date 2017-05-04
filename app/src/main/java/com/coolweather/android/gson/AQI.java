package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

public class AQI {

    public AQICity city;

    public class AQICity {
        public String aqi;

        public String pm25;

        public String co;

        public String no2;

        public String o3;

        public String pm10;

        @SerializedName("qlty")
        public String quality;

        public String so2;
    }
}
