package com.safjnest.SlashCommands.ManageMembers;

import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class BanSlash extends SlashCommand{

    public BanSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get banned", true),
            new OptionData(OptionType.STRING, "reason", "The reason why the guy is banned", false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        User theGuy = event.getOption("user").getAsUser();
        String reason = (event.getOption("reason") == null) ? "Something bad" : event.getOption("reason").getAsString();
        try {
            final User surelyTheGuy = theGuy;

            if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.BAN_MEMBERS))
                event.deferReply(true).addContent(event.getJDA().getSelfUser().getAsMention() + " doesn't have the permissions to ban, give the bot an admin role").queue();

            else if (PermissionHandler.isUntouchable(theGuy.getId()))
                event.deferReply(false).addContent("Don't you dare touch my creators.").queue();

            else if (PermissionHandler.hasPermission(event.getMember(), Permission.BAN_MEMBERS)) {
                event.getGuild().ban(surelyTheGuy, 0, TimeUnit.SECONDS).reason(reason).queue(
                                                        (e) -> event.deferReply(false).addContent("banned " + surelyTheGuy.getAsMention()).queue(), 
                                                        new ErrorHandler().handle(
                                                            ErrorResponse.MISSING_PERMISSIONS,
                                                                (e) -> event.deferReply(true).addContent("sorry, " + e.getMessage()).queue())
                );
            }else
                event.deferReply(true).addContent("You can't ban if you're not an admin UwU").queue();
        } catch (Exception e) {
            event.deferReply(true).addContent("sorry, " + e.getMessage()).queue();
        }
    }
}
