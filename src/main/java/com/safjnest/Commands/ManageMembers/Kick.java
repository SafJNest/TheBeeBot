package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorHandler;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Kick extends Command{

    public Kick(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        User theGuy = null;
        try {
            if(event.getMessage().getMentions().getMembers().size() > 0)
                theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
            else
                theGuy = event.getJDA().retrieveUserById(event.getArgs()).complete();
            final User surelyTheGuy = theGuy;

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.KICK_MEMBERS))
                event.reply(event.getJDA().getSelfUser().getAsMention() + " you dont have the permission to kick");

            else if (PermissionHandler.isUntouchable(theGuy.getId()))
                event.reply("Dont dare touch my creators.");

            else if(PermissionHandler.isEpria(theGuy.getId()) && !PermissionHandler.isUntouchable(event.getAuthor().getId()))
                event.reply("OHHHHHHHHHHHHHHHHHHHHHHHHHHHH NON KIKKARE MEEEEEEEEEEEEEEERIO EEEEEEEEEEEEEEEEEPRIA");

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.KICK_MEMBERS)) {
                event.getGuild().kick(surelyTheGuy).queue(
                                                (e) -> event.reply("Kicked " + surelyTheGuy.getAsMention()), 
                                                new ErrorHandler().handle(
                                                    ErrorResponse.MISSING_PERMISSIONS,
                                                        (e) -> event.replyError("error: " + e.getMessage()))
                );
            }else
                event.reply("Dont kick if you are not an admin UwU.");
        } catch (Exception e) {
            event.replyError("error: catched " + e.getMessage());
        }
    }
}
