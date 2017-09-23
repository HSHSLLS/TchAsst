package com.xc.www.utils;

import com.xc.www.app.AppSharedPreferences;
import com.xc.www.app.ConstantValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/10/20.
 */
public class TableTimer {

    private static String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
            "星期六"};

    public static String daysOfTwo(String oDate) {

        String fDate = AppSharedPreferences.getString(ConstantValues.SETDATE, "2016-09-01");  //获取设置参数：设置参数的日期
        int setweek = AppSharedPreferences.getInt(ConstantValues.SETWEEK, 1);  //获取设置参数：第几周
        int weekday = AppSharedPreferences.getInt(ConstantValues.WEEKDAY, 4);  //获取设置参数：星期几，根据数组dayNames[]

        int thisWeekday = 0;
        int day1 = 0;
        int day2 = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar aCalendar = Calendar.getInstance();
        try {
            aCalendar.setTime(sdf.parse(fDate));
            day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

            aCalendar.setTime(sdf.parse(oDate));
            day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

            thisWeekday = aCalendar.get(Calendar.DAY_OF_WEEK) - 1;   //得出今天周几
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //根据设置，计算出第几周
        int thisWeek=0;
        int remain = day2 - day1 - (6 - weekday); //减去 设置那周要变周所需要的天数
        if (remain > 0) {                         //大于0，表示不是设置参数的那周
            thisWeek = setweek + remain / 7;      //商
            if (remain % 7 != 0) {
                thisWeek = thisWeek + 1;          //余数算一周
            }
        }else {                                    //还处于设置参数那周
            thisWeek=setweek;
        }

        String result = "第" + thisWeek + "周-" + dayNames[thisWeekday];
        return result;
    }
}
