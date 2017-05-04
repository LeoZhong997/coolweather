package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

public class Suggestion {

    /* 由于 JSON 中的一些字段可能不太适合直接作为 Java 字段来命名，因此使用 @SerializedName 注解的方式来让 JSON 字段和 Java 字段之间建立映射关系*/
    @SerializedName("comf")
    public ComfortIndex comfortIndex;

    @SerializedName("cw")
    public CarWashIndex carWashIndex;

    @SerializedName("sport")
    public SportIndex sportIndex;

    @SerializedName("air")
    public AirIndex airIndex;

    @SerializedName("drsg")
    public DressIndex dressIndex;

    @SerializedName("flu")
    public FluIndex fluIndex;

    @SerializedName("trav")
    public TravelIndex travelIndex;

    @SerializedName("uv")
    public SunscreenIndex sunscreenIndex;

    public class AirIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class DressIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class FluIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class TravelIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class SunscreenIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class ComfortIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class CarWashIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }

    public class SportIndex {
        @SerializedName("brf")
        public String brief;

        @SerializedName("txt")
        public String info;
    }
}
