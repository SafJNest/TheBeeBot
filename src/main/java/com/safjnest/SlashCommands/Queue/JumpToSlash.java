package com.safjnest.SlashCommands.Queue;

import java.util.Arrays;
import java.awt.Color;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.TrackScheduler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 */
public class JumpToSlash extends SlashCommand {

    public JumpToSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "position", "Select a song from the queue", true)
                .setAutoComplete(true)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        User self = event.getJDA().getSelfUser();

        int position = Integer.parseInt(event.getOption("position").getAsString());
        TrackScheduler ts = PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler();

        System.out.println(ts.getIndex());
        ts.setIndex(position - 1);
        System.out.println(ts.getIndex());
        ts.getPlayer().stopTrack();
        ts.play(ts.nextTrack());
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Jumped To:");
        eb.setDescription("[" + ts.getPlayer().getPlayingTrack().getInfo().title + "](" + ts.getPlayer().getPlayingTrack().getInfo().uri + ")");
        eb.setThumbnail("https://img.youtube.com/vi/" + ts.getPlayer().getPlayingTrack().getIdentifier() + "/hqdefault.jpg");
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));

        eb.addField("Lenght", SafJNest.getFormattedDuration(ts.getPlayer().getPlayingTrack().getInfo().length) , true);
        eb.setFooter("Requested by " + event.getMember().getEffectiveName(), event.getUser().getAvatarUrl());

        event.deferReply().addEmbeds(eb.build()).queue();
	}
}