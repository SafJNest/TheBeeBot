package com.safjnest.Commands.Queue;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.TrackScheduler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 */
public class JumpTo extends Command {

    public JumpTo() {
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

        if (event.getArgs().isEmpty()) {
            event.reply("Please provide a valid number");
            return;
        }
        TrackScheduler ts = PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler();

        int position = Integer.parseInt(event.getArgs()) - 1;
        if (position > ts.getQueue().size()) {
            event.reply("There are only " + ts.getQueue().size() + " songs in the queue");
            return;
        }
        else if (position < 0) {
            event.reply("Please provide a valid number");
            return;
        }

        ts.setIndex(position - 1);
        ts.getPlayer().stopTrack();
        ts.play(ts.nextTrack());
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Jumped To:");
        eb.setDescription("[" + ts.getPlayer().getPlayingTrack().getInfo().title + "](" + ts.getPlayer().getPlayingTrack().getInfo().uri + ")");
        eb.setThumbnail("https://img.youtube.com/vi/" + ts.getPlayer().getPlayingTrack().getIdentifier() + "/hqdefault.jpg");
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));
        eb.addField("Lenght", SafJNest.getFormattedDuration(ts.getPlayer().getPlayingTrack().getInfo().length) , true);
        eb.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getAuthor().getAvatarUrl());

        event.reply(eb.build());
    }
}