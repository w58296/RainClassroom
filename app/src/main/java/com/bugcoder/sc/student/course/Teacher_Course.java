package com.bugcoder.sc.student.course;

import java.io.Serializable;

public class Teacher_Course implements Serializable {
    public Teacher_Course(String major, String courseName, String teacherName, int taskNum, int myProgress) {
        this.major = major;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.taskNum = taskNum;
        this.myProgress = myProgress;
    }

    ;
    public String major;
    public String courseName;
    public String teacherName;
    public int taskNum = 0;
    public int myProgress = 0;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getMyProgress() {
        return myProgress;
    }

    public void setMyProgress(int myProgress) {
        this.myProgress = myProgress;
    }
}
