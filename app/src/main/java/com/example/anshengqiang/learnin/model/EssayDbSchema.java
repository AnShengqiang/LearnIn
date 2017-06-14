package com.example.anshengqiang.learnin.model;

import android.graphics.Bitmap;

/**
 * Created by anshengqiang on 2017/2/28.
 */

public class EssayDbSchema {

    public static final class EssayTable{
        public static final String NAME = "essays";

        public static final class Cols{
            public static final String CATEGORY = "category";
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DETAIL = "detail";
            public static final String DATE = "date";
            public static final String IMAGE = "image";
            public static final String JSONID = "jsonid";
            public static final String CSS = "css";
        }

    }

}
