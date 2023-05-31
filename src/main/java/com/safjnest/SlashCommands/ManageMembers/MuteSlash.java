package com.safjnest.SlashCommands.ManageMembers;

import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import java.util.Arrays;

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
public class MuteSlash extends SlashCommand{

    public MuteSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to mute", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member theGuy = event.getOption("user").getAsMember();
        try {
            final Member surelyTheGuy = theGuy;

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.VOICE_MUTE_OTHERS))
                event.deferReply(true).addContent(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to mute").queue();

            else if (PermissionHandler.isUntouchable(theGuy.getId()))
                event.deferReply(false).addContent("Dont dare touch my creators.").queue(); //di proposito false così tutti lo vedono wsto pezzo di merdafigli od tiroai annodam iedi

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.VOICE_MUTE_OTHERS)) {
                event.getGuild().mute(surelyTheGuy, true).queue(
                                                        (e) -> event.deferReply(false).addContent("muted " + surelyTheGuy.getAsMention()).queue(), 
                                                        new ErrorHandler().handle(
                                                            ErrorResponse.MISSING_PERMISSIONS,
                                                                (e) -> event.deferReply(true).addContent("sorry, " + e.getMessage()).queue())
                );
            } else
                event.deferReply(true).addContent("Dont mute if you are not an admin UwU.").queue();
        } catch (Exception e) {
            event.deferReply(true).addContent("error: " + e.getMessage()).queue();
        }
    }
}
