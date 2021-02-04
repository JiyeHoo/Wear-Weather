package com.jiyehoo.wearweather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import interfaces.heweather.com.interfacesmodule.bean.Basic;
import interfaces.heweather.com.interfacesmodule.bean.air.AirNowBean;
import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class UIWeatherActivity extends WearableActivity {

    private TextView mTvLocation, mTvWeather, mTvTemp, mTvAir, mTvTime;

    //定位服务类
    private AMapLocationClient locationClient = null;
    //定位服务参数
    private AMapLocationClientOption locationClientOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_i_weather);
        // Enables Always-on
        setAmbientEnabled();

        //申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        mTvLocation = findViewById(R.id.tv_ui_location);
        mTvWeather = findViewById(R.id.tv_ui_weather);
        mTvTemp = findViewById(R.id.tv_ui_temp);
        mTvAir = findViewById(R.id.tv_ui_air);
        mTvTime = findViewById(R.id.tv_ui_time);

        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        //获取定位
        locationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                Log.d("###UI", "access");
                //解析定位结果
                //经度
                double longitude = aMapLocation.getLongitude();
                //纬度
                double latitude = aMapLocation.getLatitude();
                //显示地点
                mTvLocation.setText(aMapLocation.getDistrict());

                //获取天气
                String longAndLatitude = longitude + "," + latitude;
                Log.d("###UI", aMapLocation.getDistrict());
                Log.d("###UI", longAndLatitude);
                getLocationWeatherNow(longAndLatitude);
                getAir(longAndLatitude);

            } else {
                Log.d("###UI", "定位失败,"
                        + "ErrCode:" + aMapLocation.getErrorCode()
                        + ", errInfo:" + aMapLocation.getErrorInfo());
            }
        });

        //设置定位参数
        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocation(true);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();

    }

    //获取实时天气
    private void getLocationWeatherNow(String longAndLatitude) {
        HeWeather.getWeatherNow(UIWeatherActivity.this, longAndLatitude, new HeWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d("LocationWeather Error", "onError" + throwable);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(weatherNowBean.getCode())) {

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    String updateTime = weatherNowBean.getBasic().getUpdateTime();
                    //显示
                    mTvWeather.setText(now.getText());
                    mTvTemp.setText(now.getTemp() + "°");
                    Log.d("###Time", updateTime);
                    String time = updateTime.replaceAll("T", " ");
                    time = time.substring(0, time.length() - 6).substring(11);
                    mTvTime.setText("观测时间:" + time);
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d("###UI_Error", "LocationWeather getWeather failed code: " + code);
                }
            }
        });
    }

    //获取空气质量
    private void getAir(String longAndLatitude) {
        HeWeather.getAirNow(UIWeatherActivity.this, longAndLatitude, Lang.ZH_HANS, new HeWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d("LocationWeather Error", "onError" + throwable);
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                if (Code.OK.getCode().equalsIgnoreCase(airNowBean.getCode())) {
                    Log.d("###UI", airNowBean.getNow().getCategory());
                    mTvAir.setText("空气" + airNowBean.getNow().getCategory());
                } else {
                    //在此查看返回数据失败的原因
                    String status = airNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d("###UI_Error", "LocationWeather getWeather failed code: " + code);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //给了权限
            } else {
                //没有权限
                Toast.makeText(this, "没有赋予定位权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy();
            locationClient = null;
            locationClientOption =null;
        }
    }
}