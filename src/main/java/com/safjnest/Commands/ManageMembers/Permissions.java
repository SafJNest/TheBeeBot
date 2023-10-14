package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;

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
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            Member mentionedMember;
            if(event.getArgs().equals(""))
                mentionedMember = event.getMember();
            else
                mentionedMember = PermissionHandler.getMentionedMember(event, event.getArgs());

            if(mentionedMember == null) {
                event.reply("Couldn't find the specified member, please mention or write the id of a member.");
                return;
            }

            if (mentionedMember.isOwner()) {
                event.reply(mentionedMember.getAsMention() + " is the owner of the guild.");
            }
            else if (mentionedMember.hasPermission(Permission.ADMINISTRATOR)) {
                event.reply(mentionedMember.getAsMention() + " is an admin.");
            }
            else {
                StringBuilder permissionsString = new StringBuilder();
                for(Permission permission :  mentionedMember.getPermissions())
                    permissionsString.append(permission.getName() + " - ");
                permissionsString.delete(permissionsString.length() - 3, permissionsString.length());
                event.reply(mentionedMember.getAsMention() + " **is not an admin and these are his permissions:** \n" + permissionsString.toString());
            }
        } catch (Exception e) {
            event.reply("Error: " + e.getMessage());
        }
    }
}