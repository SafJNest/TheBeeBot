package com.safjnest.Utilities.LOL.Runes;

/**
 * This class is used to store the data of a rune and all of its attributes.
 * 
 * 
 * <p>The runes are loaded from Riot's API and stored in a {@link com.safjnest.Utilities.LOL.RiotHandler#runesHandler list of Rune objects.}</p>
 */
public class Rune {
    /**
     * The id of the rune.
     */
    private String id;
    /**
     * The key of the rune.
     */
    private String key;
    /**
     * The icon of the rune.
     */
    private String icon;
    /**
     * The name of the rune.
     */
    private String name;
    /**
     * The short description of the rune.
     */
    private String shortDesc;
    /**
     * The long description of the rune.
     */
    private String longDesc;

    /**
     * Constructor for the Rune class.
     * 
     * @param id The id of the rune.
     * @param key The key of the rune.
     * @param icon The icon of the rune.
     * @param name The name of the rune.
     * @param shortDesc The short description of the rune.
     * @param longDesc The long description of the rune.
     */
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
