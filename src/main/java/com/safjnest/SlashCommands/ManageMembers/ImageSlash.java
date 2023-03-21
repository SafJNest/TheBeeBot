package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class ImageSlash extends SlashCommand{
    public ImageSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get the profile pic", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            User theGuy = event.getOption("user").getAsUser();
            event.deferReply(false).addContent(theGuy.getAvatarUrl()).queue();
        } catch (Exception e) {
            event.deferReply(true).addContent("error: " + e.getMessage()).queue();
        }
    }
}