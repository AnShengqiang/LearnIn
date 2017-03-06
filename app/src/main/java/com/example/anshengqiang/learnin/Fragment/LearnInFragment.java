package com.example.anshengqiang.learnin.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
    private List<Essay> mEssays;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private PosterImageDownloader<EssayListHolder> mPosterImageDownloader;
    //private MyDiskLruCache mMyDiskLruCache;


    private void initThread() {
    /*执行AsyncTask线程*/
        new FetchItemTask().execute();

        /*实例化HandlerThread线程*/
        Handler responseHandler = new Handler();
        mPosterImageDownloader = new PosterImageDownloader<>(responseHandler);

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initThread();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn_in, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.learnin_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

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
                    mEssay.getCss());
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

            new MyDiskLruCache(getActivity(), essay.getImage());
            //Drawable drawable = getResources().getDrawable(R.mipmap.header);
            holder.bindTitle(essay);
            //holder.bindDrawable(drawable);
            holder.bindEssay(essay);
            mPosterImageDownloader.queueImageDownloader(holder, essay.getImage());

            //mMyDiskLruCache = new MyDiskLruCache(getActivity(), essay.getImage());
            //mMyDiskLruCache.execThread();

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
            try {
                fetchr.fetchLatest(getActivity().getApplicationContext(), ZHI_HU);
                mItems = fetchr.fetchDetail(getActivity().getApplicationContext(), ZHI_HU);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mItems;
        }

        /*此方法接受doInBackground返回的参数，在线程完成的时候自动调用*/
        @Override
        protected void onPostExecute(List<Essay> items) {
            mEssays = items;
            updateUI();
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
