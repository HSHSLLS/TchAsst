package com.xc.www.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/10/20.
 */
public class AppSharedPreferences {
    private static SharedPreferences sp;

    public static void putStirng(String key,String value){
        if (sp==null){
            sp=AppApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public static String getString(String key,String defValue){
        if (sp==null){
            sp=AppApplication.getInstance().getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getString(key,defValue);
    }

    public static void putInt(String key,int value){
        if (sp==null){
            sp=AppApplication.getInstance().getSharedPreferences("TchAsst_config", Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key,value);
        edit.commit();
    }

    public static int getInt(String key,int defValue){
        if (sp==null){
            sp=AppApplication.getInstance().getSharedPreferences("TchAsst_config",Context.MODE_PRIVATE);
        }
        return sp.getInt(key,defValue);
    }

    public static void putBoolean(String key,Boolean value){
        if (sp==null){
            sp=AppApplication.getInstance().getSharedPreferences("TchAsst_config",Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public static Boolean getBoolean(String key,Boolean defValue){
        if (sp==null){
            sp=AppApplication.getInstance().getSharedPreferences("TchAsst_config",Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);
    }

}
