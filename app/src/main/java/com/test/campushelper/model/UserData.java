package com.test.campushelper.model;

import cn.bmob.v3.BmobObject;

/**
 *  用户信息类
 */
public class UserData extends BmobObject {
    private String userName;
    private String sex;
    private String birthday;
    private String school;
    private String depart;
    private String major;
    private String grade;
    private String signature;
    private String hobby;
    private String id;

    public UserData(){
    }

    public UserData(String id,String userName, String sex, String birthday, String school, String depart, String major, String grade, String signature, String hobby) {
        this.id = id;
        this.userName = userName;
        this.sex = sex;
        this.birthday = birthday;
        this.school = school;
        this.depart = depart;
        this.major = major;
        this.grade = grade;
        this.signature = signature;
        this.hobby = hobby;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
