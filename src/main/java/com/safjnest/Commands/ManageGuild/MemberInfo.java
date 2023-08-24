package com.safjnest.Commands.ManageGuild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class MemberInfo extends Command{
    private final int defaultRoleCharNumber = 200;

    public MemberInfo() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        int roleCharNumber;
        if(!SafJNest.intIsParsable(event.getArgs()) || !SafJNest.isInteger(event.getArgs()) || (Integer.parseInt(event.getArgs())) > 1024 || (Integer.parseInt(event.getArgs())) < 1)
            roleCharNumber = defaultRoleCharNumber;
        else
            roleCharNumber = Integer.parseInt(event.getArgs());

        User user;
        if(event.getMessage().getMentions().getMembers().size() > 0)
            user = event.getMessage().getMentions().getMembers().get(0).getUser();
        else{
            try {
                user = event.getJDA().retrieveUserById(event.getArgs()).complete();
            } catch (Exception e) {
                user = event.getAuthor();
            }
        }
        if(!event.getGuild().isMember(user)){
            event.reply("The user is not in this server");
            return;
        }

        Guild guild = event.getGuild();
        Member member = guild.getMember(user);

        List<String> RoleNames = PermissionHandler.getMaxFieldableRoleNames(member.getRoles(), roleCharNumber);

        String permissionNames = PermissionHandler.getFilteredPermissionNames(member).toString();

        String query = "SELECT summoner_id FROM lol_user WHERE guild_id = '" + user.getId() + "';";
        ArrayList<String> accounts = DatabaseHandler.getSql().getAllRowsSpecifiedColumn(query, "summoner_id");
        String lolAccounts = "";
        if(accounts.size() == 0){
            lolAccounts = user.getName() + " has not connected a riot account.";
        }else{
            for(String s : accounts)
                lolAccounts += RiotHandler.getSummonerBySummonerId(s).getName() + " - ";
            lolAccounts = lolAccounts.substring(0, lolAccounts.length() - 3);
        }

        query = "select exp, level, messages from exp_table where user_id ='" + user.getId() + "' and guild_id = '" + event.getGuild().getId() + "';";
        ArrayList<String> arr = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
        int exp = 0, lvl = 0, msg = 0;
        if(arr != null) {
            exp = Integer.valueOf(arr.get(0));
            lvl = Integer.valueOf(arr.get(1));
            msg = Integer.valueOf(arr.get(2));
        }
        String lvlString = String.valueOf(ExpSystem.expToLvlUp(lvl, exp) + "/" + (ExpSystem.totalExpToLvlUp(lvl + 1) - ExpSystem.totalExpToLvlUp(lvl)));

        List<String> activityNames = new ArrayList<String>();
        member.getActivities().forEach(activity -> activityNames.add(activity.getName()));
        

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(":busts_in_silhouette: **INFORMATION ABOUT " + user.getName() + "** :busts_in_silhouette:");
        eb.setThumbnail(user.getAvatarUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));


        eb.addField("Name", "```" + user.getName() + "```", true);

        eb.addField("Nickname", "```"
                    + (member.getNickname() == null
                        ? "NO NICKNAME"
                        : member.getNickname())
                    + "```", true);

        eb.addField("ID", "```" + user.getId() + "```" , true);
        
        eb.addField("Roles [" + member.getRoles().size() + "] " + "(Printed " + RoleNames.size() + ")", "```"
                    + (RoleNames.size() == 0
                        ? "NO ROLES"
                        : RoleNames.toString().substring(1, RoleNames.toString().length() - 1))
                    + "```", false);

        eb.addField("Status", "```"
                    + member.getOnlineStatus()
                    + "```", true);

        eb.addField("Is a bot", "```"
                    + ((user.isBot() || PermissionHandler.isEpria(user.getId()))
                        ? "yes"
                        : "no")
                    + "```" , true);

        if(activityNames.size() > 0) {
            eb.addField("Activities", "```"
                    + activityNames.toString().substring(1, activityNames.toString().length() - 1)
                    + "```", false);
        }

        eb.addField("Permissions", "```"
                    + (member.hasPermission(Permission.ADMINISTRATOR)
                        ? "👑 Admin"
                        : permissionNames.substring(1, permissionNames.length() - 1))
                    + "```", false);
        
        eb.addField("League Of Legends Account [" + accounts.size() + "]", "```" 
                    + lolAccounts 
                    + "```", false);
        
        eb.addField("Level", "```" 
                    + lvl + " (" + lvlString + ")"
                    + "```", true);

        eb.addField("Experience gained", "```"
                    + exp + " exp"
                    + "```", true);
        
        eb.addField("Total messages sent","```" 
                    + msg 
                    +"```", true);
        
        eb.addField("Total Sound Uploaded", "```"
                    + DatabaseHandler.getSql().getString(
                        "select count(name) as count from sound where user_id = '" + user.getId() + "';", 
                        "count")
                    + "```", true);

        eb.addField("Sound Uploaded in this server", "```"
                    + DatabaseHandler.getSql().getString(
                        "select count(name) as count from sound where guild_id = '" + event.getGuild().getId()+"' AND user_id = '" + user.getId()+"';", 
                        "count")
                    + "```", true);
        
        eb.addField("Total Sound played (global)", "```"
                    + (DatabaseHandler.getSql().getString(
                        "select sum(times) as sum from play where user_id = '" + user.getId() + "';", 
                        "sum"))
                    + "```", true);

        eb.addField("Member joined", 
                    "<t:" + member.getTimeJoined().toEpochSecond() + ":f>" + " | <t:" + member.getTimeJoined().toEpochSecond() + ":R>",
                    false);

        eb.addField("Account created", 
                   "<t:" + user.getTimeCreated().toEpochSecond() + ":f>"  + " | <t:" + user.getTimeCreated().toEpochSecond() + ":R>",
                    false);
        
        event.reply(eb.build());    
    }
}