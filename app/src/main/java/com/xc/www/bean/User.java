package com.xc.www.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/10/6.
 */
public class User extends BmobUser {
    private String sex;
//    private String tch_number;
    private String tch_name;
    private String department;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

//    public String getTch_number() {
//        return tch_number;
//    }
//
//    public void setTch_number(String tch_number) {
//        this.tch_number = tch_number;
//    }


    public String getTch_name() {
        return tch_name;
    }

    public void setTch_name(String tch_name) {
        this.tch_name = tch_name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}
