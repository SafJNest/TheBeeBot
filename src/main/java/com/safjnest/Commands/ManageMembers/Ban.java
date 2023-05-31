package com.safjnest.Commands.ManageMembers;

import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class Ban extends Command{

    public Ban(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {

        String[] args = event.getArgs().split(" ", 2);
        String reason = (args.length < 2) ? "unspecified reason" : args[1];
   
        
        User theGuy = null;
        try {
            if(event.getMessage().getMentions().getMembers().size() > 0)
                theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
            else
                theGuy = event.getJDA().retrieveUserById(args[0]).complete();
            final User surelyTheGuy = theGuy;

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.BAN_MEMBERS))
                event.reply(event.getJDA().getSelfUser().getAsMention() + " doesn't have the permissions to ban, give the bot an admin role");

            else if (PermissionHandler.isUntouchable(theGuy.getId()))
                event.reply("Don't you dare touch my creators.");

            else if(PermissionHandler.isEpria(theGuy.getId()) && !PermissionHandler.isUntouchable(event.getAuthor().getId()))
                event.reply("OHHHHHHHHHHHHHHHHHHHHHHHHHHHH NON BANNARE MEEEEEEEEEEEEEEERIO EEEEEEEEEEEEEEEEEPRIA");

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.BAN_MEMBERS)) {
                event.getGuild().ban(surelyTheGuy, 0, TimeUnit.SECONDS).reason(reason).queue(
                                                        (e) -> event.reply("banned " + surelyTheGuy.getAsMention()), 
                                                        new ErrorHandler().handle(
                                                            ErrorResponse.MISSING_PERMISSIONS,
                                                                (e) -> event.replyError("sorry, " + e.getMessage()))
                );
            }else
                event.reply("You can't ban if you're not an admin UwU");
        } catch (Exception e) {
            event.replyError("sorry, " + e.getMessage());
        }
    }
}
