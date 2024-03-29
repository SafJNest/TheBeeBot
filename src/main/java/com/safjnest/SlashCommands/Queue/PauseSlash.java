package com.safjnest.SlashCommands.Queue;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Audio.PlayerManager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;


public class PauseSlash extends SlashCommand {

    public PauseSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        User self = event.getJDA().getSelfUser();
        PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler().pause(true);

        event.deferReply(false).addContent("Playing paused").queue();
    }
}
