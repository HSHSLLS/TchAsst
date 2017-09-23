package com.xc.www.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/10/11.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String ROLLCALLTABLE="RollCallTable";

    private final String NOTE_TABLE="create table note (" +
            "id integer primary key autoincrement," +
            "title text," +
            "content text," +
            "write_time text)";

    private final String NOTIFICATION_TABLE="create table notification (" +
            "id integer primary key autoincrement," +
            "title text," +
            "content text," +
            "receive_time text)";

    public DBOpenHelper(Context context) {
        super(context , "tchasst.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NOTE_TABLE);
        db.execSQL(NOTIFICATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
