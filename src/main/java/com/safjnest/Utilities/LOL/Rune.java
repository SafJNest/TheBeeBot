package com.safjnest.Utilities.LOL;

public class Rune {
    
    private String id;
    private String key;
    private String icon;
    private String name;
    private String shortDesc;
    private String longDesc;

    public Rune(String id, String key, String icon, String name, String shortDesc, String longDesc){
        this.id = id;
        this.key = key;
        this.icon = icon;
        this.name = name;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
    }

    //create all get and set methods
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

    public String getShortDesc(){
        return shortDesc;
    }

    public String getLongDesc(){
        return longDesc;
    }


}
