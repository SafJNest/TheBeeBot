package com.safjnest.SlashCommands.Settings.LevelUp;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

public class LevelUpPreviewSlash extends SlashCommand{

    public LevelUpPreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        if(!DatabaseHandler.isExpEnabled(guildId, botId)) {
            event.deferReply(true).addContent("This guild doesn't have the exp system enabled.").queue();
            return;
        }

        String levelupMessage = DatabaseHandler.getLevelUp(guildId, botId).get("levelup_message");

        if(levelupMessage == null){
            event.deferReply(true).addContent("No level up message found.").queue();
            return;
        }

        levelupMessage = levelupMessage.replace("#user", event.getUser().getAsMention());
        levelupMessage = levelupMessage.replace("#level", "117");

        String message = "Level Up message:\n" + levelupMessage;
        
        QueryResult rooms = DatabaseHandler.getRoomsSettingsWithExpModifier(guildId);

        if(!rooms.isEmpty()) {
            message += "\n\nChannel with exp Modifier:\n";
            for(ResultRow room : rooms) {
                message += event.getGuild().getTextChannelById(room.get("room_id")).getAsMention() + " exp: " + room.get("exp_value") + "\n";
            }
        }

        rooms = DatabaseHandler.getRoomsSettingsWithoutExp(guildId);
        if(!rooms.isEmpty()){
            message += "\n\nChannel with exp system disabled:\n";
            for(ResultRow room : rooms){
                message += event.getGuild().getTextChannelById(room.get("room_id")).getAsMention() + "\n";
            }
        }

        event.deferReply(false).addContent(message).queue();
    }
}