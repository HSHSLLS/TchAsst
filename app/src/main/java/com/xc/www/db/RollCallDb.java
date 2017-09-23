package com.xc.www.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.xc.www.app.AppSharedPreferences;
import com.xc.www.app.ConstantValues;
import com.xc.www.bean.Student;
import com.xc.www.bean.StudentItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11.
 */
public class RollCallDb {

    private static RollCallDb rollCallDb;
    private final DBOpenHelper mDBOpenHelper;

    private RollCallDb(Context context) {
        mDBOpenHelper = new DBOpenHelper(context);
    }

    public static synchronized RollCallDb getInstance(Context context) {
        if (rollCallDb == null) {
            rollCallDb = new RollCallDb(context);
        }
        return rollCallDb;
    }

    /**
     *
     * @param tableName
     * @param studentList
     * @return 返回-1，表示建表失败,；返回0，表示插入数据为零或不成功（同时删除该新表），返回值不为-1、0，表示成功插入数据的条数
     */
    public int createTable(String tableName, List<Student> studentList) {
        String rollcall = "create table if not exists " + tableName + " (" +
                "id char(10) primary key not null," +
                "stu_number char[20] unique," +
                "name char(20)," +
                "sex char(4)," +
                "major char(10)," +
                "cl char(5)," +
                "institute char(15)," +
                "mac char(20) unique," +
                "grade integer default 100," +
                "late_times integer default 0," +
                "truancy_times integer default 0," +
                "leave_times integer default 0)";
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        try {
            db.execSQL(rollcall);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        int count=-1;
        if (isTableExist(tableName)){
            if (studentList.size() != 0) {
                count = insert(tableName, studentList);
                if (count==0){
                    deleteTable(tableName);
                }
            }
        }
        return count;
    }

    public void deleteTable(String tableName) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL("drop table " + tableName);
        db.close();
    }

    public boolean isTableExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
            cursor.close();

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            db.close();
        }
        return result;
    }


    public int insert(String tableName, List<Student> studentList) {
        int count=0;
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        for (int i = 0; i < studentList.size(); i++) {
            Student student = studentList.get(i);
            contentValues.put("id",student.getObjectId());
            contentValues.put("stu_number",student.getStu_number());
            contentValues.put("name",student.getName());
            contentValues.put("sex",student.getSex());
            contentValues.put("major",student.getMajor());
            contentValues.put("cl",student.getCl());
            contentValues.put("institute",student.getInstitute());
            if (student.getMac().contains(":")){
                contentValues.put("mac",student.getMac());
            }
            long insert = db.insert(tableName, null, contentValues);
            if (insert!=0){
                count++;
            }
            contentValues.clear();

//            String[] info = new String[]{
//                    student.getObjectId(), student.getStu_number(),
//                    student.getName(), student.getSex(),
//                    student.getMajor(), student.getCl(),
//                    student.getInstitute(), student.getMac() + ""
//            };

//            db.execSQL("insert into " + DBOpenHelper.ROLLCALLTABLE + " (" +
//                    "id,stu_number,name,sex,major,cl,institute,mac)" +
//                    " values (?,?,?,?,?,?,?,?)", info);
        }
        db.close();
        return count;
    }

    public void delete(String stu_number) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL("delete from " + DBOpenHelper.ROLLCALLTABLE + " where stu_number=?", new String[]{stu_number});
        db.close();
    }

    public Map<String, Integer> upDateMac(String tableName, Map<String,String> mDevicesMap) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        Map<String,Integer> feedBack=new HashMap<>();
        int successCount=0;
        int errorCount=0;
        int repeatCount=0;
        int result;
        ContentValues contentValues=new ContentValues();
        Iterator iterator=mDevicesMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            contentValues.put("mac", (String) entry.getValue());
            try {
                result = db.update(tableName, contentValues, "stu_number=?", new String[]{(String) entry.getKey()});
                if (result!=0){
                    successCount++;
                }else
                    errorCount++;
            } catch (Exception e) {
                String er=e.toString();
                if (er.contains("UNIQUE constraint failed")){
                    repeatCount++;
                }
            }
            contentValues.clear();
        }
        db.close();
        feedBack.put("successCount",successCount);
        feedBack.put("errorCount",errorCount);
        feedBack.put("repeatCount",repeatCount);
        return feedBack;
    }


    public List<StudentItem> queryAll(String tableName) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select major,cl,name,stu_number,mac,id,grade from " + tableName, null);
        List<StudentItem> studentItems = new ArrayList<>();
        while (cursor.moveToNext()) {
            StudentItem studentItem = new StudentItem();
            studentItem.setMajorGroup(cursor.getString(0) + cursor.getString(1));
            studentItem.setName(cursor.getString(2));
            studentItem.setStu_number(cursor.getString(3));
            studentItem.setMac(cursor.getString(4));
            studentItem.setId(cursor.getString(5));
            studentItems.add(studentItem);
        }
        cursor.close();
        db.close();
        return studentItems;
    }

    public void queryMAC(String tableName, List<StudentItem> studentItems){
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        for (StudentItem studentItem:studentItems){
            Cursor cursor = db.query(tableName, new String[]{"mac"}, "stu_number=?", new String[]{studentItem.getStu_number()}, null, null, null);
            while (cursor.moveToNext()){
                studentItem.setMac(cursor.getString(0));
            }
            cursor.close();
        }
        db.close();

    }

    public void updateGrade(String tableName,String stu_number,int editGrade){
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL("update "+tableName+" set grade=grade+? where stu_number=?",new String[]{String.valueOf(editGrade),stu_number});
        db.close();
    }

    public String queryGrade(String tableName,String stu_number){
        String grade="";
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select grade from " + tableName + " where stu_number=?", new String[]{stu_number});
        while (cursor.moveToNext()){
            grade= String.valueOf(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        return grade;
    }

    public void saveAttendance(String tableName,List<StudentItem> studentItems){
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        int lateGrade= AppSharedPreferences.getInt(ConstantValues.LATEGRADE,1);       //以正数存储
        int truancyGrade=AppSharedPreferences.getInt(ConstantValues.TRUANCYGRADE,3);  //以正数存储
        for (StudentItem studentItem:studentItems){
            switch (studentItem.getAttendence()){
                case 1: //迟到
                    db.execSQL("update "+ tableName+" set late_times=late_times+1,grade=grade-? where stu_number=?",new String[]{String.valueOf(lateGrade),studentItem.getStu_number()});
                    break;
                case 2: //旷课
                    db.execSQL("update "+ tableName+" set truancy_times=truancy_times+1,grade=grade-? where stu_number=?",new String[]{String.valueOf(truancyGrade),studentItem.getStu_number()});
                    break;
                case 3: //请假
                    db.execSQL("update "+ tableName+" set leave_times=leave_times+1 where stu_number=?",new String[]{studentItem.getStu_number()});
                    break;
            }
        }
        db.close();
    }

    public void upDateAttendance(String tableName,Map<String,String> changeAttnMap){
        int lateGrade= AppSharedPreferences.getInt(ConstantValues.LATEGRADE,0);  //默认迟到扣0分
        int truancyGrade=AppSharedPreferences.getInt(ConstantValues.TRUANCYGRADE,0);//默认旷课扣0分
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        Iterator iterator=changeAttnMap.entrySet().iterator();
        String stu_number;
        String type;
        while (iterator.hasNext()){
            Map.Entry entry= (Map.Entry) iterator.next();
            stu_number=entry.getKey().toString();
            type=entry.getValue().toString();
            type=type.substring(0,1)+type.substring(type.length()-1,type.length());
            switch (Integer.parseInt(type)){
                case 20: //从2变为0，旷课改为到
                    db.execSQL("update "+ tableName+" set truancy_times=truancy_times-1,grade=grade+? where stu_number=?",new String[]{String.valueOf(truancyGrade),stu_number});
                    break;
                case 21: //从2变为1，旷课改为迟到
                    db.execSQL("update "+ tableName+" set truancy_times=truancy_times-1,late_times=late_times+1,grade=grade+?-? where stu_number=?",new String[]{String.valueOf(truancyGrade),String.valueOf(lateGrade),stu_number});
                    break;
                case 23: //从2变为3，旷课改为请假
                    db.execSQL("update "+ tableName+" set truancy_times=truancy_times-1,leave_times=leave_times+1,grade=grade+? where stu_number=?",new String[]{String.valueOf(truancyGrade),stu_number});
                    break;
                default:
                    break;
            }
        }
        db.close();
    }


    public List<StudentItem> getStudentSituation(String tableName){
        List<StudentItem> studentItems=new ArrayList<>();
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select major,cl,name,stu_number,grade,late_times,truancy_times,leave_times from " + tableName,null);
        while (cursor.moveToNext()){
            StudentItem studentItem=new StudentItem();
            studentItem.setMajorGroup(cursor.getString(0) + cursor.getString(1));
            studentItem.setName(cursor.getString(2));
            studentItem.setStu_number(cursor.getString(3));
            studentItem.setGrade(cursor.getInt(4));
            studentItem.setLate_times(cursor.getInt(5));
            studentItem.setTruancy_times(cursor.getInt(6));
            studentItem.setLeave_times(cursor.getInt(7));
            studentItems.add(studentItem);
        }
        cursor.close();
        db.close();
        return studentItems;
    }

}
