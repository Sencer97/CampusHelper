package com.test.campushelper.model;

/**
 * 报名竞赛活动的人
 */
public class Attender {
    private String name;
    private String time;
    private String headUrl;

    public Attender(){}



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

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
}
