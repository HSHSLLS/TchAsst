package com.xc.www.app;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/10/10.
 */
public class ConstantValues {
    //该应用文件父路径
    public static final String PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"TchAsst";

    public static final String SETDATE="setdate";  //设置参数的日期
    public static final String SETWEEK="setweek";  //第几周
    public static final String WEEKDAY="weekday";  //星期几

    public static final String LATEGRADE="lategrade";       //迟到扣的分数
    public static final String TRUANCYGRADE="truancygrade"; //旷课扣的分数

    public static final String NOTIFICATION="notification"; //是否有通知
    public static final String NOTIFICATION_ID="notification_id"; //通知的id（接收的时间 HH:mm:ss）,sharedpreference,string类型，以逗号为拼接符
}
