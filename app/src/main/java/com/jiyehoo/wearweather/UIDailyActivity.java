package com.jiyehoo.wearweather;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class UIDailyActivity extends WearableActivity {

    private LinearLayout mLinearLayout;
    private final String TAG = "###UIDailyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_i_daily);
        // Enables Always-on
        setAmbientEnabled();

        bindView();
        String location = "101300518";
        getDailyWeather3D(location);
    }

    private void bindView() {
        mLinearLayout = findViewById(R.id.ll_daily);
    }

    //获取天气预报
    private void getDailyWeather3D(String location) {
        HeWeather.getWeather3D(UIDailyActivity.this, location, new HeWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "getDailyWeather onError" + throwable);
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                Log.d(TAG, "Weather Daily onSuccess:" + new Gson().toJson(weatherDailyBean));

                mLinearLayout.removeAllViews();

                if (Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode())) {
                    List<WeatherDailyBean.DailyBean> dailyBeanList = weatherDailyBean.getDaily();
                    for (WeatherDailyBean.DailyBean dailyBean : dailyBeanList) {
                        showItem(dailyBean);
                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d(TAG, String.valueOf(code));
                }

            }
        });
    }

    //显示item
    private void showItem(WeatherDailyBean.DailyBean dailyBean) {
        String data = dailyBean.getFxDate();
        String weather = dailyBean.getTextDay();
        String degreeMin = dailyBean.getTempMin();
        String degreeMax = dailyBean.getTempMax();

        View view = LayoutInflater.from(this).inflate(R.layout.item_daily_layout, mLinearLayout, false);
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
        } else {
            mTvDate.setText(data.substring(5));
        }

        mLinearLayout.addView(view);
    }
}