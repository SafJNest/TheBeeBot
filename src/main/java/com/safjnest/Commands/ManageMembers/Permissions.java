package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Permissions extends Command{
    public Permissions(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        Member theGuy = null;
        String per = "";
        try {
            
            if(event.getMessage().getMentions().getMembers().size() > 0)
                theGuy = event.getMessage().getMentions().getMembers().get(0);
            else
                theGuy = event.getGuild().retrieveMemberById(event.getArgs()).complete();
            if (theGuy.isOwner())
                event.reply(theGuy.getAsMention() + " is the owner of the server.");
            else if (theGuy.hasPermission(Permission.ADMINISTRATOR))
                event.reply(theGuy.getAsMention() + " is an admin.");
            else{
                for(Permission p :  theGuy.getPermissions())
                    per+=p.getName() + "\n";
                event.reply(theGuy.getAsMention() + " is not an admin\nThese are his permissions: " + per);
            }
        } catch (Exception e) {
            event.replyError("sorry, " + e.getMessage());
        }
    }
}
