package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpToggleSlash extends SlashCommand{

    private GuildSettings gs;

    public LevelUpToggleSlash(GuildSettings gs, String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.gs = gs;
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "toggle", "on or off", true)
                .addChoice("on", "on")
                .addChoice("off", "off")
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        boolean toggle = event.getOption("toggle").getAsString().equalsIgnoreCase("on") ? true : false;

        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();
        
        if(!DatabaseHandler.toggleLevelUp(guildId, botId, toggle)){
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }
        
        gs.getServer(event.getGuild().getId()).setExpSystem(toggle);
        event.deferReply(false).addContent("Toggled level up message " + (toggle ? "on" : "off") + ".").queue();
    }
}