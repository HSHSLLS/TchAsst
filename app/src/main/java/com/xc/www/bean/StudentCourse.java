package com.xc.www.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/10/12.
 */
public class StudentCourse extends BmobObject {
    private String stu_number;
    private String course_number;

    public String getCourse_number() {
        return course_number;
    }

    public void setCourse_number(String course_number) {
        this.course_number = course_number;
    }

    public String getStu_number() {
        return stu_number;
    }

    public void setStu_number(String stu_number) {
        this.stu_number = stu_number;
    }
}
