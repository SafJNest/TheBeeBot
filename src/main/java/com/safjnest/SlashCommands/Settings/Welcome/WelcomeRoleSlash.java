package com.safjnest.SlashCommands.Settings.Welcome;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WelcomeRoleSlash extends SlashCommand{

    public WelcomeRoleSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.ROLE, "role", "Role that will be given to the new members (leave out to unset).", false)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String roleID = event.getOption("role") != null ? event.getOption("role").getAsString() : null;

        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        if(!DatabaseHandler.hasWelcome(guildId, botId)) {
            event.deferReply(true).addContent("This guild doesn't have a welcome message.").queue();
            return;
        }

        if(!DatabaseHandler.updateWelcomeRole(guildId, botId, roleID)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Changed welcome role.").queue();
    }
}