package com.xc.www.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/10/8.
 */
public class Course extends BmobObject {
    private String course_number;
    private String name;
    private String tch_number;
    private User user;
    private String location;
    private String time;
    private Integer stu_count;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTch_number() {
        return tch_number;
    }

    public void setTch_number(String tch_number) {
        this.tch_number = tch_number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getStu_count() {
        return stu_count;
    }

    public void setStu_count(Integer stu_count) {
        this.stu_count = stu_count;
    }
}
