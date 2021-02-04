package com.jiyehoo.wearweather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.jiyehoo.wearweather.R;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.IndicesBean;

/**
 * @author JiyeHoo
 * @date 20-12-15 下午5:16
 */
public class SuggestRootFragment extends Fragment {

    private ViewPager2 suggestViewPager;
//    private List<IndicesBean.DailyBean> dailyBeanList;
    private SuggestPagerFragment suggestPager1Fragment;
    private SuggestPagerFragment suggestPager2Fragment;
    private SuggestPagerFragment suggestPager3Fragment;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_root_suggest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //todo 绑定布局，设置监听
        mRadioGroup = view.findViewById(R.id.suggest_radio_group);
        mRadioButton1 = view.findViewById(R.id.rb_suggest_1);
        mRadioButton2 = view.findViewById(R.id.rb_suggest_2);
        mRadioButton3 = view.findViewById(R.id.rb_suggest_3);
        mRadioGroup.setOnCheckedChangeListener(new MyRadioGroupListener());

        suggestViewPager = view.findViewById(R.id.vp_suggest_root);

        suggestPager1Fragment = new SuggestPagerFragment();
        suggestPager2Fragment = new SuggestPagerFragment();
        suggestPager3Fragment = new SuggestPagerFragment();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(suggestPager1Fragment);
        fragmentList.add(suggestPager2Fragment);
        fragmentList.add(suggestPager3Fragment);

        suggestViewPager.setOffscreenPageLimit(3);
        suggestViewPager.setCurrentItem(0);
        suggestViewPager.setAdapter(new MyFragmentAdapter(getActivity().getSupportFragmentManager(), getLifecycle(), fragmentList));
        suggestViewPager.registerOnPageChangeCallback(new MyViewPagerChangeCallback());
    }

    public void update(List<IndicesBean.DailyBean> dailyBeanList) {
//        this.dailyBeanList = dailyBeanList;
        suggestPager1Fragment.update(dailyBeanList.get(2));
        suggestPager2Fragment.update(dailyBeanList.get(1));
        suggestPager3Fragment.update(dailyBeanList.get(0));
    }

    //radioGroup监听
    private class MyRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_suggest_1:
                    suggestViewPager.setCurrentItem(0);
                    break;
                case R.id.rb_suggest_2:
                    suggestViewPager.setCurrentItem(1);
                    break;
                case R.id.rb_suggest_3:
                    suggestViewPager.setCurrentItem(2);
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
                    mRadioGroup.check(R.id.rb_suggest_1);
                    break;
                case 1:
                    mRadioGroup.check(R.id.rb_suggest_2);
                    break;
                case 2:
                    mRadioGroup.check(R.id.rb_suggest_3);
                    break;
            }
        }
    }
}
