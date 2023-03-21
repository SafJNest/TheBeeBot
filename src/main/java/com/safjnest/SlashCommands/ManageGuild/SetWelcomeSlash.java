package com.safjnest.SlashCommands.ManageGuild;

import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class SetWelcomeSlash extends SlashCommand {
    private SQL sql;

    public SetWelcomeSlash(SQL sql) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        //this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "msg", "Welcome message", true),
            new OptionData(OptionType.CHANNEL, "channel", "User to get the information about", false)
                            .setChannelTypes(ChannelType.TEXT),
            new OptionData(OptionType.ROLE, "role", "User to get the information about", false));
        this.sql = sql;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String channel = null;
        if(event.getOption("channel") == null){
            try {
                channel = event.getGuild().getSystemChannel().getId();
            } catch (Exception e) {
                event.deferReply(true).addContent("No channel was selected and there isn't a system channel (check your server discord settings). Be sure to select a channel next time.").queue();
                return;
            }
        }else{
            channel = event.getOption("channel").getAsChannel().getId();
        }
        String message = event.getOption("msg").getAsString();
        message = message.replace("'", "''");
        
        String discordId = event.getGuild().getId();
        String query = "INSERT INTO welcome_message(discord_id, channel_id, message_text, bot_id)"
                            + "VALUES('" + discordId + "','" + channel +"','" + message + "','"+event.getJDA().getSelfUser().getId()+"');";
        sql.runQuery(query);
        if(event.getOption("role") != null){
            query = "INSERT INTO welcome_roles(role_id, discord_id, bot_id)"
                                + "VALUES('" + event.getOption("role").getAsRole().getId() + "','" + discordId + "','"+event.getJDA().getSelfUser().getId()+"');";
            sql.runQuery(query);
        }

        
        event.deferReply(false).addContent("All set correctly").queue();
    }
}