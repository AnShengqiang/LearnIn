package com.example.anshengqiang.learnin.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.anshengqiang.learnin.libcore.MyDiskLruCache;
import com.example.anshengqiang.learnin.model.Essay;
import com.example.anshengqiang.learnin.model.EssayLab;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by anshengqiang on 2017/2/27.
 */

public class PagerContentFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ZHI_HU_LATEST = "http://news-at.zhihu.com/api/4/news/latest";
    private static final String ZHI_HU_FUN = "http://news-at.zhihu.com/api/3/section/2";
    private static final String ZHI_HU_STORY = "http://news-at.zhihu.com/api/3/section/29";
    private static final String ZHI_HU_NIGHT = "http://news-at.zhihu.com/api/3/section/1";

    private static final String TAG = "PagerContentFragment";

    private static final String ZHI_HU = "http://news-at.zhihu.com/api/4/news/";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private PosterImageDownloader<EssayListHolder> mPosterImageDownloader;
    private MyDiskLruCache mMyDiskLruCache;



    private void initThread() {

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

    public static PagerContentFragment newInstance(String s) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CATEGORY, s);

        PagerContentFragment pagerContentFragment = new PagerContentFragment();
        pagerContentFragment.setArguments(bundle);

        return pagerContentFragment;
    }

    /**
     * 获取essays数组，setAdapter()
     */
    private void updateUI() {
        EssayLab essayLab = EssayLab.get(getActivity());
        List<Essay> essays = essayLab.getEssays(getArguments().getString(ARG_CATEGORY));

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
        View v = inflater.inflate(R.layout.fragment_pager_content, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.tab_content_recycler_view);
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
            if (mEssays == null) {
                Log.i(TAG, "mEssays is null");
            }
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

            holder.bindTitle(essay);
            holder.bindEssay(essay);

            mPosterImageDownloader.queueImageDownloader(holder, essay.getImage());

        }

        @Override
        public int getItemCount() {
            if (mEssays != null) {
                return mEssays.size();
            }else {
                return 0;
            }
        }
    }

    public class FetchItemTask extends AsyncTask<Void, Void, List<Essay>> {
        List<Essay> mItems;


        @Override
        protected List<Essay> doInBackground(Void... params) {
            HexoFetchr fetchr = new HexoFetchr();
            Context context = getActivity().getApplicationContext();
            try {
                String category = getArguments().getString(ARG_CATEGORY);
                switch (category){
                    case "吐槽":
                        fetchr.fetchLatest(context, ZHI_HU_FUN, "吐槽");
                        mItems = fetchr.fetchDetail(context, ZHI_HU, "吐槽");
                        break;
                    case "惊奇":
                        fetchr.fetchLatest(context, ZHI_HU_NIGHT, "惊奇");
                        mItems = fetchr.fetchDetail(context, ZHI_HU, "惊奇");
                        break;
                    case "大误":
                        fetchr.fetchLatest(context, ZHI_HU_STORY, "大误");
                        mItems = fetchr.fetchDetail(context, ZHI_HU, "大误");
                        break;
                    default:
                        break;
                }
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
