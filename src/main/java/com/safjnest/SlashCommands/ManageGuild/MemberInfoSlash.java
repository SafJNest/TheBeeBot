package com.safjnest.SlashCommands.ManageGuild;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
            new OptionData(OptionType.USER, "user", "User to get information on", false),
            new OptionData(OptionType.INTEGER, "rolecharnumber", "Max number of charachters the roles filed can be (1 to 1024)", false)
                .setMinValue(1)
                .setMaxValue(1024)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        int roleCharNumber = (event.getOption("roleCharNumber") == null) ? defaultRoleCharNumber : event.getOption("roleCharNumber").getAsInt();
        User user = event.getOption("user") == null ? event.getUser() : event.getOption("user").getAsUser();

        if(!event.getGuild().isMember(user)){
            event.reply("The specified user is not in this guild.");
            return;
        }

        Member mentionedMember = event.getGuild().getMember(user);
        String name = mentionedMember.getUser().getName();
        String id = mentionedMember.getId();

        List<String> RoleNames = PermissionHandler.getMaxFieldableRoleNames(mentionedMember.getRoles(), roleCharNumber);

        String permissionNames = PermissionHandler.getFilteredPermissionNames(mentionedMember).toString();

        QueryResult lolAccounts = DatabaseHandler.getLolAccounts(id);
        String lolAccountsString = "";
        if(lolAccounts.isEmpty()) {
            lolAccountsString = mentionedMember.getNickname() + " has not connected a riot account.";
        }
        else {
            for(ResultRow lolAccount : lolAccounts) {
                lolAccountsString += RiotHandler.getSummonerBySummonerId(lolAccount.get("summoner_id")).getName() + " - ";
            }
            lolAccountsString = lolAccountsString.substring(0, lolAccountsString.length() - 3);
        }

        ResultRow userExp = DatabaseHandler.getUserExp(id, event.getGuild().getId());
        int exp = 0, lvl = 0, msg = 0;
        if(userExp != null) {
            exp = userExp.getAsInt("exp");
            lvl = userExp.getAsInt("level");
            msg = userExp.getAsInt("messages");
        }
        String lvlString = String.valueOf(ExpSystem.getExpToLvlUp(lvl, exp) + "/" + (ExpSystem.getExpToReachLvlFromZero(lvl + 1) - ExpSystem.getExpToReachLvlFromZero(lvl)));

        List<String> activityNames = new ArrayList<String>();
        mentionedMember.getActivities().forEach(activity -> activityNames.add(activity.getName()));
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(":busts_in_silhouette: **INFORMATION ABOUT " + name + "** :busts_in_silhouette:");
        eb.setThumbnail(mentionedMember.getAvatarUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));

        eb.addField("Name", "```" + name + "```", true);

        eb.addField("Nickname", "```"
            + (mentionedMember.getNickname() == null
                ? "NO NICKNAME"
                : mentionedMember.getNickname())
        + "```", true);

        eb.addField("ID", "```" + id + "```" , true);
        
        eb.addField("Roles [" + mentionedMember.getRoles().size() + "] " + "(Printed " + RoleNames.size() + ")", "```"
            + (RoleNames.size() == 0
                ? "NO ROLES"
                : RoleNames.toString().substring(1, RoleNames.toString().length() - 1))
        + "```", false);

        eb.addField("Status", "```"
            + mentionedMember.getOnlineStatus()
        + "```", true);

        eb.addField("Is a bot", "```"
            + ((mentionedMember.getUser().isBot())
                ? "yes"
                : "no")
        + "```" , true);

        if(activityNames.size() > 0) {
            eb.addField("Activities", "```"
                + activityNames.toString().substring(1, activityNames.toString().length() - 1)
            + "```", false);
        }

        eb.addField("Permissions", "```"
            + (mentionedMember.hasPermission(Permission.ADMINISTRATOR)
                ? "👑 Admin"
                : permissionNames.substring(1, permissionNames.length() - 1)) + " "
        + "```", false);
        
        eb.addField("League Of Legends Account [" + lolAccounts.size() + "]", "```" 
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
        
        eb.addField("Total Sounds Uploaded", "```" 
            + DatabaseHandler.getSoundsUploadedByUserCount(id)
        + "```", true);

        eb.addField("Sounds Uploaded in this server", "```" 
            + DatabaseHandler.getSoundsUploadedByUserCount(id, event.getGuild().getId())
        + "```", true);
        
        eb.addField("Total Sound played (global)", "```"
            + DatabaseHandler.getTotalPlays(id)
        + "```", true);

        eb.addField("Member joined", 
            "<t:" + mentionedMember.getTimeJoined().toEpochSecond() + ":f>" + " | <t:" + mentionedMember.getTimeJoined().toEpochSecond() + ":R>",
        false);

        eb.addField("Account created", 
            "<t:" + mentionedMember.getTimeCreated().toEpochSecond() + ":f>"  + " | <t:" + mentionedMember.getTimeCreated().toEpochSecond() + ":R>",
        false);

        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}