package com.jiyehoo.wearweather;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.IndicesBean;
import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.IndicesType;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class UISuggestActivity extends WearableActivity {

    private final String TAG = "###Suggest";
    private TextView mTvComfort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_i_suggest);
        // Enables Always-on
        setAmbientEnabled();

        bindView();
        getSuggestion("101300518");
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
        //化妆指数
        indicesTypeList.add(IndicesType.MU);
        //洗车指数
        indicesTypeList.add(IndicesType.CW);
        //旅游指数
        indicesTypeList.add(IndicesType.TRAV);

        HeWeather.getIndices1D(this, longAndLatitude, Lang.ZH_HANS, indicesTypeList, new HeWeather.OnResultIndicesListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("Suggest", "获取建议信息失败");
            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                if (Code.OK.getCode().equalsIgnoreCase(indicesBean.getCode())) {
                    //用于传给显示dialog
                    List<IndicesBean.DailyBean> dailyBeanList = indicesBean.getDailyList();

                    mTvComfort.setText(dailyBeanList.get(0).getText());

//                    for (IndicesBean.DailyBean dailyBean : dailyBeanList) {
//                        String name = dailyBean.getName();
//                        String text = dailyBean.getText();
//                        Log.d(TAG, name + text);
//                    }
                } else {
                    String status = indicesBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.e("Suggest", "状态错误：" + code);
                }
            }
        });
    }

    private void bindView() {
        mTvComfort = findViewById(R.id.tv_suggest_comfort);
    }
}