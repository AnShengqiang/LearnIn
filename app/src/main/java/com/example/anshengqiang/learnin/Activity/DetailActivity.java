package com.example.anshengqiang.learnin.Activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.example.anshengqiang.learnin.Fragment.DetailFragment;

public class DetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_DETAIL = "com.example.anshengqiang.learnin.page_detail";
    private static final String EXTRA_CSS = "com.example.anshengqiang.learnin.css";
    private static final String EXTRA_IMAGE = "com.example.anshengqiang.learnin.image";

    public static Intent newIntent(Context context, String pageDetail, String css, String image){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_DETAIL, pageDetail);
        intent.putExtra(EXTRA_CSS, css);
        intent.putExtra(EXTRA_IMAGE, image);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        return new DetailFragment().newInstance(this.getIntent().getStringExtra(EXTRA_DETAIL),
                this.getIntent().getStringExtra(EXTRA_CSS),
                this.getIntent().getStringExtra(EXTRA_IMAGE));
    }
}
