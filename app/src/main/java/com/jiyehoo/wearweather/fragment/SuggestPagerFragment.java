package com.jiyehoo.wearweather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jiyehoo.wearweather.R;

import interfaces.heweather.com.interfacesmodule.bean.IndicesBean;

/**
 * @author JiyeHoo
 * @date 20-12-15 下午5:16
 */
public class SuggestPagerFragment extends Fragment {

    private TextView mTvTitle;
    private EditText mTvText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //todo 绑定布局，设置监听
        mTvTitle = view.findViewById(R.id.tv_vp_suggest_title);
        mTvText = view.findViewById(R.id.tv_vp_suggest_text);
    }

    public void update(IndicesBean.DailyBean dailyBean) {
        mTvTitle.setText(dailyBean.getName());
        mTvText.setText(dailyBean.getText());
    }
}
