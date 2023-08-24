package com.safjnest.SlashCommands.Settings;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Commands.CommandsLoader;

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
public class SetLevelUpMessageSlash extends SlashCommand {

    public SetLevelUpMessageSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "msg", "Welcome message", true));
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        String message = event.getOption("msg").getAsString();
        message = message.replace("'", "''");
        
        String discordId = event.getGuild().getId();
        String query = "INSERT INTO levelup_message(discord_id, message_text)"
                            + "VALUES('" + discordId + "','" + message +"');";
        if(!DatabaseHandler.getSql().runQuery(query)){
            query = "UPDATE levelup_message SET message_text = '" + message + "' WHERE discord_id = '" + discordId + "';"; 
            DatabaseHandler.getSql().runQuery(query);
            event.deferReply(false).addContent("Set a new LevelUp message.").queue();
            return;
        }
        event.deferReply(false).addContent("All set correctly.").queue();
    }
}