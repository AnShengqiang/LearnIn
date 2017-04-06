package com.example.anshengqiang.learnin.fetchr;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.anshengqiang.learnin.MyApplication;
import com.example.anshengqiang.learnin.model.Essay;
import com.example.anshengqiang.learnin.model.EssayLab;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by anshengqiang on 2017/2/28.
 */

public class HexoFetchr {
    private static final String TAG = "HexoFetchr";

    public static String id;


    /**
     * 从指定网址获取Json数据
     * 存入byte[]
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        /*获取HttpURLConnection*/
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            /*输出流 输入流*/
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            /*判断HttpURLConnection的连接情况*/
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[2048000];

            /**
             * 输入流--->byte[]--->输出流
             * in.read(byte[])              输入流--->byte[]，同时返回一个int值
             * out.write(byte[], 0, int)    byte[]-->输出流
             * */
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }



    /**
     * 从制定地址获取json数据
     * 存为String格式
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    /**
     * 连接最新文章列表
     * 获取json
     */
    public List<Essay> fetchLatest(Context context, String url, String category) throws IOException, JSONException {
        String jsonList = getUrlString(url);
        JSONObject jsonObject = new JSONObject(jsonList);

        return parseLatest(context, jsonObject, category);
    }
/*
    public List<Essay> fetchFun(Context context, String url) throws IOException, JSONException {
        String jsonList = getUrlString(url + "latest");
        JSONObject jsonObject = new JSONObject(jsonList);

        return parseLatest(context, jsonObject);
    }

    public List<Essay> fetchPsychology(Context context, String url) throws IOException, JSONException {
        String jsonList = getUrlString(url + "latest");
        JSONObject jsonObject = new JSONObject(jsonList);

        return parseLatest(context, jsonObject);
    }*/

    /**
     * 连接文章页面
     * 获取json
     * 调用parseDetail 方法，解析文章json
     */
    public List<Essay> fetchDetail(Context context, String _url, String category) {

        List<Essay> essays = EssayLab.get(context).getEssays(category);

        for (int i = 0; i < essays.size(); i++) {

            id = essays.get(i).getJsonId();
            String url = _url + id;

            try {
                String jsonDetail = getUrlString(url);
                JSONObject jsonObject = new JSONObject(jsonDetail);
                essays = parseDetail(context, jsonObject, category);

            } catch (IOException e) {
                Log.i(TAG, "Failed to fetch Detail", e);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to parse Detail json", e);
            }
        }

        return essays;
    }

    /**
     * 解析文章列表json
     */
    private List<Essay> parseLatest(Context context, JSONObject jsonList, String category)
            throws IOException, JSONException {

        List<Essay> essays = EssayLab.get(context).getEssays(category);
        JSONArray topStoriesJsonArray = jsonList.getJSONArray("stories");

        for (int i = 0; i < topStoriesJsonArray.length(); i++) {
            JSONObject storyJsonObject = topStoriesJsonArray.getJSONObject(i);

            /*数据库中是否"已经存在"该文章*/
            boolean isExist = false;
            for (int j = 0; j < essays.size(); j++) {
                /*判断字符串是否相等，a.equals(b)*/
                if (essays.get(j).getJsonId().equals(storyJsonObject.getString("id"))) {
                    isExist = true;
                    Log.i(TAG, "isExist的值变为" + isExist);
                    break;
                }
            }
            if (!isExist) {
                /*新建一个Essay，存入数据*/
                Essay essay = new Essay();

                essay.setCategory(category);
                essay.setDate(new Date());                                                          //测试完之后，需要更改
                essay.setTitle(storyJsonObject.getString("title"));
                essay.setJsonId(storyJsonObject.getString("id"));

                /*JSONArray imageArray = storyJsonObject.getJSONArray("images");
                String image = imageArray.toString();
                image = image.substring(2, 67);
                Log.i(TAG, "image url is:" + image);
                essay.setImage(image);*/

                EssayLab.get(context).addEssay(essay);
                Log.i(TAG, "添加了一个Essay，isExist = " + isExist +"title is:" + essay.getTitle());
            }
        }

        return EssayLab.get(context).getEssays(category);                                                   //假如直接使用essays能不能刷新？
    }

    /**
     * 解析文章json
     */
    private List<Essay> parseDetail(Context context, JSONObject jsonDetail, String category)
            throws IOException, JSONException {

        /**
         * css是一个json数组，数组内只有一个字符串，而且没有key
         * 尝试直接获取数组，并转为string
         * */
        JSONArray cssArray = jsonDetail.getJSONArray("css");
        String css = cssArray.toString();


        String jsonId = jsonDetail.getString("id");
        String detail = jsonDetail.getString("body");
        String image = jsonDetail.getString("image");
        List<Essay> essays = EssayLab.get(context).getEssays(category);

        /*遍历id，找出特定的Essay*/
        for (int i = 0; i < essays.size(); i++) {
            Essay essay = essays.get(i);
            if(essay.getJsonId().equals(jsonId)) {

                essay.setDetail(detail);
                essay.setCss(css);
                essay.setImage(image);
                EssayLab.get(context).updateEssay(essay);

                //Log.i(TAG, "分享链接为" + detail);
            }
        }
        return essays;

    }


}
