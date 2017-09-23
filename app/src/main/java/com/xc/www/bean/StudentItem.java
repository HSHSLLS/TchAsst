package com.xc.www.bean;

/**
 * Created by Administrator on 2016/10/12.
 */
public class StudentItem {
    private String majorGroup;
    private String name;
    private String stu_number;
    private String mac;
    private String id;
    private int attendence; //0:到 1：迟到 2：旷课 3：请假  注：类变量默认值为0

    private int grade;
    private int late_times;
    private int truancy_times;
    private int leave_times;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getLate_times() {
        return late_times;
    }

    public void setLate_times(int late_times) {
        this.late_times = late_times;
    }

    public int getTruancy_times() {
        return truancy_times;
    }

    public void setTruancy_times(int truancy_times) {
        this.truancy_times = truancy_times;
    }

    public int getLeave_times() {
        return leave_times;
    }

    public void setLeave_times(int leave_times) {
        this.leave_times = leave_times;
    }

    public int getAttendence() {
        return attendence;
    }

    public void setAttendence(int attendence) {
        this.attendence = attendence;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMajorGroup() {
        return majorGroup;
    }

    public void setMajorGroup(String majorGroup) {
        this.majorGroup = majorGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStu_number() {
        return stu_number;
    }

    public void setStu_number(String stu_number) {
        this.stu_number = stu_number;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
