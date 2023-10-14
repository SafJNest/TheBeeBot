package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class ModifyNickname extends Command {
    /**
     * Default constructor for the class.
     */
    public ModifyNickname() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.botPermissions = new Permission[]{Permission.NICKNAME_MANAGE};
        this.userPermissions = new Permission[]{Permission.NICKNAME_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            String[] args = event.getArgs().split(" ", 2);
            String mentionedName = args[0];
            String newNickname;

            Member selfMember = event.getGuild().getSelfMember();
            Member author = event.getMember();
            Member mentionedMember = PermissionHandler.getMentionedMember(event, mentionedName);
            
            if(mentionedMember == null) { 
                event.reply("Couldn't find the specified member, please mention or write the id of a member.");
            }// if you mention a user not in the guild or write a wrong id

            else if(args.length < 2) {
                event.reply("New nickname missing, please write the new nickname after the user.");
            }// if there is no nickname given

            else if((newNickname = args[1]).length() > 32) {
                event.reply("The new nickname must be 32 or fewer in lenght.");
            }// if the nickname is longer than 32 characters

            else if(!selfMember.canInteract(mentionedMember)) {
                event.reply(selfMember.getAsMention() + " can't change the nickname of a member with higher or equal highest role than itself.");
            }// if the bot doesnt have a high enough role to change the nickname of the member

            else if(!author.canInteract(mentionedMember) && author != mentionedMember) {
                event.reply("You can't change the nickname of a member with higher or equal highest role than yourself.");
            }// if the author doesnt have a high enough role to change the nickname of the member and if its not yourself!
            
            else {
                mentionedMember.modifyNickname(newNickname).queue(
                    (e) -> event.reply("Changed nickname of " + mentionedMember.getAsMention()), 
                    new ErrorHandler()
                        .handle(
                            ErrorResponse.MISSING_PERMISSIONS, 
                            (e) -> event.replyError("Error. " + e.getMessage())
                        )
                        .handle(
                            ErrorResponse.INVALID_FORM_BODY, 
                            (e) -> event.replyError("Error. " + e.getMessage())
                        )
                );
            }
        } catch (Exception e) {
            event.replyError("Error: " + e.getMessage());
        }
    }
}