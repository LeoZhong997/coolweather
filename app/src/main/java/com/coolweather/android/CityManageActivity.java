package com.coolweather.android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.coolweather.android.db.CountyLoaded;
import com.coolweather.android.util.BaseActivity;
import com.coolweather.android.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class CityManageActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CityManageActivity";

    private Toolbar toolbar;

    private RecyclerView manageRecycleView;

    private FloatingActionButton addFloatingButton;

    private List<CountyLoaded> countyLoadedList;

    private CityManageAdapter cityManageAdapter;

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
        setContentView(R.layout.activity_city_manage);

        Intent backIntent = getIntent();

        List<CountyLoaded> countyLoadedList= DataSupport.findAll(CountyLoaded.class);
        for (CountyLoaded countyLoaded : countyLoadedList) {
            LogUtil.d(TAG, countyLoaded.toString());
        }

        addFloatingButton = (FloatingActionButton) findViewById(R.id.add_floating_button);
        addFloatingButton.setOnClickListener(this);
        manageRecycleView = (RecyclerView) findViewById(R.id.manage_recycle_view);

        //填充列表
        countyLoadedList = DataSupport.findAll(CountyLoaded.class);
        if (countyLoadedList.size() > 0) {
            cityManageAdapter = new CityManageAdapter(countyLoadedList);
            manageRecycleView.setAdapter(cityManageAdapter);
            manageRecycleView.setLayoutManager(new LinearLayoutManager(this));
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallBack(cityManageAdapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(manageRecycleView);
        }

        //标题栏配置
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("城市管理");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_floating_button:
                Intent intent = new Intent(this, AddAreaActivity.class);
                startActivity(intent);
                break;
        }
    }
}
