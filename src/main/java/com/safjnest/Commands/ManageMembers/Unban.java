package com.safjnest.Commands.ManageMembers;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.safjnest.App;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
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
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            if(event.getArgs().length() == 0){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("List of banned users");
                for (net.dv8tion.jda.api.entities.Guild.Ban ban : event.getGuild().retrieveBanList().complete())
                    eb.appendDescription(ban.getUser().getAsMention() + " - ");
                eb.setColor(Color.decode(App.color));
                eb.setAuthor(event.getSelfUser().getName(), "https://github.com/SafJNest",event.getSelfUser().getAvatarUrl());
                eb.setFooter("*This is not SoundFx, this is much worse cit. steve jobs (probably)", null);
                event.reply(eb.build());
            }

            else{
                final User surelyTheGuy = event.getJDA().retrieveUserById(event.getArgs()).complete();
                if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.BAN_MEMBERS))
                    event.reply(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to unban");

                else if (PermissionHandler.hasPermission(event.getMember(), Permission.BAN_MEMBERS)) {
                    event.getGuild().unban(surelyTheGuy).queue(
                                                            (e) -> event.reply("Unbanned " + surelyTheGuy.getAsMention()), 
                                                            new ErrorHandler().handle(
                                                                ErrorResponse.MISSING_PERMISSIONS,
                                                                    (e) -> event.replyError("sorry, " + e.getMessage()))
                    );
                }
                else 
                event.reply(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to unban");
            }
        } catch (Exception e) {
            event.replyError("sorry, " + e.getMessage());
        }
    }
}