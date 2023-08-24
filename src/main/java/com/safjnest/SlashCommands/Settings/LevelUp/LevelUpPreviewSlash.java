package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;

public class LevelUpPreviewSlash extends SlashCommand{

    public LevelUpPreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String query = "SELECT message_text FROM levelup_message WHERE guild_id = '" + event.getGuild().getId() + "';";
        String message = DatabaseHandler.getSql().getString(query, "message_text");
        if(message == null){
            event.deferReply(false).addContent("You have not set a LevelUp message yet.").queue();
            return;
        }
        message = message.replace("#user", event.getUser().getAsMention());
        message = message.replace("#level", String.valueOf(117));
        String msg = "Level Up message:\n" + message;
        
        query = "SELECT room_id, has_exp, exp_value from rooms_settings WHERE guild_id = '"+ event.getGuild().getId() +"' AND exp_value > 1;";
        ArrayList<ArrayList<String>> rooms = DatabaseHandler.getSql().getAllRows(query, 3);
        if(rooms.size() != 0){
            msg += "\n\nChannel with exp Modifier:\n";
            for(ArrayList<String> room : rooms){
                msg+= event.getGuild().getTextChannelById(room.get(0)).getAsMention() + " exp: " + room.get(2) + "\n";
            }
        }

        query = "SELECT room_id, has_exp, exp_value from rooms_settings WHERE guild_id = '"+ event.getGuild().getId() +"' AND has_exp = 0;";
        rooms = DatabaseHandler.getSql().getAllRows(query, 3);
        if(rooms.size() != 0){
            msg += "\n\nChannel with exp system disabled:\n";
            for(ArrayList<String> room : rooms){
                msg+= event.getGuild().getTextChannelById(room.get(0)).getAsMention() + "\n";
            }
        }

        event.deferReply(false).addContent(msg).queue();
        
    }
    
}
