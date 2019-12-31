package com.bugcoder.sc.student.schedule;

import java.io.Serializable;

public class Teacher_Schedule implements Serializable {
    public Teacher_Schedule(String courseName, String teacherName, String time, String room, String date) {
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.time = time;
        this.room = room;
        this.date = date;
    }

    String courseName;
    String teacherName;
    String time;
    String room;
    String date;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
