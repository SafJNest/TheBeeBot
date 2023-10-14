package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

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
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "member", "Member to get the permission of", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member mentionedMember = (event.getOption("member") == null) ? event.getMember() : event.getOption("member").getAsMember();
        try {
            if (mentionedMember.isOwner()) {
                event.deferReply(false).addContent(mentionedMember.getAsMention() + " is the owner of the guild.").queue();
            }
            else if (mentionedMember.hasPermission(Permission.ADMINISTRATOR)) {
                event.deferReply(false).addContent(mentionedMember.getAsMention() + " is an admin.").queue();
            }
            else {
                StringBuilder permissionsString = new StringBuilder();
                for(Permission permission :  mentionedMember.getPermissions())
                    permissionsString.append(permission.getName() + " - ");
                permissionsString.delete(permissionsString.length() - 3, permissionsString.length());
                event.deferReply(false).addContent(mentionedMember.getAsMention() + " **is not an admin and these are his permissions:** \n" + permissionsString.toString()).queue();
            }
        } catch (Exception e) {
            event.deferReply(true).addContent("Error: " + e.getMessage()).queue();
        }
    }
}
