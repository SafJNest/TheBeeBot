package com.safjnest.SlashCommands.Settings.LevelUp;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LevelUpToggleSlash extends SlashCommand{

    private GuildSettings gs;

    public LevelUpToggleSlash(GuildSettings gs, String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.gs = gs;
        this.options = Arrays.asList(
            new OptionData(OptionType.BOOLEAN, "toggle", "True to enable and False to disable", true));
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
