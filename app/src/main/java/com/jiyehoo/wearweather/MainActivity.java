package com.jiyehoo.wearweather;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Basic;
import interfaces.heweather.com.interfacesmodule.bean.IndicesBean;
import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.IndicesType;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.base.Unit;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;


public class MainActivity extends WearableActivity implements View.OnClickListener {

    private TextView mTvTitle, mTvWeather, mTvDegree, mTvWindDir;
    private TextView mTvDailyWeather;
    private TextView mTvSuggest;
    private Button mBtnLocation, mBtnUIWeather, mBtnUIDaily, mBtnViewPager, mBtnSuggest;


    String TAG = "Weather";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        HeConfig.init("HE2012131458581972", "f0b559f4a0924536b80963469946c2ca");
        HeConfig.switchToDevService();

        bindView();

        getNowWeather();
        getDailyWeather3D();
        getSuggest();

    }

    private void getNowWeather() {
        /**
         * 实况天气数据
         * @param location 所查询的地区，可通过该地区名称、ID、IP和经纬度进行查询经纬度格式：纬度,经度
         *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
         * @param lang     (选填)多语言，可以不使用该参数，默认为简体中文
         * @param unit     (选填)单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问结果回调
         */
        HeWeather.getWeatherNow(MainActivity.this, "110,25", Lang.ZH_HANS, Unit.METRIC, new HeWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError" + throwable);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                Log.d(TAG, "Weather Now onSuccess JSON:" + new Gson().toJson(weatherNowBean));

                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(weatherNowBean.getCode())) {
                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    Basic basic = weatherNowBean.getBasic();
                    //更新时间
                    String updateTime = basic.getUpdateTime();
                    //天气
                    String weatherData = now.getText();
                    //温度
                    String degreeData = now.getTemp();
                    //风向
                    String windDirData = now.getWindDir();

                    mTvTitle.setText("观测时间：" + updateTime);
                    mTvWeather.setText("天气：" + weatherData);
                    mTvDegree.setText("温度：" + degreeData + "度");
                    mTvWindDir.setText("风向：" + windDirData);

                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d(TAG, "failed code: " + code);
                }

            }
        });
    }

    //获取天气预报
    private void getDailyWeather3D() {
        HeWeather.getWeather3D(MainActivity.this, "101300518", new HeWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "getDailyWeather onError" + throwable);
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                Log.d(TAG, "Weather Daily onSuccess:" + new Gson().toJson(weatherDailyBean));

                if (Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode())) {
                    List<WeatherDailyBean.DailyBean> dailyBeanList = weatherDailyBean.getDaily();
                    for (WeatherDailyBean.DailyBean dailyBean : dailyBeanList) {

                        String weather = dailyBean.getTextDay();
                        String data = dailyBean.getFxDate();
                        Log.d(TAG,  data + "预报:" + weather);

                        mTvDailyWeather.setText(mTvDailyWeather.getText() + data + "预报:" + weather + "\n");
                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d(TAG, "failed code: " + code);
                }

            }
        });
    }

    //获取建议
    private void getSuggest() {

        //需要的建议类型
        List<IndicesType> indicesTypeList = new ArrayList<>();
//        indicesTypeList.add(IndicesType.ALL);
        indicesTypeList.add(IndicesType.COMF);
        indicesTypeList.add(IndicesType.CW);

        HeWeather.getIndices1D(MainActivity.this, "101300518", Lang.ZH_HANS, indicesTypeList, new HeWeather.OnResultIndicesListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "suggest onError:" + throwable);
            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                Log.d(TAG, "suggest onSuccess:" + new Gson().toJson(indicesBean));

                if (Code.OK.getCode().equalsIgnoreCase(indicesBean.getCode())) {
                    List<IndicesBean.DailyBean> dailyBeanList = indicesBean.getDailyList();
                    for (IndicesBean.DailyBean dailyBean : dailyBeanList) {
                        String date = dailyBean.getDate();
                        String level = dailyBean.getLevel();
                        String name = dailyBean.getName();
                        String text = dailyBean.getText();

                        Log.d("生活指数date", date);
                        Log.d("生活指数name", name + ":" + level);
                        Log.d("生活指数text", text);

                        mTvSuggest.setText(mTvSuggest.getText().toString() + name + ":"
                                + level + "\n"
                                + text + "\n\n");

                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = indicesBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d(TAG, "suggest failed code: " + code);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_location_weather:
                intent = new Intent(MainActivity.this, LocationWeatherActivity.class);
                break;
            case R.id.btn_ui_weather:
                intent = new Intent(MainActivity.this, UIWeatherActivity.class);
                break;
            case R.id.btn_ui_daily:
                intent = new Intent(MainActivity.this, UIDailyActivity.class);
                break;
            case R.id.btn_ui_suggest:
                intent = new Intent(MainActivity.this, UISuggestActivity.class);
                break;
            case R.id.btn_ui_pager:
                intent = new Intent(MainActivity.this, UIPagerActivity.class);
                break;
        }
        startActivity(intent);
    }

    private void bindView() {
        mTvTitle = findViewById(R.id.tv_now_title);
        mTvWeather = findViewById(R.id.tv_now_weather);
        mTvDegree = findViewById(R.id.tv_now_degree);
        mTvWindDir = findViewById(R.id.tv_now_wind);
        mTvDailyWeather = findViewById(R.id.tv_daily);
        mTvSuggest = findViewById(R.id.tv_suggest);
        mBtnLocation = findViewById(R.id.btn_location_weather);
        mBtnUIWeather = findViewById(R.id.btn_ui_weather);
        mBtnUIDaily = findViewById(R.id.btn_ui_daily);
        mBtnSuggest = findViewById(R.id.btn_ui_suggest);
        mBtnViewPager = findViewById(R.id.btn_ui_pager);

        mBtnLocation.setOnClickListener(this);
        mBtnUIWeather.setOnClickListener(this);
        mBtnUIDaily.setOnClickListener(this);
        mBtnSuggest.setOnClickListener(this);
        mBtnViewPager.setOnClickListener(this);
    }
}