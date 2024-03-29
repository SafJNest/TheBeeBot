package com.safjnest.Utilities.Guild.Alert;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.safjnest.Bot;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class RewardData extends AlertData{
    private int level;
    private boolean temporary;
    
    public RewardData(int ID, String message, boolean enabled, HashMap<Integer, String> roles, int level, boolean temporary) {
        super(ID, message, null, enabled, AlertType.REWARD, roles);
        this.level = level;
        this.temporary = temporary;
    }


    public static RewardData createRewardData(String guild_id, String message, String[] roles, int level, boolean temporary) {
        int id = DatabaseHandler.createAlert(guild_id, message, null, AlertType.REWARD);
        HashMap<Integer, String> rolesMap = DatabaseHandler.createRolesAlert(String.valueOf(id), roles);
        DatabaseHandler.createRewardData(String.valueOf(id), level, temporary);
        return new RewardData(id, message, true, rolesMap, level, temporary);
    }

    public int getLevel() {
        return level;
    }


    /**
     * A reward is temporary if it is removed when a higher level reward is achieved
     * @return true if the reward is temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    @Override
    public EmbedBuilder getSampleEmbed(Guild guild) {
        List<String> roleNames = this.getRoles().values().stream()
            .map(role -> "@" + guild.getRoleById(role).getName())
            .collect(Collectors.toList());


        String sampleText = super.getMessage();
        sampleText = sampleText.replace("#user", "@sunyx");
        sampleText = sampleText.replace("#level", "117");
        sampleText = sampleText.replace("#role", String.join(", ", roleNames));

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(guild.getSelfMember().getEffectiveName(), "https://github.com/SafJNest", guild.getSelfMember().getEffectiveAvatarUrl());
        eb.setTitle(this.getType().getDescription() + "'s preview");
        eb.setDescription("```" + sampleText + "```");
        eb.setColor(Color.decode(Bot.getColor()));
        eb.setThumbnail(guild.getSelfMember().getEffectiveAvatarUrl());
        
        eb.addField("is Enabled",
                    (this.isEnabled()
                        ?"```✅ Yes```"
                        :"```❌ No```")
                    , true);
        
        eb.addField("Level", "```" + this.level + "```", true);
        
        eb.addField("Temporary",
                    (this.temporary
                        ?"```✅ Yes```"
                        :"```❌ No```")
                    , true);

        eb.addField("Roles", "```" + String.join("\n", roleNames) + "```", false);

        return eb;
    }




    @Override
    public AlertKey getKey() {
        return new AlertKey(AlertType.REWARD, this.level);
    }
    

}
