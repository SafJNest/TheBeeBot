package com.safjnest.SlashCommands.Settings.Leave;

import java.util.ArrayList;
import java.util.Collections;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

public class LeaveSlash extends SlashCommand {


    public LeaveSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        String father = this.getClass().getSimpleName().replace("Slash", "");

        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.help = "json";
        
        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new LeaveMoveSlash(father), new LeavePreviewSlash(father), new LeaveTextSlash(father), new LeaveDeleteSlash(father));
        this.children = slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]);

    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }
    
}
