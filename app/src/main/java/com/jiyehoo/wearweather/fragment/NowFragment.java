package com.jiyehoo.wearweather.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jiyehoo.wearweather.R;


/**
 * @author JiyeHoo
 * @date 20-12-15 下午3:31
 * 当前天气
 */
public class NowFragment extends Fragment {
    private final String TAG = "###NowFragment";

    private TextView mTvLocation, mTvWeather, mTvTemp, mTvTime, mTvAir;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvLocation = view.findViewById(R.id.tv_vp_now_location);
        mTvWeather = view.findViewById(R.id.tv_vp_now_weather);
        mTvTemp = view.findViewById(R.id.tv_vp_now_temp);
        mTvTime = view.findViewById(R.id.tv_vp_now_time);
        mTvAir = view.findViewById(R.id.tv_vp_now_air);
    }


    @SuppressLint("SetTextI18n")
    public void update(String location, String weather, String temp, String time) {
        mTvLocation.setText(location);
        mTvWeather.setText(weather);
        mTvTemp.setText(temp + "°");
        mTvTime.setText("观测时间:" + time);
    }

    public void updateAir(String air) {
        mTvAir.setText("空气" + air);
    }

}
