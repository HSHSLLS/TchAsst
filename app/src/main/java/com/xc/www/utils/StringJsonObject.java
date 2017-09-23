package com.xc.www.utils;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xc.www.app.AppApplication;
import com.xc.www.app.ConstantValues;
import com.xc.www.bean.CourseItem;
import com.xc.www.bean.StudentItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/10.
 */
public class StringJsonObject {
    private static File file= new File(ConstantValues.PATH+File.separator+"course.json");

    public static void setJsonFromObjectList(List<CourseItem> courseItems){
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter =null;
        BufferedWriter bufferedWriter=null;
        Gson gson=new Gson();
        String result=gson.toJson(courseItems,new TypeToken<List<CourseItem>>(){}.getType());
        if (file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(result);
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedWriter.close();
                outputStreamWriter.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<CourseItem> getObjectListFromJson(){
        FileInputStream fileInputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;

        List<CourseItem> courseItems=new ArrayList<>();
        if (file.exists()){
            try {
                fileInputStream=new FileInputStream(file);
                inputStreamReader=new InputStreamReader(fileInputStream);
                bufferedReader=new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder=new StringBuilder();
                String str=null;
                while ((str=bufferedReader.readLine())!=null){
//                    if(TextUtils.isEmpty(stringBuilder.toString())){
//                        stringBuilder.append("\r");
//                    }
                    stringBuilder.append(str);  //必须借助str，不能直接传入bufferedReader.readLine()，否则为空。（即stringBuilder的入参不允许有操作）
                }
                Gson gson=new Gson();
                courseItems = gson.fromJson(stringBuilder.toString(), new TypeToken<List<CourseItem>>(){}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bufferedReader.close();
                    inputStreamReader.close();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return courseItems;
    }

    /**
     * @param type     1代表点名保存，2代表修改结果
     * @param file
     * @param studentItems
     * @return boolean 返回true表示保存成功，返回false表示保存失败
     */
    public static Boolean saveRollCallDetail(int type,File file, List<StudentItem> studentItems){
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter =null;
        BufferedWriter bufferedWriter=null;
        Gson gson=new Gson();
        String result=gson.toJson(studentItems,new TypeToken<List<StudentItem>>(){}.getType());
        if (type==1 && file.exists()){
            Toast.makeText(AppApplication.getInstance(),"本课点名结果已存在",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (type==2){
            file.delete();
        }

        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(result);
            bufferedWriter.flush();
            Toast.makeText(AppApplication.getInstance(),"保存成功",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedWriter.close();
                outputStreamWriter.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;

    }


    public static List<StudentItem> readRollCallDetail(String path){
        File rollCallTable=new File(path);
        FileInputStream fileInputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;

        List<StudentItem> studentItems=new ArrayList<>();
        if (rollCallTable.exists()){
            try {
                fileInputStream=new FileInputStream(rollCallTable);
                inputStreamReader=new InputStreamReader(fileInputStream);
                bufferedReader=new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder=new StringBuilder();
                String str=null;
                while ((str=bufferedReader.readLine())!=null){
//                    if(TextUtils.isEmpty(stringBuilder.toString())){
//                        stringBuilder.append("\r");
//                    }
                    stringBuilder.append(str);  //必须借助str，不能直接传入bufferedReader.readLine()，否则为空。（即stringBuilder的入参不允许有操作）
                }
                Gson gson=new Gson();
                studentItems = gson.fromJson(stringBuilder.toString(), new TypeToken<List<StudentItem>>(){}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bufferedReader.close();
                    inputStreamReader.close();
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return studentItems;
    }


}
