package com.safjnest.SlashCommands.Settings.Welcome;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WelcomeTextSlash extends SlashCommand{
    
    public WelcomeTextSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
        new OptionData(OptionType.STRING, "msg", "Welcome message", true),
        new OptionData(OptionType.CHANNEL, "channel", "Channel where the message will be sent. Null to use default channel.", false)
                        .setChannelTypes(ChannelType.TEXT),
        new OptionData(OptionType.ROLE, "role", "Role that will be given to new members.", false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        SQL sql = DatabaseHandler.getSql();
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
        String query = "INSERT INTO welcome_message(guild_id, channel_id, message_text, bot_id)"
                            + "VALUES('" + discordId + "','" + channel +"','" + message + "','"+event.getJDA().getSelfUser().getId()+"');";
        sql.runQuery(query);
        if(event.getOption("role") != null){
            query = "INSERT INTO welcome_roles(role_id, guild_id, bot_id)"
                                + "VALUES('" + event.getOption("role").getAsRole().getId() + "','" + discordId + "','"+event.getJDA().getSelfUser().getId()+"');";
            sql.runQuery(query);
        }

        
        event.deferReply(false).addContent("All set correctly").queue();
    }
    
}
