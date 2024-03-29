package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorHandler;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Kick extends Command{

    public Kick(){
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String[] args = event.getArgs().split(" ", 2);
            String mentionedName = args[0];
            String reason = (args.length < 2) ? "unspecified reason" : args[1];
            
            Member selfMember = event.getGuild().getSelfMember();
            Member author = event.getMember();
            Member mentionedMember = PermissionHandler.getMentionedMember(event, mentionedName);
            
            if(mentionedMember == null) { 
                event.reply("Couldn't find the specified member, please mention or write the id of a member.");
            }// if you mention a user not in the guild or write a wrong id

            else if(!selfMember.canInteract(mentionedMember)) {
                event.reply(selfMember.getAsMention() + " can't kick a member with higher or equal highest role than itself.");
            }// if the bot doesnt have a high enough role to kick the member

            else if(!author.canInteract(mentionedMember) || author == mentionedMember) {
                event.reply("You can't kick a member with higher or equal highest role than yourself.");
            }// if the author doesnt have a high enough role to kick the member and if its not yourself!
            
            else {
                event.getGuild().kick(mentionedMember).reason(reason).queue(
                    (e) -> event.reply(mentionedMember.getAsMention() + " has been kicked"), 
                    new ErrorHandler().handle(
                        ErrorResponse.MISSING_PERMISSIONS,
                        (e) -> event.replyError("Error. " + e.getMessage()))
                );
            }
        } catch (Exception e) {
            event.replyError("Error: " + e.getMessage());
        }
    }
}