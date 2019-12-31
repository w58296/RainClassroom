package com.bugcoder.sc.student.course;

import java.io.Serializable;

public class Student_Talk implements Serializable {
    private  String stuId;
    private  String stuName;
    private String question;
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String good;
    private String name;

    public Student_Talk(String stuId, String stuName, String question, String date, String good, String name) {
        this.stuId = stuId;
        this.stuName = stuName;
        this.question = question;
        this.date = date;
        this.good = good;
        this.name = name;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public Student_Talk(String stuId, String stuName, String question, String date, String good) {
        this.stuId = stuId;
        this.stuName = stuName;
        this.question = question;
        this.date = date;
        this.good = good;
    }
}
