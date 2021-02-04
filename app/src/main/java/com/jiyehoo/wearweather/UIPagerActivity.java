package com.jiyehoo.wearweather;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.wear.ambient.AmbientModeSupport;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.google.gson.Gson;
import com.jiyehoo.wearweather.fragment.DailyFragment;
import com.jiyehoo.wearweather.fragment.MyFragmentAdapter;
import com.jiyehoo.wearweather.fragment.NowFragment;
import com.jiyehoo.wearweather.fragment.SuggestRootFragment;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.IndicesBean;
import interfaces.heweather.com.interfacesmodule.bean.air.AirNowBean;
import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.IndicesType;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class UIPagerActivity extends FragmentActivity implements AmbientModeSupport.AmbientCallbackProvider {

    private final String TAG = "###UIPagerActivity";

    private ViewPager2 viewPager2;
    private NowFragment nowFragment;
    private DailyFragment dailyFragment;
    private SuggestRootFragment suggestRootFragment;
    private RadioGroup mRadioGroupRoot;
    private RadioButton mRadioButtonNow, mRadioButtonDaily, mRadioButtonSuggest;

    private String location, nowWeather, nowTemp, nowTime;

    //定位服务类
    private AMapLocationClient locationClient = null;
    //定位服务参数
    private AMapLocationClientOption locationClientOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_i_pager);
        AmbientModeSupport.attach(this);

        initViewPager();

        //初始化定位
        locationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocation(true);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();

        getGps();
    }

    private void initViewPager() {
        mRadioGroupRoot = findViewById(R.id.root_radio_group);
        mRadioButtonNow = findViewById(R.id.radio_button_now);
        mRadioButtonDaily = findViewById(R.id.radio_button_daily);
        mRadioButtonSuggest = findViewById(R.id.radio_button_suggest);

        mRadioGroupRoot.setOnCheckedChangeListener(new MyRadioGroupListener());

        viewPager2 = findViewById(R.id.view_pager_2);
        nowFragment = new NowFragment();
        dailyFragment = new DailyFragment();
//        suggestFragment = new SuggestFragment();
        suggestRootFragment = new SuggestRootFragment();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(nowFragment);
        fragmentList.add(dailyFragment);
        fragmentList.add(suggestRootFragment);

        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setCurrentItem(0);
        viewPager2.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), getLifecycle(), fragmentList));
        viewPager2.registerOnPageChangeCallback(new MyViewPagerChangeCallback());
    }


    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return null;
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

    //获取实时天气
    private void getWeatherNow(String longAndLatitude) {
        HeWeather.getWeatherNow(UIPagerActivity.this, longAndLatitude, new HeWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(TAG, "onError" + throwable);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(weatherNowBean.getCode())) {

                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    String updateTime = weatherNowBean.getBasic().getUpdateTime();
                    //显示
                    nowWeather = now.getText();
                    nowTemp = now.getTemp();
                    String time = updateTime.replaceAll("T", " ");
                    time = time.substring(0, time.length() - 6).substring(11);
                    nowTime = time;

                    nowFragment.update(location, nowWeather, nowTemp, nowTime);
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d(TAG, "LocationWeather getWeather failed code: " + code);
                }
            }
        });
    }

    //获取空气质量
    private void getAir(String longAndLatitude) {
        HeWeather.getAirNow(UIPagerActivity.this, longAndLatitude, Lang.ZH_HANS, new HeWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d("LocationWeather Error", "onError" + throwable);
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                if (Code.OK.getCode().equalsIgnoreCase(airNowBean.getCode())) {
                    nowFragment.updateAir(airNowBean.getNow().getCategory());
                } else {
                    //在此查看返回数据失败的原因
                    String status = airNowBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.d(TAG, "LocationWeather getWeather failed code: " + code);
                }
            }
        });
    }

    //获取天气预报
    private void getDailyWeather3D(String longAndLatitude) {
        HeWeather.getWeather3D(UIPagerActivity.this, longAndLatitude, new HeWeather.OnResultWeatherDailyListener() {
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
                        dailyFragment.update(dailyBean);
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

    //获取生活建议
    private void getSuggestion(String longAndLatitude) {
        List<IndicesType> indicesTypeList = new ArrayList<>();
        //舒适度
        indicesTypeList.add(IndicesType.COMF);
        //穿衣指数
        indicesTypeList.add(IndicesType.DRSG);
        //运动指数
        indicesTypeList.add(IndicesType.SPT);
//        //化妆指数
//        indicesTypeList.add(IndicesType.MU);
//        //洗车指数
//        indicesTypeList.add(IndicesType.CW);
//        //旅游指数
//        indicesTypeList.add(IndicesType.TRAV);

        HeWeather.getIndices1D(this, longAndLatitude, Lang.ZH_HANS, indicesTypeList, new HeWeather.OnResultIndicesListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("Suggest", "获取建议信息失败");
            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                if (Code.OK.getCode().equalsIgnoreCase(indicesBean.getCode())) {

                    suggestRootFragment.update(indicesBean.getDailyList());

                } else {
                    String status = indicesBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.e("Suggest", "状态错误：" + code);
                }
            }
        });
    }

    private void getGps() {
        //获取定位
        locationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                Log.d(TAG, "access");
                //经度
                double longitude = aMapLocation.getLongitude();
                //纬度
                double latitude = aMapLocation.getLatitude();

                String longAndLatitude = longitude + "," + latitude;
                Log.d(TAG, longAndLatitude + aMapLocation.getDistrict());
                location = aMapLocation.getDistrict();
                //载入数据
                getWeatherNow(longAndLatitude);
                getAir(longAndLatitude);
                getDailyWeather3D(longAndLatitude);
                getSuggestion(longAndLatitude);

            } else {
                Log.d(TAG, "定位失败,"
                        + "ErrCode:" + aMapLocation.getErrorCode()
                        + ", errInfo:" + aMapLocation.getErrorInfo());
            }
        });
    }

    //radioGroup监听
    private class MyRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_button_now:
                    viewPager2.setCurrentItem(0);
                    break;
                case R.id.radio_button_daily:
                    viewPager2.setCurrentItem(1);
                    break;
                case R.id.radio_button_suggest:
                    viewPager2.setCurrentItem(2);
                    break;
            }
        }
    }

    //viewPager回调
    private class MyViewPagerChangeCallback extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            switch (position) {
                case 0:
                    mRadioGroupRoot.check(R.id.radio_button_now);
                    break;
                case 1:
                    mRadioGroupRoot.check(R.id.radio_button_daily);
                    break;
                case 2:
                    mRadioGroupRoot.check(R.id.radio_button_suggest);
                    break;
            }
        }
    }
}