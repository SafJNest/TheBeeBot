package com.safjnest.Utilities.Guild;

/**
 * Class that stores all the settings for a guild.
 * <ul>
 * <li>Prefix</li>
 * <li>ID</li>
 * </ul>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class GuildData {
    /**Server ID */
    private Long id;
    /**Prefix Server */
    private String prefix;

    /**
     * default constructor
     * @param id
     * @param prefix
     */
    public GuildData(Long id, String prefix) {
        this.id = id;
        this.prefix = prefix;
    }

    public Long getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }
}
