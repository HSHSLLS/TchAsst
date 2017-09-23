package com.xc.www.app;

import android.app.Application;

import com.xc.www.bean.User;

import java.io.File;

/**
 * Created by Administrator on 2016/10/6.
 */
public class AppApplication extends Application {

    public static AppApplication mInstance;
//    public String objectId;
//    public String tch_number;
//    public boolean islogin;
    public User currentUser=null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();

        //初始化测试数据
//        AppSharedPreferences.putStirng(ConstantValues.SETDATE,"2016-09-01");
//        AppSharedPreferences.putInt(ConstantValues.SETWEEK,1);
//        AppSharedPreferences.putInt(ConstantValues.WEEKDAY,4);
    }

    private void init() {
        //初始化文件夹
        File file=new File(ConstantValues.PATH);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    public static AppApplication getInstance() {
        return mInstance;
    }


}
