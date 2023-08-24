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

public class LevelUpModifierSlash extends SlashCommand{
    private GuildSettings gs;

    public LevelUpModifierSlash(String father, GuildSettings gs){
        this.gs = gs;
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "Channel to active/disable exp gain", true)
                            .setChannelTypes(ChannelType.TEXT),
            new OptionData(OptionType.NUMBER, "value", "Its suggested a value not higher than 1.5/2.0", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        SQL sql = DatabaseHandler.getSql();
        String channel = event.getOption("channel").getAsChannel().getId();
        double value = event.getOption("value").getAsDouble();
        String query = "INSERT INTO rooms_settings (`guild_id`, `room_id`, `exp_value`)" + 
                "VALUES ('"+event.getGuild().getId()+"', '"+channel+"', '"+value+"')" + 
                "ON DUPLICATE KEY UPDATE exp_value = "+ value +";";
        sql.runQuery(query);
        if(gs.getServer(event.getGuild().getId()).getRoom(Long.parseLong(channel)) == null){
            Room r = new Room(Long.parseLong(channel),null, true, String.valueOf(value), true);
            gs.getServer(event.getGuild().getId()).addRoom(r);
        }else{
           gs.getServer(event.getGuild().getId()).setExpValueRoom(Long.parseLong(channel), String.valueOf(value)); 
        }
        event.deferReply(false).addContent("You will gain " + value + " times the normal amount of exp in this channel").queue();
    }
    
}
