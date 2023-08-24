package com.safjnest.SlashCommands.ManageGuild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class MemberInfoSlash extends SlashCommand{
    private final int defaultRoleCharNumber = 200;

    public MemberInfoSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get the information about", false),
            new OptionData(OptionType.INTEGER, "rolecharnumber", "max number of charachters to print roles (0 to 1024)", false)
                .setMinValue(1)
                .setMaxValue(1024)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int roleCharNumber = (event.getOption("roleCharNumber") == null) ? defaultRoleCharNumber : event.getOption("roleCharNumber").getAsInt();
        User user = event.getOption("user") == null ? event.getUser() : event.getOption("user").getAsUser();

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
                    + DatabaseHandler.getSql().getString(
                        "select sum(times) as sum from play where user_id = '" + user.getId()+"';", 
                        "sum")
                    + "```", true);

        eb.addField("Member joined", 
                    "<t:" + member.getTimeJoined().toEpochSecond() + ":f>" + " | <t:" + member.getTimeJoined().toEpochSecond() + ":R>",
                    false);

        eb.addField("Account created", 
                   "<t:" + user.getTimeCreated().toEpochSecond() + ":f>"  + " | <t:" + user.getTimeCreated().toEpochSecond() + ":R>",
                    false);

        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}