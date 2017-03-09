package com.example.anshengqiang.learnin.Activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.example.anshengqiang.learnin.Fragment.DetailFragment;

public class DetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_DETAIL = "com.example.anshengqiang.learnin.page_detail";
    private static final String EXTRA_CSS = "com.example.anshengqiang.learnin.css";
    private static final String EXTRA_IMAGE = "com.example.anshengqiang.learnin.image";
    private static final String EXTRA_TITLE = "com.example.anshengqiang.learnin.title";


    public static Intent newIntent(Context context, String pageDetail, String css, String image, String title){
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_DETAIL, pageDetail);
        intent.putExtra(EXTRA_CSS, css);
        intent.putExtra(EXTRA_IMAGE, image);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);



        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    @Override
    protected Fragment createFragment() {

        return new DetailFragment().newInstance(
                this.getIntent().getStringExtra(EXTRA_DETAIL),
                this.getIntent().getStringExtra(EXTRA_CSS),
                this.getIntent().getStringExtra(EXTRA_IMAGE),
                this.getIntent().getStringExtra(EXTRA_TITLE));
    }
}
