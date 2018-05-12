package com.test.campushelper.model;


import com.test.campushelper.utils.Cn2Spell;

import cn.bmob.v3.BmobObject;

public class Fellow extends BmobObject implements Comparable<Fellow>{
    private String name;
    private String mobile;
    private String spell;
    private String firstLetter;
    private String id;
    private String tag;

    public Fellow(){}

    public Fellow(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
        spell = Cn2Spell.getSpell(name);
        firstLetter = spell.substring(0,1).toUpperCase();
        if(!firstLetter.matches("[A-Z]")){       //匹配字母，正则表达式
            firstLetter = "#";
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int compareTo(Fellow another) {
        if(firstLetter.equals("#") && !another.getFirstLetter().equals("#")){
            return 1;
        }else if(!firstLetter.equals("#")  && another.getFirstLetter().equals("#")){
            return -1;
        }else {
            return spell.compareToIgnoreCase(another.getSpell());
        }
    }
}
