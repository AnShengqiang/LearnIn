package com.example.anshengqiang.learnin;

import android.app.Application;
import android.content.Context;

/**
 * 用于全局获取Context
 * Created by anshengqiang on 2017/3/1.
 */

public class MyApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate(){
        sContext = getApplicationContext();
    }

    public Context getContext(){
        return sContext;
    }

}
