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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import interfaces.heweather.com.interfacesmodule.bean.Basic;
import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class LocationWeatherActivity extends WearableActivity {

    private TextView mTvLongitude, mTvLatitude, mTvDistrict;
    private TextView mTvLocationWeather, mTvLocationDegree;

    //定位服务类
    private AMapLocationClient locationClient = null;
    //定位服务参数
    private AMapLocationClientOption locationClientOption = null;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_weather);
        // Enables Always-on
        setAmbientEnabled();

        //申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        mTvLongitude = findViewById(R.id.tv_location_longitude);
        mTvLatitude = findViewById(R.id.tv_location_latitude);
        mTvDistrict = findViewById(R.id.tv_location_district);
        mTvLocationWeather = findViewById(R.id.tv_location_weather);
        mTvLocationDegree = findViewById(R.id.tv_location_degree);

        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        //获取定位
        locationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                Log.d("###Location", "access");
                //解析定位结果
                //经度
                double longitude = aMapLocation.getLongitude();
                //纬度
                double latitude = aMapLocation.getLatitude();

                mTvLongitude.setText("经度:" + aMapLocation.getLongitude());
                mTvLatitude.setText("纬度:" + aMapLocation.getLatitude());
                mTvDistrict.setText("地点:" + aMapLocation.getDistrict());

                //获取天气
                String longAndLatitude = longitude + "," + latitude;
                getLocationWeatherNow(longAndLatitude);

            } else {
                Log.d("###Location", "失败");
                mTvLongitude.setText("定位失败,"
                        + "ErrCode:"
                        + aMapLocation.getErrorCode()
                        + ", errInfo:"
                        + aMapLocation.getErrorInfo());
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
        HeWeather.getWeatherNow(LocationWeatherActivity.this, longAndLatitude, new HeWeather.OnResultWeatherNowListener() {
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
                    Basic basic = weatherNowBean.getBasic();

                    //显示
                    mTvLocationWeather.setText("天气:" + now.getText());
                    mTvLocationDegree.setText("温度:" + now.getTemp());


                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d("LocationWeather Error", "LocationWeather getWeather failed code: " + code);
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