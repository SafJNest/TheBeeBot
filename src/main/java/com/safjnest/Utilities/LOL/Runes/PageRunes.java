package com.safjnest.Utilities.LOL.Runes;

import java.util.HashMap;


/**
 * This class is used to store the data of a rune page and all of its attributes.
 * <ul>
    * <li>Precision</li>
    * <li>Domination</li>
    * <li>Sorcery</li>
    * <li>Resolve</li>
    * <li>Inspiration</li>
 * </ul>
 * 
 */
public class PageRunes {
    /**
     * The number of the rune page.
     */
    private String page;
    /**
     * The id of the rune page.
     */
    private String id;
    /**
     * The key of the rune page.
     */
    private String key;
    /**
     * The icon of the rune page.
     */
    private String icon;
    /**
     * The name of the rune page.
     */
    private String name;
    /**
     * The runes of the rune page.
     */
    private HashMap<String,Rune> runes;

    /**
     * Constructor for the PageRunes class.
     * 
     * @param page The number of the rune page.
     * @param id The id of the rune page.
     * @param key The key of the rune page.
     * @param icon The icon of the rune page.
     * @param name The name of the rune page.
     */
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
