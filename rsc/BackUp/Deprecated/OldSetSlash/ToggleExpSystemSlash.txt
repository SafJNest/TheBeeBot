package com.safjnest.SlashCommands.Settings;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Commands.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class ToggleExpSystemSlash extends SlashCommand {

    private GuildSettings gs;
    
    public ToggleExpSystemSlash(GuildSettings gs) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.BOOLEAN, "toggle", "True to enable and False to disable", true));
        this.gs = gs;
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        Boolean toggle = event.getOption("toggle").getAsBoolean();
        int toggleInt = toggle ? 1 : 0;
        String query = "INSERT INTO guild_settings(guild_id, bot_id, exp_enabled) VALUES ('" + event.getGuild().getId() + "', '" + event.getJDA().getSelfUser().getId() + "', '" + toggleInt + "') ON DUPLICATE KEY UPDATE exp_enabled = '" + toggleInt + "';";
        if(DatabaseHandler.getSql().runQuery(query)){
            event.deferReply(false).addContent("All set correctly.").queue();
            gs.getServer(event.getGuild().getId()).setExpSystem(toggle);
            return;
        }
        event.deferReply(false).addContent("Something went wrong, contact the admin /bug").queue();
        return;
    }
}