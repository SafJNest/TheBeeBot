package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.safjnest.Utilities.CommandsLoader;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
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
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "member", "Member to kick", true),
            new OptionData(OptionType.STRING, "reason", "Reason of the kick", false)
                .setMaxLength(512)
        );    
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            Member mentionedMember = event.getOption("member").getAsMember();
            String reason = (event.getOption("reason") == null) ? "unspecified reason" : event.getOption("reason").getAsString();

            Member selfMember = event.getGuild().getSelfMember();
            Member author = event.getMember();

            if(mentionedMember == null) { 
                event.deferReply(true).addContent("Couldn't find the specified member, please mention or write the id of a member.").queue();
            }// if you mention a user not in the guild or write a wrong id

            else if(!selfMember.canInteract(mentionedMember)) {
                event.deferReply(true).addContent(selfMember.getAsMention() + " can't kick a member with higher or equal highest role than itself.").queue();
            }// if the bot doesnt have a high enough role to kick the member

            else if(!author.canInteract(mentionedMember) || author == mentionedMember) {
                event.deferReply(true).addContent("You can't kick a member with higher or equal highest role than yourself.").queue();
            }// if the author doesnt have a high enough role to kick the member
            
            else {
                event.getGuild().kick(mentionedMember).reason(reason).queue(
                    (e) -> event.deferReply(false).addContent(mentionedMember.getAsMention() + " has been kicked").queue(), 
                    new ErrorHandler().handle(
                        ErrorResponse.MISSING_PERMISSIONS,
                        (e) -> event.deferReply(true).addContent("Error. " + e.getMessage()).queue())
                );
            }
        } catch (Exception e) {
            event.deferReply(true).addContent("Error: " + e.getMessage()).queue();
        }
    }
}
