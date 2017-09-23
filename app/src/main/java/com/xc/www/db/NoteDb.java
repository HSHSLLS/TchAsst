package com.xc.www.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xc.www.bean.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/30.
 */
public class NoteDb {

    private static NoteDb noteDb;
    private final DBOpenHelper mDbOpenHelper;

    private NoteDb(Context context) {
        mDbOpenHelper = new DBOpenHelper(context);
    }

    public static synchronized NoteDb getInstance(Context context) {
        if (noteDb == null) {
            noteDb = new NoteDb(context);
        }
        return noteDb;
    }

    public void insertNote(Note note) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.execSQL("insert into note (title,content,write_time) values(?,?,?)", new String[]{note.getTitle(), note.getContent(),note.getWrite_time()});
        db.close();
    }

    public void upDateNote(Note note){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.execSQL("update note set title=?,content=? where write_time=?", new String[]{note.getTitle(), note.getContent(), note.getWrite_time()});
        db.close();
    }

    public List<Note> queryAll() {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from note", null);
        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setTitle(cursor.getString(1));
            note.setContent(cursor.getString(2));
            note.setWrite_time(cursor.getString(3));
            noteList.add(note);
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public void deleteNote(List<Note> noteList){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        for (Note note:noteList) {
            db.execSQL("delete from note where write_time=?", new String[]{note.getWrite_time()});
        }
        db.close();
    }

    public Note queryNote(String writeTime) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        Note note = new Note();
        Cursor cursor = db.rawQuery("select * from note where write_time=?", new String[]{writeTime});
        while (cursor.moveToNext()) {
            note.setTitle(cursor.getString(1));
            note.setContent(cursor.getString(2));
            note.setWrite_time(cursor.getString(3));
        }
        cursor.close();
        db.close();
        return note;
    }

}
