package com.example.anshengqiang.learnin.Fragment;

import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.anshengqiang.learnin.R;

/**
 * Created by anshengqiang on 2017/3/4.
 */

public class DetailFragment extends Fragment {
    private static final String ARG_DETAIL = "page_detail";
    private static final String ARG_CSS = "page_css";

    private static final String TAG = "DetailFragment";

    private String mPageDetail;
    private String mPageCss;
    private WebView mWebView;
    private ProgressBar mProgressBar;



    public static DetailFragment newInstance(String pageDetail, String css){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DETAIL, pageDetail);
        args.putSerializable(ARG_CSS, css);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mPageDetail = getArguments().getSerializable(ARG_DETAIL).toString();
        mPageCss = getArguments().getSerializable(ARG_CSS).toString();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_detail, parent, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_detail_progress_bar);
        mProgressBar.setMax(100);

        mWebView = (WebView) v.findViewById(R.id.fragment_detail_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView webView, int newProgress){
                if (newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            public void onReceivedTitle(WebView webView, String title){
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });


        mPageCss = mPageCss.substring(1, mPageCss.length() - 2);
        mPageDetail = "<link rel=\"stylesheet\" href=" + mPageCss + " type=\"text/css\" />" + mPageDetail;

        mWebView.loadDataWithBaseURL("",mPageDetail, "text/html", "utf-8", null);
        mWebView.setWebChromeClient(new WebChromeClient());

        Log.i(TAG, "正文是：" + mPageDetail);

        return v;
    }



}
