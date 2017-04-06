package com.example.anshengqiang.learnin.Fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anshengqiang.learnin.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by anshengqiang on 2017/3/22.
 */

public class MainFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mTabFragments;
    private List<String> mTabIndicators;
    private ContentPagerAdapter mContentPagerAdapter;

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.main_tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.main_view_pager);

        initTab();
        initPager();

        return view;
    }

    private void initTab(){
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        ViewCompat.setElevation(mTabLayout, 10);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initPager(){
        mTabIndicators = new ArrayList<>();
        mTabIndicators.add(getResources().getString(R.string.fun));
        mTabIndicators.add(getResources().getString(R.string.essay));
        mTabIndicators.add(getResources().getString(R.string.story));

        mTabFragments = new ArrayList<>();
        for (String s: mTabIndicators){
            mTabFragments.add(PagerContentFragment.newInstance(s));
        }

        mContentPagerAdapter = new ContentPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mContentPagerAdapter);
    }

    class ContentPagerAdapter extends FragmentPagerAdapter{

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mTabFragments.get(position);
        }

        @Override
        public int getCount() {
            return mTabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabIndicators.get(position);
        }
    }
}
