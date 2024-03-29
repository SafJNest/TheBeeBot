package com.safjnest.SlashCommands.Settings;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PrefixSlash extends SlashCommand{

    GuildSettings gs;
    public PrefixSlash(GuildSettings gs){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "prefix", "New Prefix", true));
        this.gs = gs;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(gs.getServer(event.getGuild().getId()).setPrefix(event.getOption("prefix").getAsString()))
            event.deferReply(false).addContent("The new Prefix is " + event.getOption("prefix").getAsString()).queue();
        else
            event.deferReply(true).addContent("Error").queue();   
    }
}
