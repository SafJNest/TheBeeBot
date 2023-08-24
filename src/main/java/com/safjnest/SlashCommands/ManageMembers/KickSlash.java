package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class KickSlash extends SlashCommand{

    public KickSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get kicked out of marons", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        User theGuy = event.getOption("user").getAsUser();
        try {
            final User surelyTheGuy = theGuy;

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.KICK_MEMBERS))
                event.deferReply(true).addContent(event.getJDA().getSelfUser().getAsMention() + " you dont have the permission to kick").queue();

            else if (PermissionHandler.isUntouchable(theGuy.getId()))
                event.deferReply(true).addContent("Dont dare touch my creators.").queue();

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.KICK_MEMBERS)) {
                event.getGuild().kick(surelyTheGuy).queue(
                                                (e) -> event.deferReply(false).addContent("Kicked " + surelyTheGuy.getAsMention()).queue(), 
                                                new ErrorHandler().handle(
                                                    ErrorResponse.MISSING_PERMISSIONS,
                                                        (e) -> event.deferReply(true).addContent("error: " + e.getMessage()).queue())
                );
            }else
                event.deferReply(true).addContent("Dont kick if you are not an admin UwU.").queue();
        } catch (Exception e) {
            event.deferReply(true).addContent("error: catched " + e.getMessage()).queue();
        }
    }
}
