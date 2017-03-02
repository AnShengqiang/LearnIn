package com.example.anshengqiang.learnin;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anshengqiang.learnin.fetchr.HexoFetchr;
import com.example.anshengqiang.learnin.model.Essay;
import com.example.anshengqiang.learnin.model.EssayLab;
import java.util.List;

/**
 * Created by anshengqiang on 2017/2/27.
 */

public class LearnInFragment extends Fragment {

    private static final String TAG = "LearnInFragment";

    private static final String ZHI_HU = "http://news-at.zhihu.com/api/4/news/";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public static LearnInFragment newInstance(){
        return new LearnInFragment();
    }

    /**
     * 获取essays数组，setAdapter()
     * */
    private void updateUI(){
        EssayLab essayLab = EssayLab.get(getActivity());
        List<Essay> essays = essayLab.getEssays();

        mAdapter = new EssayListAdapter(essays);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /*执行AsyncTask线程*/
        new FetchItemTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_learn_in, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.learnin_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }


    public class EssayListHolder extends RecyclerView.ViewHolder{
        private ImageView mTitleImageView;

        private TextView mTextView1;
        private TextView mTextView2;
        private TextView mTextView3;
        private TextView mTextView4;
        private TextView mTextView5;
        private TextView mTextView6;

        public EssayListHolder(View itemView) {
            super(itemView);
            mTitleImageView = (ImageView) itemView.findViewById(R.id.list_item_essay_image_view);

            mTextView1 = (TextView) itemView.findViewById(R.id.text_1);
            mTextView2 = (TextView) itemView.findViewById(R.id.text_2);
            mTextView3 = (TextView) itemView.findViewById(R.id.text_3);
            mTextView4 = (TextView) itemView.findViewById(R.id.text_4);
            mTextView5 = (TextView) itemView.findViewById(R.id.text_5);
            mTextView6 = (TextView) itemView.findViewById(R.id.text_6);

        }

        public void bindHolder(Essay essay){
       //     mTitleImageView.setImageResource(R.drawable.sea);

            mTextView1.setText(essay.getId().toString());
            mTextView2.setText(essay.getTitle());
            mTextView3.setText(essay.getDetail());
            mTextView4.setText(essay.getImage());
            mTextView5.setText(essay.getJsonId());
            mTextView6.setText(essay.getCss());

        }
    }

    public class EssayListAdapter extends RecyclerView.Adapter<EssayListHolder>{

        private List<Essay> mEssays;

        public EssayListAdapter(List<Essay> essays){
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
            holder.bindHolder(essay);
        }

        @Override
        public int getItemCount() {
            return mEssays.size();
        }
    }

    public class FetchItemTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            HexoFetchr fetchr = new HexoFetchr();
            fetchr.fetchLatest(ZHI_HU);
            fetchr.fetchDetail(ZHI_HU);
            return null;
        }
    }


}
