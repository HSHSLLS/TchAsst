package com.xc.www.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xc.www.bean.NotificationItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/30.
 */
public class NotificationDb {

    private static NotificationDb notificationDbDb;
    private final DBOpenHelper mDbOpenHelper;

    private NotificationDb(Context context) {
        mDbOpenHelper = new DBOpenHelper(context);
    }

    public static synchronized NotificationDb getInstance(Context context) {
        if (notificationDbDb == null) {
            notificationDbDb = new NotificationDb(context);
        }
        return notificationDbDb;
    }

    public void insertNotification(String title, String content) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        String receiveTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        db.execSQL("insert into notification (title,content,receive_time) values(?,?,?)", new String[]{title, content, receiveTime});
        db.close();
    }

    public List<NotificationItem> queryAll() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        List<NotificationItem> notificationList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from notification", null);
        while (cursor.moveToNext()) {
            NotificationItem notificationItem = new NotificationItem();
            notificationItem.setId(cursor.getInt(0));
            notificationItem.setContent(cursor.getString(2));
            notificationItem.setReceive_time(cursor.getString(3));
            notificationList.add(notificationItem);
        }
        cursor.close();
        db.close();
        return notificationList;
    }

    public void deleteNotification(int id) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.execSQL("delete from notification where id=?", new String[]{String.valueOf(id)});
        db.close();
    }


}
