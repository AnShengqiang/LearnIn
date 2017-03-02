package com.example.anshengqiang.learnin.model;

import android.graphics.Bitmap;

import java.sql.Blob;
import java.util.Date;
import java.util.UUID;

/**
 * Created by anshengqiang on 2017/2/27.
 */

public class Essay {

    private UUID mId;
    private String mTitle;
    private String mDetail;
    private String mJsonId;
    private String mCss;
    private Date mDate;
    private String mImage;





    public Essay(){
        this(UUID.randomUUID());
    }

    public Essay(UUID id){
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String name) {
        mTitle = name;
    }


    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String details) {
        mDetail = details;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getJsonId() {
        return mJsonId;
    }

    public void setJsonId(String mJsonId) {
        this.mJsonId = mJsonId;
    }


    public String getCss() {
        return mCss;
    }

    public void setCss(String css) {
        mCss = css;
    }
}
