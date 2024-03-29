package com.safjnest.SlashCommands.Settings.Welcome;

import java.util.ArrayList;
import java.util.Collections;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

public class WelcomeSlash extends SlashCommand{

    public WelcomeSlash(GuildSettings gs){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        String father = this.getClass().getSimpleName().replace("Slash", "");

        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.help = "json";
        
        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new WelcomeChannelSlash(father), new WelcomeCreateSlash(father), new WelcomeDeleteSlash(father), new WelcomePreviewSlash(father), new WelcomeTextSlash(father), new WelcomeToggleSlash(father), new WelcomeAddRole(father), new WelcomeRemoveRole(father));
        this.children = slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]);                                 
    }

    @Override
    protected void execute(SlashCommandEvent event) {

    }
}