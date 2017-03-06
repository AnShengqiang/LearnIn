package com.example.anshengqiang.learnin.model;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.anshengqiang.learnin.model.EssayDbSchema.EssayTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by anshengqiang on 2017/2/28.
 */

public class EssayCursorWrapper extends CursorWrapper {

    public EssayCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    /*adsf*/
    /**
     * 从Cursor中取出数据
     * 用数据创建一个Essay
     * */
    public Essay getEssay(){

        String uuidString = getString(getColumnIndex(EssayTable.Cols.UUID));
        String detail = getString(getColumnIndex(EssayTable.Cols.DETAIL));
        String title = getString(getColumnIndex(EssayTable.Cols.TITLE));

        String jsonid = getString(getColumnIndex(EssayTable.Cols.JSONID));
        String image = getString(getColumnIndex(EssayTable.Cols.IMAGE));
        String css = getString(getColumnIndex(EssayTable.Cols.CSS));
        long date = getLong(getColumnIndex(EssayTable.Cols.DATE));


        Essay essay = new Essay(UUID.fromString(uuidString));
        essay.setTitle(title);
        essay.setDetail(detail);

        essay.setJsonId(jsonid);
        essay.setImage(image);
        essay.setCss(css);
        essay.setDate(new Date(date));

        return essay;
    }
}
