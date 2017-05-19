package com.coolweather.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.MyApplication;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ZhiQiang on 2017/4/26.
 */

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";         //调试用

    //定义回退按钮的回调方法和变量
    protected BackHandlerInterface mBackHandlerInterface;
    public interface BackHandlerInterface {
        void setSelectedFragment(ChooseAreaFragment chooseAreaFragment);
    }

    public static final int LEVEL_PROVINCE = 0;         //选择省份

    public static final int LEVEL_CITY = 1;             //选择城市

    public static final int LEVEL_COUNTY = 2;           //选择县

    private ProgressDialog progressDialog;               //进度框

    private TextView titleText;                          //标题控件

    private Button backButton;                           //返回按钮

    private ListView listView;                           //列表

    private ChooseAreaAdapter cityArrayAdapter;         //自定义适配器

    private List<String> dataList = new ArrayList<>();  //数据列表，作为适配器的数据源

    private List<Province> provinceList;                //省列表

    private List<City> cityList;                        //市列表

    private List<County> countyList;                    //县列表

    private Province selectedProvince;                 //选中的省份

    private City selectedCity;                          //选中的城市

    private County selectedCounty;                      //选中的的县

    private int currentLevel;                          //当前选中的级别

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //回调方法赋值
        if (getActivity() instanceof BackHandlerInterface) {
            mBackHandlerInterface = (BackHandlerInterface) getActivity();
        } else {
            throw new ClassCastException("Hosting Activity must implement BackHandlerInterface!");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //将自己的实例传出去
        mBackHandlerInterface.setSelectedFragment(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        /* 获取控件实例 */
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        cityArrayAdapter = new ChooseAreaAdapter(getActivity(), R.layout.main_list_item, dataList);
        listView.setAdapter(cityArrayAdapter);      //为 ListView  添加适配器
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* 设置 ListView 的点击事件 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);  //记录被点击的 Province
                    queryCities();                                       //进入查询城市的逻辑
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);           //记录被点击的 City
                    queryCounties();                                    //进入查询县的逻辑
                } else if (currentLevel == LEVEL_COUNTY) {
                    selectedCounty = countyList.get(position);
                    String weatherId = selectedCounty.getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        intent.putExtra("activity_name", "MainActivity");
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof AddAreaActivity) {
                        LogUtil.d(TAG, weatherId);
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        intent.putExtra("activity_name", "AddAreaActivity");
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            }
        });
        /* 设置 Button 的点击事件*/
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_PROVINCE) {
                    if (getActivity() instanceof AddAreaActivity) {
                        getActivity().onBackPressed();
                    }
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();           //初始化时默认加载省级数据
    }

    /**
     * 回退按钮处理逻辑
     */
    public boolean onBackPressed() {
        if (mBackHandlerInterface != null) {
            LogUtil.d(TAG, "catch backPressed event!");
            if (currentLevel == LEVEL_PROVINCE) {
                return false;
            } else if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
            return true;
        }
        return false;
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        if (getActivity() instanceof AddAreaActivity) {
            backButton.setVisibility(View.VISIBLE);
        } else {
            backButton.setVisibility(View.GONE);                    //将 Button 设置隐藏
        }
        provinceList = DataSupport.findAll(Province.class);     //调用 LitePal 的查询接口来从数据库中读取省级数据
        /* 如果读取到了数据，将数据显示在界面上，没有则调用 queryFromServer() 方法来从服务器上查询数据 */
        if (provinceList.size() > 0) {
            dataList.clear();                                    //清空 dataList
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());        //填充 dataList
            }
            cityArrayAdapter.notifyDataSetChanged();                //通知 ArrayAdapter 数据源已改变
            listView.setSelection(0);                            //定位到第一条
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";    //组装请求地址
            queryFromServer(address, "province");                  //向服务器请求查询数据
        }
    }

    /**
     * 查询选中的省里所有的市，优先从数据库查询，如果没有查到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);  //从数据库中通过 City 的 provinceid 值（这个值来自于已经确定了的 Province）来决定选择读取哪一个市级数据
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            cityArrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中的市里所有的县，优先从数据库查询，如果没有查到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);  //从数据库中通过 County 的 cityid 值（这个值来自于已经确定了的 City）来决定选择读取哪一个县级数据
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            cityArrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县的数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();                      //打开进度框
        /* 调用sendOkHttpRequest() 方法向服务器发起请求，响应的数据会回调到 onResponse() 方法中 */
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();      //关闭进度框
                        Toast.makeText(MyApplication.getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                /* 调用不同的方法来解析和处理服务器返回的数据，并存储到数据库中 */
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                /* 再次调用 queryXXX() 方法来重新加载数据 */
                if (result) {
                    /* 因为涉及到 UI 操作，所以必须将子线程切换到主线程*/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)){
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
