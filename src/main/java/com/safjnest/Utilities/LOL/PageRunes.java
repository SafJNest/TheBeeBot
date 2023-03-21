package com.safjnest.Utilities.LOL;

import java.util.HashMap;

public class PageRunes {
    private String page;
    private String id;
    private String key;
    private String icon;
    private String name;
    private HashMap<String,Rune> runes;

    public PageRunes(String page, String id, String key, String icon, String name){
        this.id = id;
        this.key = key;
        this.icon = icon;
        this.name = name;
        runes = new HashMap<String,Rune>();
    }

    public String getPage(){
        return page;
    }

    public String getId(){
        return id;
    }
    
    public String getKey(){
        return key;
    }

    public String getIcon(){
        return icon;
    }

    public String getName(){
        return name;
    }

    public void insertRune(String id, Rune rune){
        runes.put(id, rune);
    }

    public HashMap<String, Rune> getRunes(){
        return runes;
    }

    public Rune getRune(String id){
        return runes.get(id);
    }


}
