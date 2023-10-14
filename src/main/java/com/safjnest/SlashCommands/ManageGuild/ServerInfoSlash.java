package com.safjnest.SlashCommands.ManageGuild;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;


import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1.02
 */
public class ServerInfoSlash extends SlashCommand{
    private final int defaultRoleCharNumber = 200;

    public ServerInfoSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "rolecharnumber", "Max number of charachters the roles filed can be (1 to 1024)", false)
                .setMinValue(1)
                .setMaxValue(1024)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();

        int roleCharNumber = (event.getOption("roleCharNumber") == null) ? defaultRoleCharNumber : event.getOption("roleCharNumber").getAsInt();

        ResultRow alerts = DatabaseHandler.getAlert(event.getGuild().getId(), event.getJDA().getSelfUser().getId());
        ResultRow settings = DatabaseHandler.getGuildData(event.getGuild().getId(), event.getJDA().getSelfUser().getId());

        String welcomeMessageString = null;
        if(alerts.get("welcome_message") != null) {
            welcomeMessageString = alerts.get("welcome_message")
                + " [" + event.getJDA().getTextChannelById(alerts.get("welcome_channel")).getName() + "]"
                + " [" + (alerts.getAsBoolean("welcome_enabled") ? "on" : "off") + "]"
            + "\n\n";
        }
        
        String leaveMessageString = null;
        if(alerts.get("leave_message") != null) {
            leaveMessageString = alerts.get("leave_message")
                + " [" + event.getJDA().getChannelById(TextChannel.class, alerts.get("leave_channel")).getName() + "]"
                + " [" + (alerts.getAsBoolean("leave_enabled") ? "on" : "off") + "]"
            + "\n\n";
        }

        String blacklistString = null;
        if(settings.get("blacklist_channel") != null) {
            blacklistString = "A total of " + DatabaseHandler.getBannedTimesInGuild(guild.getId()) + " users have been banned from this guild"
                + " [" + event.getJDA().getChannelById(TextChannel.class, settings.get("blacklist_channel")).getName() + "]"
                + " [" + (settings.getAsBoolean("blacklist_enabled") ? "on" : "off") + "]"
            + "\n\n";
        }

        String lvlUpString = alerts.get("lvlup_message");

        List<String> RoleNames = PermissionHandler.getMaxFieldableRoleNames(guild.getRoles(), roleCharNumber);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":desktop: **SERVER INFORMATION** :desktop:");
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));

        eb.addField("Server name", "```" + guild.getName() + "```", true);

        eb.addField("Owner ID", "```" + guild.getOwnerId() + "```" , true);

        eb.addField("Server Description", "```" 
            + ((guild.getDescription() == null) 
                ? "No description" 
                : guild.getDescription()) 
        + "```", false);

        eb.addField("Server ID", "```" + guild.getId() + "```" , true);

        eb.addField("Region", "```" + guild.getLocale().toString() + "```", true);

        eb.addField("Total number of members [" + String.valueOf(guild.getMemberCount()) + "]", "```"
            + "Members: " + guild.getMembers().stream()
                .filter(member -> !member.getUser().isBot()).count()
            + " | Bots: " + guild.getMembers().stream()
                .filter(member -> member.getUser().isBot()).count()
        + "```", false);

        eb.addField("Boost tier", "```" + guild.getBoostTier().name() + "```", true);

        eb.addField("Boost number", "```" + String.valueOf(guild.getBoostCount()) + "```", true);

        eb.addField("Booster role", "```" 
            + (guild.getBoostRole() == null
                ? "NONE"
                : guild.getBoostRole().getName())
        + "```", true);
        
        eb.addField("Welcome Message", "```" 
            + ((welcomeMessageString == null)
                ? "No welcome message set for this guild, use /help welcome for more information"
                : welcomeMessageString)
        +  "```", false);
        
        eb.addField("Leave Message", "```" 
            + ((leaveMessageString == null)
                ? "No leave message set for this guild, use /help leave for more information"
                : leaveMessageString)
        +  "```", false);
        
        eb.addField("Level Up Message", "```" 
            + ((lvlUpString == null)
                ? "No levelup message set for this guild, use /help levelup for more information"
                : lvlUpString)
        +  "```", false);

        eb.addField("Blacklist", "```" 
            + ((blacklistString == null)
                ? "No blacklist set for this guild, use /help blacklist for more information"
                : blacklistString)
        +  "```", false);

        eb.addField("Categories and channels [" + guild.getChannels().size() + "]", "```" 
            +    "Categories: "    + guild.getCategories().size() 
            + " | Text channels: "  + guild.getTextChannels().size() 
            + " | Voice channels: " + guild.getVoiceChannels().size() 
            + " | Stage channels: " + guild.getStageChannels().size() 
            + " | Announcement channels: " + guild.getForumChannels().size()
        + "```", false);

        eb.addField("Emojis [" + (guild.getEmojis().size()+guild.getStickers().size()) + "]", "```" 
            +    "Emojis: " + guild.getEmojis().stream()
                                .filter(emote -> emote.isAvailable()).count()
            + " | Gifs: " + guild.getStickers().stream()
                                .filter(emote -> emote.isAvailable()).count()
        + "```", false);

        eb.addField("Explicit content", "```" + guild.getExplicitContentLevel().name() + "```", true);

        eb.addField("NSFW", "```" + guild.getNSFWLevel().toString() + "```", true);

        eb.addField("Required MFA", "```" + guild.getRequiredMFALevel().toString() + "```", true);

        eb.addField("Server roles [" + guild.getRoles().size() + "] (printed " + RoleNames.size() + ")" , "```" 
            + ((RoleNames.size() > 0)
                ? RoleNames.toString().substring(1, RoleNames.toString().length() - 1)
                : "NO ROLES")
        + "```", false);

        eb.addField("Server created on", "<t:" + guild.getTimeCreated().toEpochSecond() + ":f> | "
            + "<t:" + guild.getTimeCreated().toEpochSecond() + ":R>",
        false);

        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}