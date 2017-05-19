package com.coolweather.android;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZhiQiang on 2017/5/18.
 */

public class ChooseAreaAdapter extends ArrayAdapter<String> {

    private List<String> mCityList;

    private int mResourceId;

    public ChooseAreaAdapter(Context context, int textViewResourceId, List<String> cityList) {
        super(context, textViewResourceId, cityList);
        mResourceId = textViewResourceId;
        mCityList = cityList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(mResourceId, parent, false);
        TextView cityName = (TextView) view.findViewById(R.id.city_name);
        cityName.setText(mCityList.get(position));
        return view;
    }
}
