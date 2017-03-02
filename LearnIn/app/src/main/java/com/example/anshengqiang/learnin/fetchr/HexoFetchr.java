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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by anshengqiang on 2017/2/28.
 */

public class HexoFetchr {
    private static final String TAG = "HexoFetchr";

    public static String id;


    /**
     * 从制定网址获取Json数据
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
    public void fetchLatest(String url) {
        try {
            String jsonList = getUrlString(url + "latest");
            JSONObject jsonObject = new JSONObject(jsonList);
            parseLatest(new MyApplication().getContext(), jsonObject);
        } catch (IOException e) {
            Log.i(TAG, "Failed to fetch Latest", e);
        } catch (JSONException e) {
            Log.i(TAG, "Failed to parse Latest json", e);
        }
    }

    /**
     * 连接文章页面
     * 获取json
     * 调用parseDetail 方法，解析文章json
     */
    public void fetchDetail(String _url) {
        Context context = new MyApplication().getContext();
        List<Essay> essays = EssayLab.get(context).getEssays();

        for (int i = 0; i < essays.size(); i++) {

            id = essays.get(i).getJsonId();
            String url = _url + id;

            try {
                String jsonDetail = getUrlString(url);
                JSONObject jsonObject = new JSONObject(jsonDetail);
                parseDetail(context, jsonObject);

            } catch (IOException e) {
                Log.i(TAG, "Failed to fetch Detail", e);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to parse Detail json", e);
            }
        }


    }

    /**
     * 解析文章列表json
     * */
    private void parseLatest(Context context, JSONObject jsonList)
            throws IOException, JSONException {

        List<Essay> essays = EssayLab.get(context).getEssays();
        JSONArray topStoriesJsonArray = jsonList.getJSONArray("top_stories");
        /*数据库中是否"已经存在"该文章*/
        boolean isExist = false;

        for (int i = 0; i < topStoriesJsonArray.length(); i++) {
            JSONObject storyJsonObject = topStoriesJsonArray.getJSONObject(i);

            /*遍历essays，寻找是否存在相同id。若相同，则已经存在该文章*/
            for (int j = 0; j < essays.size(); j++) {
                if (essays.get(j).getJsonId() == storyJsonObject.getString("id")) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                break;
            }

            /*新建一个Essay，存入数据*/
            Essay essay = new Essay();

            essay.setDate(new Date());                                                              //测试完之后，需要更改

            essay.setTitle(storyJsonObject.getString("title"));
            essay.setJsonId(storyJsonObject.getString("id"));

            if (!storyJsonObject.has("image")) {
                continue;
            }

            essay.setImage(storyJsonObject.getString("image"));
            EssayLab.get(context).addEssay(essay);
        }

    }

    /**
     * 解析文章json
     * */
    private void parseDetail(Context context, JSONObject jsonDetail)
            throws IOException, JSONException {

        String _id = jsonDetail.getString("id");
        id = _id;
        String detail = jsonDetail.getString("body");
        String css = jsonDetail.getString("css");
        List<Essay> essays = EssayLab.get(context).getEssays();

        /*遍历id，找出特定的Essay*/
        for (int i = 0; i < essays.size(); i++) {
            while (essays.get(i).getJsonId() == _id) {
                essays.get(i).setDetail(detail);
                essays.get(i).setCss(css);

                EssayLab.get(context).updateEssay(essays.get(i));
            }
        }

    }


}
