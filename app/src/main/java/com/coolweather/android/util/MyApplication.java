package com.coolweather.android.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

/**
 * 用于管理程序内一些全局的状态信息
 * 比如获得全局的Context
 */
public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();          //获得一个应用程序级别的Context
        LitePalApplication.initialize(mContext);     //LitePal初始化
    }

    /**
     * 静态方法
     * 返回获得的全局Context
     * @return
     */
    public static Context getContext() {
        return mContext;
    }
}
