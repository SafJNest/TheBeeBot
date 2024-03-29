package com.safjnest.Utilities.Guild.Alert;

import java.awt.*;
import java.util.HashMap;

import com.safjnest.Bot;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class AlertData {

    private final int ID;
    private String message;
    private String channelId;
    private boolean enabled;
    private AlertType type;
    private HashMap<Integer, String> roles;

    public AlertData(int ID, String message, String channelId, boolean enabled, AlertType type, HashMap<Integer, String> roles) {
        this.ID = ID;
        this.message = message;
        this.channelId = channelId;
        this.enabled = enabled;
        this.type = type;
        this.roles = roles;
    }


    /**
     * Constructor for the alert message
     * @param guild_id
     * @param bot_id
     * @param message
     * @param channelId
     * @param enabled
     * @param type
     */
    public AlertData(String guild_id, String message, String channelId, AlertType type) {
        this.ID = DatabaseHandler.createAlert(guild_id, message, channelId, type);
        this.message = message;
        this.channelId = channelId;
        this.enabled = true;
        this.type = type;
    }

    /**
     * Constructor for the welcome message
     * @param guild_id
     * @param bot_id
     * @param message
     * @param channelId
     * @param roles
     */
    public AlertData(String guild_id, String message, String channelId, String[] roles) {
        this.ID = DatabaseHandler.createAlert(guild_id, message, channelId, AlertType.WELCOME);
        this.message = message;
        this.channelId = channelId;
        this.enabled = true;
        this.type = AlertType.WELCOME;
        this.roles = DatabaseHandler.createRolesAlert(String.valueOf(this.ID), roles);
    }

    /**
     * Constructor for the level up message
     * @param guild_id
     * @param message
     */
    public AlertData(String guild_id, String message) {
        this.ID = DatabaseHandler.createAlert(guild_id, message, null, AlertType.LEVEL_UP);
        this.message = message;
        this.channelId = null;
        this.enabled = true;
        this.type = AlertType.LEVEL_UP;
    }

    /**
     * An alert is valid if and only if it has a message, a channel (except for level up messages) and it's enabled (true by default)
     * @return true if the alert is valid, false otherwise
     */
    public boolean isValid() {
        return this.message != null && (this.type == AlertType.LEVEL_UP ||  this.channelId != null) && this.enabled;
    }
    
    public boolean setMessage(String message) {
        boolean result = DatabaseHandler.setAlertMessage(String.valueOf(this.ID), message);
        if (result) {
            this.message = message;
        }
        return result;
    }

    public boolean setAlertChannel(String channelId) {
        boolean result = DatabaseHandler.setAlertChannel(String.valueOf(this.ID), channelId);
        if (result) {
            this.channelId = channelId;
        }
        return result;
    }

    public boolean setEnabled(boolean enabled) {
        boolean result = DatabaseHandler.setAlertEnabled(String.valueOf(this.ID), enabled);
        if (result) {
            this.enabled = enabled;
        }
        return result;
    }

    /**
     * The method first adds the new role to the local roles map at position 0, 
     * because the actual database row ID for the new role is not known yet.
     * 
     * It then calls a database operation that deletes all existing roles for 
     * the alert and re-adds them, including the new role. This operation also 
     * assigns actual database row IDs to the roles.
     * 
     * The updated roles are then assigned back to the local roles map.
     * 
     * @param roleId The ID of the new role to be added.
     * @return true if the operation was successful, false otherwise.
     * 
     */
    public boolean addRole(String roleId) {
        HashMap<Integer, String> roles = this.roles;
        if (roles == null) {
            roles = new HashMap<>();
        }
        roles.put(0, roleId);
        this.roles = DatabaseHandler.createRolesAlert(String.valueOf(this.ID), roles.values().toArray(new String[0]));
        return this.roles != null;
    }


    /**
     * The method first removes the role from the local roles map, 
     * using the provided role ID.
     * 
     * It then calls a database operation that deletes all existing roles for 
     * the alert and re-adds them, excluding the removed role. This operation also 
     * assigns actual database row IDs to the roles.
     * 
     * The updated roles are then assigned back to the local roles map.
     * 
     * @param roleId The ID of the role to be removed.
     * @return true if the operation was successful, false otherwise.
     * 
     */
    public boolean removeRole(String roleId) {
        HashMap<Integer, String> roles = this.roles;
        if (roles == null || !roles.containsValue(roleId)) {
            return false;
        }
        roles.values().removeIf(role -> role.equals(roleId));
        this.roles = DatabaseHandler.createRolesAlert(String.valueOf(this.ID), roles.values().toArray(new String[0]));
        return this.roles != null;
    }

    public boolean terminator4LaRinascita() {
        return DatabaseHandler.deleteAlert(String.valueOf(this.ID));
    }



    public int getID() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    public String getChannelId() {
        return channelId;
    }

    public AlertType getType() {
        return type;
    }

    public HashMap<Integer, String> getRoles() {
        return roles;
    }

    public String[] getRolesAsArray() {
        return roles.values().toArray(new String[0]);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getFormattedSample(Guild guild) {
        String sampleText = this.message;
        sampleText = sampleText.replace("#user", "@sunyx");
        sampleText = sampleText.replace("#level", "117");

        String channelText = "This alert has not a channel set.";
        if (this.type == AlertType.LEVEL_UP) {
            channelText = "";
        }
        else if (this.getChannelId() != null) {
            channelText = "This message would be sent to: " + guild.getTextChannelById(this.getChannelId()).getAsMention() + "\n";
        }

        String roleText = "";
        if (this.type == AlertType.WELCOME && this.getRoles() != null) {
            roleText += "Roles that would be given to the user:";
            for (String role : this.getRoles().values().toArray(new String[0])) {
                roleText += "\n" + guild.getRoleById(role).getName();
            }
        }
        return
               "The " + this.type.getDescription() + " is " + (this.enabled ? "enabled" : "disabled") + ":\n"
               + "```"
               + sampleText
               + "```"
               + channelText
               + roleText;
    }

    public EmbedBuilder getSampleEmbed(Guild guild) {
        String sampleText = this.message;
        sampleText = sampleText.replace("#user", "@sunyx");
        sampleText = sampleText.replace("#level", "117");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(guild.getSelfMember().getEffectiveName(), "https://github.com/SafJNest", guild.getSelfMember().getEffectiveAvatarUrl());
        eb.setTitle(this.getType().getDescription() + "'s preview");
        eb.setDescription("```" + sampleText + "```");
        eb.setColor(Color.decode(Bot.getColor()));
        eb.setThumbnail(guild.getSelfMember().getEffectiveAvatarUrl());

        String channelText = "This alert has not a channel set.";
        if (this.type == AlertType.LEVEL_UP) {
            channelText = "Level up has not a channel";
        }
        else if (this.getChannelId() != null) {
            channelText = guild.getTextChannelById(this.getChannelId()).getName();
        }
        eb.addField("Channel", "```" + channelText + "```", true);

        eb.addField("is Enabled",
                    (this.isEnabled()
                        ?"```✅ Yes```"
                        :"```❌ No```")
                    , true);
        

        if (this.type == AlertType.WELCOME) {
            if (this.getRoles() == null) {
                eb.addField("Roles", "```Zero roles setted.```", false);
            }
            else {
                String roleText = "```";
                for (String role : this.getRoles().values().toArray(new String[0])) {
                    roleText += guild.getRoleById(role).getName() + "\n";
                }
                roleText += "```";
                eb.addField("Roles", roleText, false);
            }
        }


        return eb;
    }


    public AlertKey getKey() {
        return new AlertKey(this.type);
    }

    @Override
    public String toString() {
        return "AlertData [ID=" + ID + ", channelId=" + channelId + ", enabled=" + enabled + ", message=" + message
                + ", roles=" + roles + ", type=" + type + "]";
    }

    
}