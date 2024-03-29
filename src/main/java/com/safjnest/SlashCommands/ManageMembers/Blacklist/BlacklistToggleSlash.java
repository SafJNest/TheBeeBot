package com.safjnest.SlashCommands.ManageMembers.Blacklist;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BlacklistToggleSlash extends SlashCommand{

    private GuildSettings gs;

    public BlacklistToggleSlash(String father, GuildSettings gs){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "toggle", "On or off.", true)
                .addChoice("on", "on")
                .addChoice("off", "off")
        );

        this.gs = gs;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        boolean toggle = event.getOption("toggle").getAsString().equalsIgnoreCase("on") ? true : false;

        if(gs.getServer(event.getGuild().getId()).getBlackChannelId() == null) {
            event.deferReply(true).addContent("This guild doesn't have blacklist set.").queue();
            return;
        }

        if(!gs.getServer(event.getGuild().getId()).setBlacklistEnabled(toggle)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Toggled blacklist " + (toggle ? "on" : "off") + ".").queue();
    }
}