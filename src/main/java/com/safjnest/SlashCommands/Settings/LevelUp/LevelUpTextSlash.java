package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpTextSlash extends SlashCommand{

    public LevelUpTextSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "msg", "Welcome message", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
       String message = event.getOption("msg").getAsString();
        message = message.replace("'", "''");
        
        String discordId = event.getGuild().getId();
        String query = "SELECT exp_enabled FROM guild_settings WHERE guild_id = '" + discordId + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        if(DatabaseHandler.getSql().getString(query, "exp_enabled") != null && DatabaseHandler.getSql().getString(query, "exp_enabled").equals("0")){
            event.deferReply(false).addContent("You have not enabled the exp system yet.").queue();
            return;
        }
        query = "INSERT INTO levelup_message(guild_id, message_text)"
                            + "VALUES('" + discordId + "','" + message +"');";
        if(!DatabaseHandler.getSql().runQuery(query)){
            query = "UPDATE levelup_message SET message_text = '" + message + "' WHERE guild_id = '" + discordId + "';"; 
            DatabaseHandler.getSql().runQuery(query);
            event.deferReply(false).addContent("Set a new LevelUp message.").queue();
            return;
        }
        event.deferReply(false).addContent("All set correctly.").queue();
    }
    
}