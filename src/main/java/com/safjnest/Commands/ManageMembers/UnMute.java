package com.safjnest.Commands.ManageMembers;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorHandler;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class UnMute extends Command{

    public UnMute(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        Member theGuy = null;
        try {
            if(event.getMessage().getMentions().getMembers().size() > 0)
                theGuy = event.getMessage().getMentions().getMembers().get(0);
            else
                theGuy = event.getGuild().retrieveMemberById(event.getArgs()).complete();
            final Member surelyTheGuy = theGuy;

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.VOICE_MUTE_OTHERS))
                event.reply(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to unmute.");
                
            else if(PermissionHandler.hasPermission(event.getMember(), Permission.VOICE_MUTE_OTHERS) && !theGuy.getVoiceState().isMuted())
                event.reply("Cant unmute who is not muted.");

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.VOICE_MUTE_OTHERS)) {
                event.getGuild().mute(surelyTheGuy, false).queue(
                                                        (e) -> event.reply("unmuted " + surelyTheGuy.getAsMention()), 
                                                        new ErrorHandler().handle(
                                                            ErrorResponse.MISSING_PERMISSIONS,
                                                                (e) -> event.replyError("sorry, " + e.getMessage()))
                );
            } else
                event.reply("Brutto fallito non kickare se non sei admin UwU");
        } catch (Exception e) {
            event.replyError("sorry, " + e.getMessage());
        }
    }
}
