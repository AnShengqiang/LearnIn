package com.example.anshengqiang.learnin.Activity;

import android.support.v4.app.Fragment;

import com.example.anshengqiang.learnin.Fragment.LearnInFragment;

public class LearnInActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LearnInFragment.newInstance();
    }

}
