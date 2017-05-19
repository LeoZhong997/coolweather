package com.coolweather.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.CountyLoaded;

import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.List;

/**
 * Created by ZhiQiang on 2017/5/18.
 */

public class CityManageAdapter extends RecyclerView.Adapter<CityManageAdapter.ViewHolder> implements onMovedAndSwipedListener{

    private static final String TAG = "CityManageAdapter";

    private List<CountyLoaded> mCountyLoadedList;

    public CityManageAdapter(List<CountyLoaded> countyLoadedList) {
        mCountyLoadedList = countyLoadedList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements onStateChangeListener{
        View countyView;
        TextView capitalCountyName;
        TextView countyName;
        TextView countyWeatherState;
        TextView countyDegree;

        public ViewHolder(View itemView) {
            super(itemView);
            countyView = itemView;
            capitalCountyName = (TextView) itemView.findViewById(R.id.capital_county_name);
            countyName = (TextView) itemView.findViewById(R.id.county_name);
            countyWeatherState = (TextView) itemView.findViewById(R.id.county_weather);
            countyDegree = (TextView) itemView.findViewById(R.id.county_degree);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.countyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                CountyLoaded countyLoaded = mCountyLoadedList.get(position);
                String weatherId = countyLoaded.getWeatherId();
                Intent intent = new Intent(v.getContext(), WeatherActivity.class);
                intent.putExtra("weather_id", weatherId);
                intent.putExtra("activity_name", "CityManageActivity");
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CountyLoaded countyLoaded = mCountyLoadedList.get(position);
        holder.capitalCountyName.setText(countyLoaded.getCountyName().substring(0, 1));
        holder.countyName.setText(countyLoaded.getCountyName());
        holder.countyWeatherState.setText(countyLoaded.getWeatherState());
        holder.countyDegree.setText(countyLoaded.getWeatherDegree());
    }

    @Override
    public int getItemCount() {
        return mCountyLoadedList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        DataSupport.deleteAll(CountyLoaded.class, "weatherId = ?", mCountyLoadedList.get(position).getWeatherId());
        mCountyLoadedList.remove(position);
        notifyItemRemoved(position);
    }
}
