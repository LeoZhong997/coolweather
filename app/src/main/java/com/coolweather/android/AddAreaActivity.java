package com.coolweather.android;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.coolweather.android.R;
import com.coolweather.android.util.BaseActivity;

public class AddAreaActivity extends BaseActivity implements ChooseAreaFragment.BackHandlerInterface{

    private ChooseAreaFragment chooseAreaFragment;

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
        setContentView(R.layout.activity_add_area);
    }

    @Override
    public void setSelectedFragment(ChooseAreaFragment chooseAreaFragment) {
        this.chooseAreaFragment = chooseAreaFragment;
    }

    @Override
    public void onBackPressed() {
        if (chooseAreaFragment == null || !chooseAreaFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
