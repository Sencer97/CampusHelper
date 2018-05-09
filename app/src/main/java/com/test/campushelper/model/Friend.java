package com.test.campushelper.model;

import com.test.campushelper.utils.Cn2Spell;

public class Friend implements Comparable<Friend>{
    private String name;
    private String headIcon;
    private String spell;
    private String firstLetter;
    public Friend(){

    }

    public Friend(String name,String headIcon) {
        this.name = name;
        spell = Cn2Spell.getSpell(name);
        firstLetter = spell.substring(0,1).toUpperCase();
        if(!firstLetter.matches("[A-Z]")){       //匹配字母，正则表达式
            firstLetter = "#";
        }
        this.headIcon = headIcon;
    }
    /**
     *  a.compareTo(b) a>b --- return 1；
     *     a<b  return -1;
     *
     * @param another 另一个联系人
     * @return 对联系人进行排序，都为#或字母是才进行排序
     */
    @Override
    public int compareTo(Friend another) {
        if(firstLetter.equals("#") && !another.getFirstLetter().equals("#")){
            return 1;
        }else if(!firstLetter.equals("#")  && another.getFirstLetter().equals("#")){
            return -1;
        }else {
            return spell.compareToIgnoreCase(another.getSpell());
        }

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }
}
