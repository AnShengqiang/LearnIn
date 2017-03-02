package com.example.anshengqiang.learnin.fetchr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by anshengqiang on 2017/3/2.
 */

public class PosterImageDownloader<T> extends HandlerThread {

    private static final String TAG = "PosterImageDownloader";

    private static final int MESSAGE_DOWNLOAD = 0;

    private Boolean mHasQuit = false;
    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private PosterImageDownloadListener<T> mPosterImageDownloadListener;

    public interface PosterImageDownloadListener<T>{
        void onPosterImageDownloaded(T target, Bitmap poster);
    }

    public void setPosterImageDownloadListener(PosterImageDownloadListener<T> listener){
        mPosterImageDownloadListener = listener;
    }

    public PosterImageDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    /**
     * 此方法在Looper首次检查消息队列之前调用
     * 创建handler的实现
     * 处理对应的消息
     * */
    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "looper获取到一个message， 请求链接： " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit(){
        mHasQuit = true;
        return super.quit();
    }



    /**
     * 在hashmap中绑定url和EssayHolder
     * 从消息循环池中取出一个Message，发送给Handler
     * */
    public void queueImageDownloader(T target, String url){
        Log.i(TAG, "got a url" + url);

        if (url == null){
            mRequestMap.remove(target);
        }else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }



    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    /**
     * 下载图片
     * */
    private void handleRequest(final T target){

        try {
            final String url = mRequestMap.get(target);

            if (url == null){
                return;
            }

            byte[] bitmapBytes = new HexoFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "图片下载好了");

            /**
             * Message从消息队列取出后， Runnable中的run()方法会执行
             * */
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mRequestMap.get(target) != url || mHasQuit){
                        return;
                    }

                    mRequestMap.remove(target);
                    mPosterImageDownloadListener.onPosterImageDownloaded(target, bitmap);
                }
            });

        } catch (IOException ioe){
            Log.e(TAG, "下载图片出错", ioe);
        }

    }
}
