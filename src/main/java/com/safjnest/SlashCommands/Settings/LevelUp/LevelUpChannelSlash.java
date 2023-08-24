package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.Guild.Room;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpChannelSlash extends SlashCommand{
    private GuildSettings gs;
    public LevelUpChannelSlash(String father, GuildSettings gs){
        this.gs = gs;
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "Channel to active/disable exp gain", true)
                            .setChannelTypes(ChannelType.TEXT),
            new OptionData(OptionType.BOOLEAN, "value", "TRUE OR FALSE", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        SQL sql = DatabaseHandler.getSql();
        String channel = event.getOption("channel").getAsChannel().getId();
        boolean buly = event.getOption("value").getAsBoolean();
        String query = "INSERT INTO rooms_settings (`guild_id`, `room_id`, `has_exp`)" + 
                "VALUES ('"+event.getGuild().getId()+"', '"+channel+"', '"+(buly ? 1 : 0)+"')" + 
                "ON DUPLICATE KEY UPDATE has_exp = "+ (buly ? 1 : 0) +";";
                
        sql.runQuery(query);
        if(gs.getServer(event.getGuild().getId()).getRoom(Long.parseLong(channel)) == null){
            Room r = new Room(Long.parseLong(channel),null, buly, "1", true);
            gs.getServer(event.getGuild().getId()).addRoom(r);
        }else{
           gs.getServer(event.getGuild().getId()).setExpSystemRoom(Long.parseLong(channel), buly); 
        }
        if(!buly){
            event.deferReply(false).addContent("You will no more gain exp on this channel!").queue();
            return;
        }
        event.deferReply(false).addContent("You will gain exp on this channel!").queue();
    }
    
}
