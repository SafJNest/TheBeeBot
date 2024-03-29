package com.safjnest.Commands.Queue;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.TrackScheduler;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.LinkedList;

import java.awt.Color;

public class Queue extends Command{
    
    public Queue() {
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    private static String formatTrack(int index, AudioTrack track) {
        //return "**[" + (index + 1) + "]** " + "`-`" + "[" + track.getInfo().title + "](" + track.getInfo().uri + ") - " + "`" + SafJNest.formatDuration(track.getInfo().length) +  "`";
        return "**[" + (index + 1) + "]** " + "`-`"  + track.getInfo().title + " - " + "`" + SafJNest.formatDuration(track.getInfo().length) +  "`";
    }

    public static EmbedBuilder getEmbed(JDA jda, Guild guild, LinkedList<AudioTrack> queue, int startIndex) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(guild.getName() + " current queue");
        eb.setColor(Color.decode(Bot.getColor()));
        eb.setThumbnail(guild.getIconUrl());

        AudioTrack playingNow = null;

        int index = PlayerManager.get().getGuildMusicManager(guild, jda.getSelfUser()).getTrackScheduler().getIndex();

        /**
         * Se vuoi che esca sempre la current song anche se stiamo guardando altre pagine basta
         * aggiungere un altro if dove se index==startindex startIndex = index + 1;
         * e lasciare index != -1 per playingNow
         * 
         * sinceramente non so cosa sia meglio, ma questo è questione di stile quindi si lascia per ultima
         */
        if(index != -1 && index == startIndex) {
            playingNow = queue.get(index);
            startIndex = index + 1;
        }
        
        String queues = "";
        for(int i = startIndex, cont = 0; i < queue.size() && cont < 10 && i != index; i++, cont ++) {
            queues += formatTrack(i, queue.get(i)) + "\n";
        }

        if(playingNow != null)
            eb.addField(RiotHandler.getFormattedEmoji(jda, "playing") + " Now playing", formatTrack(index, playingNow), false);

        eb.addField(RiotHandler.getFormattedEmoji(jda, "playlist") + " Songs in queue ("  + (queue.size() - index - 1) + ")", queues, false);
        return eb;
    }
    
    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        User self = event.getSelfUser();

        TrackScheduler ts = PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler();
        LinkedList<AudioTrack> queue = ts.getQueue();

        int currentIndex = ts.getIndex();
        if(queue.isEmpty()) {
            event.reply("```Queue is empty```");
            return;
        }

        java.util.List<LayoutComponent> buttonRows = new ArrayList<>();

        Button repeat = Button.secondary("queue-repeat", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "repeat"));
        Button previous = Button.primary("queue-previous" , " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "previous"));
        Button play = Button.primary("queue-pause", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "pause"));
        Button next = Button.primary("queue-next", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "next"));
        Button shurima = Button.secondary("queue-shurima", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "shuffle"));
        
        if(ts.isRepeat()) {
            repeat = repeat.withStyle(ButtonStyle.DANGER);
        }
            
        
        if(ts.isShuffled()) {
            shurima = shurima.withStyle(ButtonStyle.DANGER);
        }

        if(currentIndex == 0) {
            previous = previous.withStyle(ButtonStyle.DANGER);
            previous = previous.asDisabled();
        }

        if(currentIndex == ts.getQueue().size() - 1) {
            next = next.withStyle(ButtonStyle.DANGER);
            next = next.asDisabled();
        }

        if(!ts.isPaused()) {
            play = Button.primary("queue-pause", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "pause"));
        } else {
            play = Button.primary("queue-play", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "play"));
        }


        buttonRows.add(ActionRow.of(
            repeat,
            previous,
            play,
            next,
            shurima
        ));


        Button previousPage = Button.secondary("queue-previouspage-", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "leftarrow"));
        Button nextPage = Button.secondary("queue-nextpage-", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "rightarrow"));
 
        int previousIndex = ts.getIndex() - 11;
        if(previousIndex < 0)
            previousIndex = 0;

        int nextIndex = ts.getIndex() + 11;
        if(nextIndex > ts.getQueue().size())
            nextIndex = ts.getQueue().size() - 1;

        if(currentIndex > ts.getQueue().size()) {
            nextPage = nextPage.asDisabled();
        }

        if(previousIndex < 0) {
            previousPage = previousPage.asDisabled();
        }

        nextPage = nextPage.withId("queue-nextpage-" + nextIndex);
        previousPage = previousPage.withId("queue-previouspage-" + previousIndex);


        buttonRows.add(ActionRow.of(
            Button.secondary("queue-blank", " ").asDisabled().withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "blank")),
            previousPage,
            Button.secondary("queue-blank1", " ").asDisabled().withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "blank")),
            nextPage,
            Button.secondary("queue-clear", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "bin"))
        ));

        event.getChannel().sendMessageEmbeds(getEmbed(event.getJDA(), guild, queue, ts.getIndex()).build()).addComponents(buttonRows).queue();
    }
}
