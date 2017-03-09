package com.example.anshengqiang.learnin;

import android.app.Application;
import android.content.Context;

/**
 * 用于全局获取Context
 * 获取的context有问题，猜测可能不是这个应用的application
 * Created by anshengqiang on 2017/3/1.
 */

public class MyApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
    }

    public Context getContext(){
        return sContext;
    }

}
