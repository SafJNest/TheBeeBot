package com.safjnest.Commands.ManageGuild;

import java.awt.Color;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.App;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SafJNest;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class UserInfo extends Command{
    private final int defaultRoleCharNumber = 200;

    public UserInfo() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        int roleCharNumber;
        if(!SafJNest.intIsParsable(event.getArgs()) || !SafJNest.isInteger(event.getArgs()) || (Integer.parseInt(event.getArgs())) > 1024 || (Integer.parseInt(event.getArgs())) < 1)
            roleCharNumber = defaultRoleCharNumber;
        else
            roleCharNumber = Integer.parseInt(event.getArgs());

        User theGuy;
        if(event.getMessage().getMentions().getMembers().size() > 0)
            theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
        else{
            try {
                theGuy = event.getJDA().retrieveUserById(event.getArgs()).complete();
            } catch (Exception e) {
                theGuy = event.getAuthor();
            }
        }
        if(!event.getGuild().isMember(theGuy)){
            event.reply("The user is not in this server");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":busts_in_silhouette: **INFORMATION ABOUT "+theGuy.getName()+"** :busts_in_silhouette:");
        eb.setThumbnail(theGuy.getAvatarUrl());
        eb.setColor(Color.decode(App.color));

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
        
        event.reply(eb.build());
    }
}