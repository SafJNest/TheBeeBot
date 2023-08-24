package com.safjnest.Commands.ManageGuild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Bot.BotSettingsHandler;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1.02
 */
public class ServerInfo extends Command{
    private final int defaultRoleCharNumber = 200;

    public ServerInfo(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ", 2);
        int roleCharNumber;
        Guild guild = null;

        if(args.length == 1 && args[0] == "") {
            roleCharNumber = defaultRoleCharNumber;
            guild = event.getGuild();
        }
        else if(args.length == 1) {
            if(SafJNest.intIsParsable(args[0]) && (Integer.parseInt(args[0])) < 1024 && (Integer.parseInt(args[0])) > 1) {
                roleCharNumber = Integer.parseInt(args[0]);
                guild = event.getGuild();
            }
            else if(SafJNest.longIsParsable(args[0]) && event.getJDA().getGuildById(args[0]) != null) {
                roleCharNumber = defaultRoleCharNumber;
                guild = event.getJDA().getGuildById(args[0]);
            }
            else {
                event.reply("Invalid id or the bot is not in the guild");
                return;
            }
        }
        else { //args.length == 2
            if((SafJNest.intIsParsable(args[0]) && (Integer.parseInt(args[0])) < 1024 && (Integer.parseInt(args[0])) > 1) && (SafJNest.longIsParsable(args[1]) && event.getJDA().getGuildById(args[1]) != null)) {
                roleCharNumber = Integer.parseInt(args[0]);
                guild = event.getJDA().getGuildById(args[1]);
            }
            else if((SafJNest.intIsParsable(args[1]) && (Integer.parseInt(args[1])) < 1024 && (Integer.parseInt(args[1])) > 1) && (SafJNest.longIsParsable(args[0]) && event.getJDA().getGuildById(args[0]) != null)) {
                roleCharNumber = Integer.parseInt(args[1]);
                guild = event.getJDA().getGuildById(args[0]);
            }
            else {
                event.reply("Invalid id or the bot is not in the guild");
                return;
            }
        }


        ArrayList<ArrayList<String>> welcomeMessages = DatabaseHandler.getSql().getAllRows("SELECT bot_id, message_text, channel_id FROM welcome_message WHERE guild_id = '" + guild.getId() + "';");
        String welcomeMessageString = "";
        for(int i = 1; i < welcomeMessages.size(); i++) {
            welcomeMessageString += event.getJDA().getUserById(welcomeMessages.get(i).get(0)).getName()
                                    + ": " + welcomeMessages.get(i).get(1)
                                    + " [" + event.getGuild().getChannelById(TextChannel.class, welcomeMessages.get(i).get(2)).getName()  
                                    +  "]" + "\n\n";
        }

        ArrayList<ArrayList<String>> leaveMessages = DatabaseHandler.getSql().getAllRows("SELECT bot_id, message_text, channel_id FROM left_message WHERE guild_id = '" + guild.getId() + "';");
        String leaveMessageString = "";
        for(int i = 1; i < leaveMessages.size(); i++) {
            leaveMessageString += event.getJDA().getUserById(leaveMessages.get(i).get(0)).getName()
                                    + ": " + leaveMessages.get(i).get(1)
                                    + " [" + event.getGuild().getChannelById(TextChannel.class, leaveMessages.get(i).get(2)).getName()  
                                    +  "]" + "\n\n";
        }

        String lvlUpMsg = DatabaseHandler.getSql().getString("SELECT message_text FROM levelup_message WHERE guild_id = '" + guild.getId() + "';", "message_text");

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
        
        eb.addField("Welcome Message(s)", "```" 
                    + ((welcomeMessageString == "")
                        ? "No welcome message set for this guild, use /help setwelcomemessage for more information"
                        : welcomeMessageString)
                    +  "```", false);
        
        eb.addField("Leave Message(s)", "```" 
                    + ((leaveMessageString == "")
                        ? "No leave message set for this guild, use /help setleavemessage for more information"
                        : leaveMessageString)
                    +  "```", false);
        
        eb.addField("Level Up Message", "```" 
                    + ((lvlUpMsg == null)
                        ? "No levelup message set for this guild, use /help setlevelupmessage for more information"
                        : lvlUpMsg)
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

        eb.addField("Server created on", 
                      "<t:" + guild.getTimeCreated().toEpochSecond() + ":f> | "
                    + "<t:" + guild.getTimeCreated().toEpochSecond() + ":R>",
                     false);
        event.reply(eb.build());
    }
}