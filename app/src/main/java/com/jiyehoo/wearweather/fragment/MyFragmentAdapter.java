package com.jiyehoo.wearweather.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;


/**
 * @author JiyeHoo
 * @date 20-12-15 下午3:49
 */
public class MyFragmentAdapter extends FragmentStateAdapter {

    private List<Fragment> fragmentList;

    public MyFragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, List<Fragment> fragmentList) {
        super(fragmentManager, lifecycle);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
