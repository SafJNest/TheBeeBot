package com.safjnest.SlashCommands.Settings.Reward;

import java.util.ArrayList;
import java.util.Collections;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

public class RewardSlash extends SlashCommand {

    public RewardSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        String father = this.getClass().getSimpleName().replace("Slash", "");

        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.category = new Category("Settings");
        this.help = "json";
        
        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new RewardCreateSlash(father), new RewardTextSlash(father), new RewardAddRoleSlash(father), new RewardRemoveRoleSlash(father), new RewardDeleteSlash(father), new RewardPreviewSlash(father));
        this.children = slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]);                                 
    }

    @Override
    protected void execute(SlashCommandEvent event) { }
    
}
