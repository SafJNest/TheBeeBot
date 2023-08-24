package com.safjnest.SlashCommands.Settings;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class PrefixSlash extends SlashCommand{
    private SQL sql;
    GuildSettings gs;
    public PrefixSlash(SQL sql, GuildSettings gs){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "prefix", "New Prefix", true));
        this.sql = sql;
        this.gs = gs;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.deferReply(true).addContent("Only admins can change the prefix of the guild").queue();
            return;
        }
        String query = "INSERT INTO guild_settings(guild_id, bot_id, prefix)" + "VALUES('" + event.getGuild().getId() + "','" + event.getJDA().getSelfUser().getId()  + "','" + event.getOption("prefix").getAsString() +"') ON DUPLICATE KEY UPDATE prefix = '" + event.getOption("prefix").getAsString() + "';";
        if(sql.runQuery(query))
            event.deferReply(false).addContent("New Prefix is " + event.getOption("prefix").getAsString()).queue();
        else
            event.deferReply(true).addContent("Error").queue();
        
        gs.getServer(event.getGuild().getId()).setPrefix(event.getOption("prefix").getAsString());
    }
}
