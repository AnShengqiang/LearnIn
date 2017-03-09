package com.example.anshengqiang.learnin.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anshengqiang.learnin.Activity.DetailActivity;
import com.example.anshengqiang.learnin.R;
import com.example.anshengqiang.learnin.fetchr.HexoFetchr;
import com.example.anshengqiang.learnin.fetchr.PosterImageDownloader;
import com.example.anshengqiang.learnin.libcore.DiskLruCache;
import com.example.anshengqiang.learnin.libcore.MyDiskLruCache;
import com.example.anshengqiang.learnin.model.Essay;
import com.example.anshengqiang.learnin.model.EssayLab;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by anshengqiang on 2017/2/27.
 */

public class LearnInFragment extends Fragment {

    private static final String TAG = "LearnInFragment";

    private static final String ZHI_HU = "http://news-at.zhihu.com/api/4/news/";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private PosterImageDownloader<EssayListHolder> mPosterImageDownloader;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private MyDiskLruCache mMyDiskLruCache;



    private void initThread() {

        /*String imageUrl = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";
        if (MyDiskLruCache.getCachedBitmap(getActivity().getApplication(), imageUrl) == null) {
            MyDiskLruCache.writeImageThread(getActivity().getApplication(), imageUrl);
        }*/

        /*执行AsyncTask线程*/
        new FetchItemTask().execute();

        /*实例化HandlerThread线程*/
        Handler responseHandler = new Handler();
        mPosterImageDownloader = new PosterImageDownloader<>(mMyDiskLruCache, responseHandler);

        /*实现HandlerThread中的接口，方法*/
        mPosterImageDownloader.setPosterImageDownloadListener(
                new PosterImageDownloader.PosterImageDownloadListener<EssayListHolder>() {
                    @Override
                    public void onPosterImageDownloaded(EssayListHolder target, Bitmap poster) {
                        Drawable drawable = new BitmapDrawable(getResources(), poster);
                        target.bindDrawable(drawable);
                    }
                }
        );

        mPosterImageDownloader.start();
        mPosterImageDownloader.getLooper();

        Log.i(TAG, "Handler Thread开始运行");
    }

    public static LearnInFragment newInstance() {
        return new LearnInFragment();
    }

    /**
     * 获取essays数组，setAdapter()
     */
    private void updateUI() {
        EssayLab essayLab = EssayLab.get(getActivity());
        List<Essay> essays = essayLab.getEssays();

        mAdapter = new EssayListAdapter(essays);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateUI(List<Essay> essays) {
        mAdapter = new EssayListAdapter(essays);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyDiskLruCache = new MyDiskLruCache(getActivity().getApplicationContext());
        initThread();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn_in, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.learnin_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mToolbar = (Toolbar) v.findViewById(R.id.learnin_tool_bar);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, R.string.hello, Snackbar.LENGTH_SHORT)
                        .setAction("cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //这里的单击事件代表点击消除Action后的响应事件

                            }
                        })
                        .show();
            }
        });
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)
                v.findViewById(R.id.learnin_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle("你好呀");
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);//设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色

        updateUI();

        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPosterImageDownloader.clearQueue();
    }


    public class EssayListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private ImageView mPostImageView;
        private Essay mEssay;

        public EssayListHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_title_text_view);
            mPostImageView = (ImageView) itemView.findViewById(R.id.list_item_essay_image_view);
            itemView.findViewById(R.id.list_item_essay_card_view).setOnClickListener(this);
        }

        public void bindTitle(Essay essay) {
            mTitleTextView.setText(essay.getTitle());
        }

        public void bindDrawable(Drawable drawable) {
            mPostImageView.setImageDrawable(drawable);

        }

        public void bindEssay(Essay essay) {
            mEssay = essay;
        }

        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(),
                    mEssay.getDetail(),
                    mEssay.getCss(),
                    mEssay.getImage(),
                    mEssay.getTitle());
            Log.i(TAG, "点击了一个卡片:" + mEssay.getTitle());
            startActivity(intent);
        }
    }

    public class EssayListAdapter extends RecyclerView.Adapter<EssayListHolder> {

        private List<Essay> mEssays;

        public EssayListAdapter(List<Essay> essays) {
            super();
            mEssays = essays;
        }

        @Override
        public EssayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View itemView = inflater.inflate(R.layout.list_item_essay, parent, false);

            return new EssayListHolder(itemView);
        }


        @Override
        public void onBindViewHolder(EssayListHolder holder, int position) {
            Essay essay = mEssays.get(position);

            //Drawable drawable = getResources().getDrawable(R.mipmap.header);
            holder.bindTitle(essay);
            //holder.bindDrawable(drawable);
            holder.bindEssay(essay);
//          MyDiskLruCache.writeImageThread(getActivity().getApplication(), "0");

            mPosterImageDownloader.queueImageDownloader(holder, essay.getImage());

        }

        @Override
        public int getItemCount() {
            return mEssays.size();
        }
    }

    public class FetchItemTask extends AsyncTask<Void, Void, List<Essay>> {
        List<Essay> mItems;


        @Override
        protected List<Essay> doInBackground(Void... params) {
            HexoFetchr fetchr = new HexoFetchr();
            Context context = getActivity().getApplicationContext();
            try {
                fetchr.fetchLatest(context, ZHI_HU);
                mItems = fetchr.fetchDetail(context, ZHI_HU);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mItems;
        }

        /*此方法接受doInBackground返回的参数，在线程完成的时候自动调用*/
        @Override
        protected void onPostExecute(List<Essay> essays) {
            mItems = essays;
            updateUI(mItems);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 此处清理AsyncTask
         * */

        mPosterImageDownloader.quit();
        Log.i(TAG, "Handler Thread结束运行");
    }


}
