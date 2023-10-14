package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorHandler;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Unban extends Command{

    public Unban(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            Member selfMember = event.getGuild().getSelfMember();
            if(!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
                event.reply(selfMember.getAsMention() + " doesn't have the permission to unban users, give the bot a role that can do that.");
                return;
            }// if the bot doesnt have the BAN_MEMBERS permission it cant see or unban the banned users
            
            if(event.getArgs().length() == 0) {
                StringBuilder unbans = new StringBuilder();

                unbans.append("**List of banned users:**\n");
                for (net.dv8tion.jda.api.entities.Guild.Ban ban : event.getGuild().retrieveBanList().complete())
                    unbans.append(ban.getUser().getAsMention() + " - ");
                unbans.delete(unbans.length() - 3, unbans.length());

                event.reply(unbans.toString());
            }
            else {
                String mentionedName = event.getArgs();
                
                User mentionedUser = PermissionHandler.getMentionedUser(event, mentionedName);
                Member author = event.getMember();

                if(mentionedUser == null) { 
                    event.reply("Couldn't find the specified user, please write the id of a banned user. You can also use unban without parameters to get a list of banned members.");
                }// if you mention a user not in the guild or write a wrong id

                else if(!PermissionHandler.isUserBanned(event.getGuild(), mentionedUser)) {
                    event.reply("The user is not banned from this guild.");
                }// if the user is not banned from the guild

                else if(!author.hasPermission(Permission.BAN_MEMBERS)) {
                    event.reply("You don't have the permission to unban.");
                }// if the author doesnt have the BAN_MEMBERS permission

                else {
                    event.getGuild().unban(mentionedUser).queue(
                        (e) -> event.reply(mentionedUser.getAsMention() + " has been unbanned"), 
                        new ErrorHandler().handle(
                            ErrorResponse.MISSING_PERMISSIONS,
                            (e) -> event.replyError("Error. " + e.getMessage()))
                    );
                }
            }
        } catch (Exception e) {
            event.replyError("Error: " + e.getMessage());
        }
    }
}