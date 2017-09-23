package com.xc.www.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/8.
 */
public class CourseItem implements Serializable {
    private String name;
    private String Location;
    private String time;
    private Integer stu_count;
    private String teacher_name;
    private String course_number;

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Integer getStu_count() {
        return stu_count;
    }

    public void setStu_count(Integer stu_count) {
        this.stu_count = stu_count;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }
}
