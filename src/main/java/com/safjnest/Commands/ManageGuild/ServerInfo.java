package com.safjnest.Commands.ManageGuild;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.Utilities.CommandsHandler;
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
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        int roleCharNumber;
        Guild guild = null;
        try {
            guild = (event.getJDA().getGuildById(event.getArgs())==null) ? event.getGuild() : event.getJDA().getGuildById(event.getArgs());
        } catch (Exception e) {
            guild = event.getGuild();
        }
        String args = "";
        if(!SafJNest.isInteger(args) || (Integer.parseInt(args)) > 1024 || (Integer.parseInt(args)) < 1)
            roleCharNumber = defaultRoleCharNumber;
        else
            roleCharNumber = Integer.parseInt(args);
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":desktop: **SERVER INFORMATION** :desktop:");
        eb.setThumbnail(guild.getIconUrl());
        eb.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));

        eb.addField("Server name", "```" 
                    + guild.getName() 
                    + "```", true);
        eb.addField("Owner's ID", "```" 
                    + guild.getOwnerId() 
                    + "```" , true);

        eb.addField("Server Description", "```" 
                    + ((guild.getDescription() == null) 
                        ? "Description not found" 
                        : guild.getDescription()) 
                    + "```", false);

        eb.addField("Server's ID", "```" 
                    + guild.getId()
                    + "```" , true);
        eb.addField("Region", "```" 
                    + guild.getLocale().toString() 
                    + "```", true);

        eb.addField("Total number of members [" 
                    + String.valueOf(guild.getMemberCount()) + "]", "```"
                    + "Members: " + guild.getMembers().stream()
                                    .filter(member -> !member.getUser().isBot()).count()
                    + " | Bots: " + guild.getMembers().stream()
                                    .filter(member -> member.getUser().isBot()).count()
                    + "```", false);

        eb.addField("Tier boot", "```" 
                    + guild.getBoostTier().name() 
                    + "```", true);
        eb.addField("Boost number", "```" 
                    + String.valueOf(guild.getBoostCount()) 
                    + "```", true);
        eb.addField("Booster role", "```" 
                    + (guild.getBoostRole() == null
                        ? "NONE"
                        : guild.getBoostRole().getName())
                    + "```", true);
        String query = "SELECT message_text FROM welcome_message WHERE discord_id = '" + event.getGuild().getId() + "';"; 
        
                    eb.addField("Welcome Message", "```" 
                    + ((DatabaseHandler.getSql().getString(query, "message_text") == null)
                        ? "There isn't a welcome message setted for this guild, use /help setwelcome for more information"
                        : DatabaseHandler.getSql().getString(query, "message_text"))
                    +  "```", true);

        eb.addField("Categories and channel [" + guild.getChannels().size() + "]", "```" 
                    +    "Categories: " + guild.getCategories().size() 
                    + " | Text channel: " + guild.getTextChannels().size() 
                    + " | Voice channel: " + guild.getVoiceChannels().size() 
                    + "```", false);
        eb.addField("Emojies [" +(guild.getEmojis().size()+guild.getStickers().size()) + "]", "```" 
                    +    "Emojies: " + guild.getEmojis().stream()
                                        .filter(emote -> emote.isAvailable()).count()
                    + " | Gif: " + guild.getStickers().stream()
                                        .filter(emote -> emote.isAvailable()).count()
                    + "```", false);

        eb.addField("Explicit content level", "```" 
                    + guild.getExplicitContentLevel().name() 
                    + "```", true);
        eb.addField("NSFW level", "```" 
                    + guild.getNSFWLevel().toString() 
                    + "```", true);
        List<String> RoleNames = PermissionHandler.getMaxFieldableRoleNames(guild.getRoles(), roleCharNumber);
        eb.addField("Server roles [" 
                    + guild.getRoles().size() + "] (printed " 
                    + RoleNames.size() + ")" , "```" 
                    + RoleNames.toString().substring(1, RoleNames.toString().length() - 1) 
                    + "```", false);

        eb.addField("Required MFA level", "```" 
                    + guild.getRequiredMFALevel().toString() 
                    + "```", true);
        

        eb.addField("Server created on", "```" 
                    + DateHandler.formatDate(guild.getTimeCreated()) 
                    + "```", false);
        
        event.reply(eb.build());

        //event.reply(guild.getCategories().toString())
        //event.reply(guild.getChannels().toString());
    }
}