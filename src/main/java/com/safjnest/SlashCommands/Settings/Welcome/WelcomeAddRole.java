package com.safjnest.SlashCommands.Settings.Welcome;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertType;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WelcomeAddRole extends SlashCommand{

    public WelcomeAddRole(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(this.name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.ROLE, "role", "Role that will be given to the new members.", true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String roleID = event.getOption("role") != null ? event.getOption("role").getAsString() : null;

        String guildId = event.getGuild().getId();

        GuildData gs = Bot.getGuildData(guildId);

        AlertData welcome = gs.getAlert(AlertType.WELCOME);

        if (welcome == null) {
            event.deferReply(true).addContent("This guild doesn't have a welcome message.").queue();
            return;
        }

        if (welcome.getRoles() != null && welcome.getRoles().containsValue(roleID)) {
            event.deferReply(true).addContent("This role is already in the welcome message.").queue();
            return;
        }

        if(!welcome.addRole(roleID)) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        event.deferReply(false).addContent("Add welcome role.").queue();
    }
}
