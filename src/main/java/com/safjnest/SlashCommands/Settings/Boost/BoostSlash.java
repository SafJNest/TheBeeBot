package com.safjnest.SlashCommands.Settings.Boost;

import java.util.ArrayList;
import java.util.Collections;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

public class BoostSlash extends SlashCommand {

    public BoostSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        String father = this.getClass().getSimpleName().replace("Slash", "");

        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.help = "json";
        
        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new BoostPreviewSlash(father), new BoostTextSlash(father), new BoostMoveSlash(father), new BoostDeleteSlash(father));
        this.children = slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]);                            
    }

    @Override
    protected void execute(SlashCommandEvent event) {
    }
    
}
