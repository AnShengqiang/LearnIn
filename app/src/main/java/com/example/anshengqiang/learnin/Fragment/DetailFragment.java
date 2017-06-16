package com.example.anshengqiang.learnin.Fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anshengqiang.learnin.R;
import com.example.anshengqiang.learnin.fetchr.PosterImageDownloader;
import com.example.anshengqiang.learnin.libcore.MyDiskLruCache;

/**
 * Created by anshengqiang on 2017/3/4.
 */

public class DetailFragment extends Fragment {
    private static final String ARG_DETAIL = "page_detail";
    private static final String ARG_CSS = "page_css";
    private static final String ARG_IMAGE = "page_image";
    private static final String ARG_TITLE = "page_title";

    private static final String TAG = "DetailFragment";

    private String mPageDetail;
    private String mPageCss;
    private WebView mWebView;
    private Toolbar mToolBar;
    private ImageView mImageView;
    private TextView mTextView;


    public static DetailFragment newInstance(String pageDetail,
                                             String css,
                                             String imageString,
                                             String titleString) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DETAIL, pageDetail);
        args.putSerializable(ARG_CSS, css);
        args.putSerializable(ARG_IMAGE, imageString);
        args.putSerializable(ARG_TITLE, titleString);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Handler responseHandler = new Handler();

        mPageDetail = getArguments().getSerializable(ARG_DETAIL).toString();
        mPageCss = getArguments().getSerializable(ARG_CSS).toString();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_detail, parent, false);


        mToolBar = (Toolbar) v.findViewById(R.id.fragment_detail_tool_bar);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mImageView = (ImageView) v.findViewById(R.id.detail_poster_image);
        mImageView.setImageBitmap(MyDiskLruCache.getCachedBitmap(getArguments().getString(ARG_IMAGE)));

        mTextView = (TextView) v.findViewById(R.id.detail_bar_title);
        Log.i(TAG, "接收到的title是： " + getArguments().getString(ARG_TITLE));
        mTextView.setText(getArguments().getString(ARG_TITLE));

        mWebView = (WebView) v.findViewById(R.id.fragment_detail_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });


        mPageCss = mPageCss.substring(1, mPageCss.length() - 2);
        mPageDetail = "<link rel=\"stylesheet\" href=" + mPageCss + " type=\"text/css\" />" + mPageDetail;
        mPageDetail = "<style>div.headline{display:none;}</style>" + mPageDetail;

        mWebView.loadDataWithBaseURL("", mPageDetail, "text/html; charset=utf-8", "utf-8", null);
        mWebView.setWebChromeClient(new WebChromeClient());

//        Log.i(TAG, "正文是：" + mPageDetail);

        return v;
    }



}
