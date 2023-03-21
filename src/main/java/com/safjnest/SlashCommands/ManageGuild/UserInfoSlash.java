package com.safjnest.SlashCommands.ManageGuild;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class UserInfoSlash extends SlashCommand{
    private final int defaultRoleCharNumber = 200;

    public UserInfoSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get the information about", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int roleCharNumber = defaultRoleCharNumber;
        User theGuy = event.getOption("user").getAsUser();
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":busts_in_silhouette: **INFORMATION ABOUT "+theGuy.getName()+"** :busts_in_silhouette:");
        eb.setThumbnail(theGuy.getAvatarUrl());
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));

        eb.addField("Name", "```" + theGuy.getAsTag() + "```", true);
        eb.addField("ID", "```" + theGuy.getId() + "```" , true);

        List<String> RoleNames = PermissionHandler.getMaxFieldableRoleNames(event.getGuild().getMember(theGuy).getRoles(), roleCharNumber);
        eb.addField("Roles ["
                    + event.getGuild().getMember(theGuy).getRoles().size() + "] "
                    + "(Printed " + RoleNames.size() + ")", "```"
                    + (RoleNames.size() == 0
                        ? "NO ROLES"
                        : RoleNames.toString().substring(1, RoleNames.toString().length() - 1))
                    + "```", false);

        eb.addField("Nickname", "```"
                    + (event.getGuild().getMember(theGuy).getNickname() == null
                        ? "NO NICKNAME"
                        : event.getGuild().getMember(theGuy).getNickname())
                    + "```", true);

        eb.addField("Is a bot", "```"
                    + ((theGuy.isBot() || PermissionHandler.isEpria(theGuy.getId()))
                        ? "yes"
                        : "no")
                    + "```" , true);

        String permissionNames = PermissionHandler.getPermissionNames(event.getGuild().getMember(theGuy)).toString();
        eb.addField("Permissions", "```"
                    + (event.getGuild().getMember(theGuy).hasPermission(Permission.ADMINISTRATOR)
                        ? "👑 Admin "
                        : permissionNames.substring(1, permissionNames.length() - 1))
                    + "```", false);

        eb.addField("Join this server on (dd/mm/yyyy)", "```" 
                    + DateHandler.formatDate(event.getGuild().getMember(theGuy).getTimeJoined())
                    + "```", false);

        eb.addField("Account created on (dd/mm/yyyy)", "```" 
                    + DateHandler.formatDate(theGuy.getTimeCreated())
                    + "```", false);
        
        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}