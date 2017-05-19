package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ZhiQiang on 2017/5/18.
 */

public class CountyLoaded extends DataSupport{
    private int id;

    private String countyName;

    private String weatherId;

    private String weatherString;

    private String weatherState;

    private String weatherDegree;

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

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherString() {
        return weatherString;
    }

    public void setWeatherString(String weatherString) {
        this.weatherString = weatherString;
    }

    public String getWeatherState() {
        return weatherState;
    }

    public void setWeatherState(String weatherState) {
        this.weatherState = weatherState;
    }

    public String getWeatherDegree() {
        return weatherDegree;
    }

    public void setWeatherDegree(String weatherDegree) {
        this.weatherDegree = weatherDegree;
    }

    @Override
    public String toString() {
        return "CountyLoaded id " + id + ", countyName " + countyName + ", weatherId " + weatherId + ", weatherString " + weatherString;
    }
}
