package com.example.anshengqiang.learnin;

import android.support.v4.app.Fragment;
import android.os.Bundle;

public class LearnInActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return LearnInFragment.newInstance();
    }

}
