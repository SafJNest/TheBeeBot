package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class PermissionsSlash extends SlashCommand{
    public PermissionsSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get the permission, null to get yours", false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member theGuy = (event.getOption("user") == null) ? event.getMember() : event.getOption("user").getAsMember();
        String per = "";
        String msg = "";
        try {
            
            if (theGuy.isOwner())
                msg = theGuy.getAsMention() + " is the owner of the server.";
            else if (theGuy.hasPermission(Permission.ADMINISTRATOR))
                msg = theGuy.getAsMention() + " is an admin.";
            else{
                for(Permission p :  theGuy.getPermissions())
                    per+=p.getName() + "\n";
                msg = theGuy.getAsMention() + " is not an admin\nThese are his permissions: " + per;
            }
            event.deferReply(false).addContent(msg).queue();
        } catch (Exception e) {
            event.deferReply(true).addContent("sorry, " + e.getMessage()).queue();
        }
    }
}
