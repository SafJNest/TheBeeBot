package com.safjnest.SlashCommands.Settings.Leave;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;

public class LeaveDeleteSlash extends SlashCommand{

    public LeaveDeleteSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String query = "DELETE from left_message WHERE guild_id = '" + event.getGuild().getId()
                           + "' AND bot_id = '" + event.getJDA().getSelfUser().getId() + "';";
        DatabaseHandler.getSql().runQuery(query);
        event.deferReply(false).addContent("Left message disable successfully").queue();
    }
    
}
