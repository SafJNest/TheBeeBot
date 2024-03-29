package com.safjnest.Utilities;

import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public class CustomEmoji {
    private String id;
    private String guildId;
    private String name;
    private boolean animated;
    private RichCustomEmoji richCustomEmoji;

    public CustomEmoji(String id, String guildId, String name, RichCustomEmoji richCustomEmoji){
        this.id = id;
        this.guildId = guildId;
        this.name = name;
        this.richCustomEmoji = richCustomEmoji;
        this.animated = richCustomEmoji.isAnimated();
    }

    public String getId(){
        return this.id;
    }

    public String getGuildId(){
        return this.guildId;
    }

    public String getName(){
        return this.name;
    }

    public RichCustomEmoji getObject(){
        return this.richCustomEmoji;
    }

    public String toString(){
        return "<"+ (animated ? "a" : "") +":" + this.name + ":" + this.id + ">";
    }
}
