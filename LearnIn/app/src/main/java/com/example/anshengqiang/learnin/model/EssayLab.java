package com.example.anshengqiang.learnin.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.example.anshengqiang.learnin.LearnInActivity;
import com.example.anshengqiang.learnin.LearnInFragment;
import com.example.anshengqiang.learnin.MyApplication;
import com.example.anshengqiang.learnin.model.EssayDbSchema.EssayTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by anshengqiang on 2017/2/28.
 */

public class EssayLab {

    private static EssayLab sEssayLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private EssayLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new EssayBaseOpenHelper(mContext).getWritableDatabase();
    }

    /*单例get方法*/
    public static EssayLab get(Context context){
        if (sEssayLab == null){
            sEssayLab = new EssayLab(context);
        }
        return sEssayLab;
    }

    /*向数据库中插入一条essay数据*/
    public void addEssay(Essay essay){
        ContentValues values = getContentValues(essay);
        mDatabase.insert(EssayTable.NAME, null, values);
    }

    /*获取essay数组*/
    public List<Essay> getEssays(){
        List<Essay> essays = new ArrayList<>();

        EssayCursorWrapper essayCursorWrapper = queryEssays(null, null);
        essayCursorWrapper.moveToFirst();
        while(!essayCursorWrapper.isAfterLast()){
            essays.add(essayCursorWrapper.getEssay());
            essayCursorWrapper.moveToNext();
        }
        essayCursorWrapper.close();

        return essays;
    }

    /*获取特定ID的essay*/
    public Essay getEssay(UUID id){
        EssayCursorWrapper essayCursorWrapper = queryEssays(
                EssayTable.Cols.UUID + "=?", new String[] {id.toString()});

        try {
            if (essayCursorWrapper.getCount() == 0){
                return null;
            }

            essayCursorWrapper.moveToFirst();
            return essayCursorWrapper.getEssay();
        }finally {
            essayCursorWrapper.close();
        }
    }

    /*更改数据库数据*/
    public void updateEssay(Essay essay){
        String uuidString = essay.getId().toString();
        ContentValues values = getContentValues(essay);

        mDatabase.update(EssayTable.NAME, values, EssayTable.Cols.UUID + "= ?",
                new String[] {uuidString});
    }

    /*将Essay数据封装在ContentValues中*/
    private static ContentValues getContentValues(Essay essay) {
        //ByteArrayOutputStream coverStream = new ByteArrayOutputStream();

        ContentValues contentValues = new ContentValues();
        contentValues.put(EssayTable.Cols.UUID, essay.getId().toString());
        contentValues.put(EssayTable.Cols.DETAIL, essay.getDetail());
        contentValues.put(EssayTable.Cols.TITLE, essay.getTitle());
        contentValues.put(EssayTable.Cols.JSONID, essay.getJsonId());
        contentValues.put(EssayTable.Cols.CSS, essay.getCss());
        contentValues.put(EssayTable.Cols.IMAGE, essay.getImage());
        contentValues.put(EssayTable.Cols.DATE, essay.getDate().toString());



        /*try {
            coverStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return contentValues;
    }

    /*从数据库中获取数据，存入cursor，并将cursor包装在cursorwrapper中*/
    private EssayCursorWrapper queryEssays(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                EssayTable.NAME,
                null,//Column.  null selects all columns.
                whereClause,
                whereArgs,
                null,//groupBy
                null,//having 值约束
                null //orderBy
        );

        return new EssayCursorWrapper(cursor);
    }

}
