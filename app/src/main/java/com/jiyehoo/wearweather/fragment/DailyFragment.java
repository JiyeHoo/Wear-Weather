package com.jiyehoo.wearweather.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jiyehoo.wearweather.R;
import com.jiyehoo.wearweather.UIDailyActivity;
import com.jiyehoo.wearweather.UIPagerActivity;

import java.util.Calendar;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

/**
 * @author JiyeHoo
 * @date 20-12-15 下午3:35
 * 天气预报
 */
public class DailyFragment extends Fragment {

    private final String TAG = "###DailyFragment";
    private WeatherDailyBean.DailyBean dailyBean;
    private LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayout = view.findViewById(R.id.ll_vp_daily);
    }

    public void update(WeatherDailyBean.DailyBean dailyBean) {
        this.dailyBean = dailyBean;
        showItem(dailyBean);
    }

    //显示item
    @SuppressLint("SetTextI18n")
    private void showItem(WeatherDailyBean.DailyBean dailyBean) {
        String data = dailyBean.getFxDate();
        String weather = dailyBean.getTextDay();
        String degreeMin = dailyBean.getTempMin();
        String degreeMax = dailyBean.getTempMax();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_daily_layout, linearLayout, false);
        TextView mTvDate = view.findViewById(R.id.tv_daily_date);
        TextView mTvWeather = view.findViewById(R.id.tv_daily_weather);
        TextView mTvTemp = view.findViewById(R.id.tv_daily_temp);
        ImageView mIvPic = view.findViewById(R.id.iv_daily_pic);

        //显示天气
        mTvWeather.setText(weather);
        mTvTemp.setText(degreeMin + "°~" + degreeMax + "°");
        switch (weather) {
            case "中雨":
            case "大雨":
            case "暴雨":
                Glide.with(this).load(R.drawable.dayu).into(mIvPic);
                break;
            case "小雨":
            case "阵雨":
                Glide.with(this).load(R.drawable.xiaoyu).into(mIvPic);
                break;
            case "阴":
                Glide.with(this).load(R.drawable.yin).into(mIvPic);
                break;
            case "晴":
                Glide.with(this).load(R.drawable.qing).into(mIvPic);
                break;
            case "雷阵雨":
            case "雷雨":
            case "闪电":
                Glide.with(this).load(R.drawable.shandian).into(mIvPic);
                break;
            case "雪":
            case "大雪":
            case "小雪":
                Glide.with(this).load(R.drawable.xue).into(mIvPic);
                break;
            case "多云":
            default:
                Glide.with(this).load(R.drawable.duoyun).into(mIvPic);
                break;
        }

        //显示日期
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (data.substring(8).equals(String.valueOf(day))) {
            mTvDate.setText("今天");
        } else if (data.substring(8).equals(String.valueOf(day + 1))) {
            mTvDate.setText("明天");
        } else if (data.substring(8).equals(String.valueOf(day + 2))) {
            mTvDate.setText("后天");
        } else if (data.substring(8).equals(String.valueOf(day - 1))) {
            mTvDate.setText("昨天");
        } else {
            mTvDate.setText(data.substring(5));
        }

        linearLayout.addView(view);
    }
}
