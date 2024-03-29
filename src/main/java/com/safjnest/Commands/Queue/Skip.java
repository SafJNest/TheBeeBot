package com.safjnest.Commands.Queue;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Skip extends Command{
    
    public Skip() {
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        User self = event.getSelfUser();
        TrackScheduler ts = PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler();
        AudioTrack nextTrack = ts.nextTrack();
        
        if(nextTrack == null) {
            event.reply("This is the end of the queue");
            return;
        }

        ts.playForce(nextTrack);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Skipped Song:");
        eb.setDescription("[" + ts.getPlayer().getPlayingTrack().getInfo().title + "](" + ts.getPlayer().getPlayingTrack().getInfo().uri + ")");
        eb.setThumbnail("https://img.youtube.com/vi/" + ts.getPlayer().getPlayingTrack().getIdentifier() + "/hqdefault.jpg");
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));

        eb.addField("Lenght", SafJNest.getFormattedDuration(ts.getPlayer().getPlayingTrack().getInfo().length) , true);
        eb.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getMember().getAvatarUrl());

        event.reply(eb.build());
    }
}
