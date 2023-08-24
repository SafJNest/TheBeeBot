package com.safjnest.SlashCommands.ManageMembers;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;

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
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class UnMuteSlash extends SlashCommand{

    public UnMuteSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to unmute", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member theGuy = event.getOption("user").getAsMember();
        final Member surelyTheGuy = theGuy;
        try {

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.VOICE_MUTE_OTHERS))
                event.deferReply().addContent(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to unmute.").queue();
                
            else if(PermissionHandler.hasPermission(event.getMember(), Permission.VOICE_MUTE_OTHERS) && !theGuy.getVoiceState().isMuted())
            event.deferReply().addContent("Cant unmute who is not muted.").queue();

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.VOICE_MUTE_OTHERS)) {
                event.getGuild().mute(surelyTheGuy, false).queue(
                                                        (e) -> event.deferReply().addContent("unmuted " + surelyTheGuy.getAsMention()).queue(), 
                                                        new ErrorHandler().handle(
                                                            ErrorResponse.MISSING_PERMISSIONS,
                                                                (e) -> event.deferReply().addContent("sorry, " + e.getMessage()).queue())
                );
            } else
                event.deferReply(true).addContent("You dont have permission to unmute.").queue();
        } catch (Exception e) {
            event.deferReply().addContent("sorry, " + e.getMessage()).queue();
        }
    }
}
