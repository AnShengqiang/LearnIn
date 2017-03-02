package com.example.anshengqiang.learnin.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.anshengqiang.learnin.model.EssayDbSchema.EssayTable;

/**
 * Created by anshengqiang on 2017/2/28.
 */

public class EssayBaseOpenHelper extends SQLiteOpenHelper{

    private static final String TAG = "EssayBaseOpenHelper";
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "essaybase.db";

    public EssayBaseOpenHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * 此处为SQL语法，table后面要注意空格，
         * 逗号后面也要空格，不然报空指针的诡异错误，
         * 为什么？*/
        db.execSQL("create table " + EssayTable.NAME + "(" +
        " _id integer primary key autoincrement, " +
        EssayTable.Cols.UUID + ", " +
        EssayTable.Cols.TITLE + ", " +
        EssayTable.Cols.DETAIL + ", " +
        EssayTable.Cols.IMAGE + ", " +
        EssayTable.Cols.JSONID + ", " +
        EssayTable.Cols.CSS + ", " +
        EssayTable.Cols.DATE +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
