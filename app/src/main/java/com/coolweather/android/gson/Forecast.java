package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

public class Forecast {

    public String date;

    /* 由于 JSON 中的一些字段可能不太适合直接作为 Java 字段来命名，因此使用 @SerializedName 注解的方式来让 JSON 字段和 Java 字段之间建立映射关系*/
    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;

        public String min;

    }

    public class More {

        @SerializedName("txt_d")
        public String info;

    }
}
