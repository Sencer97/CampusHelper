package com.test.campushelper.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 *  用户信息类
 */
public class UserData extends BmobObject {
    private String userName;          //用户名
    private String sex;               //性别
    private String birthday;          //生日
    private String school;            //学校
    private String depart;            //学院
    private String major;             //专业
    private String grade;             //年级
    private String signature;         //个性签名
    private String hobby;             //爱好
    private String id;                //objectId
    private String headIcon;       //存在服务器中的头像url
    private List<Friend> friendList;      //用户的好友列表

    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public UserData(){
    }

    public UserData(String id,String userName, String sex, String birthday,
                    String school, String depart, String major,
                    String grade, String signature, String hobby, List<Friend> list) {
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
        this.friendList = list;
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
