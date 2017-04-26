package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

public class County extends DataSupport {

    private int id;

    private String countyName;   //县的名字

    private int weatherId;      //县对应的天气id

    private int cityId;         //县当前所属市的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
