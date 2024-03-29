package com.safjnest.Commands.Audio;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Audio.AudioType;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.SoundBoard;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class PlaySound extends Command{
    private final String path = "rsc" + File.separator + "SoundBoard"+ File.separator;
    private PlayerManager pm;

    public PlaySound() {
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.pm = PlayerManager.get();
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        User self = event.getSelfUser();
        AudioChannel authorChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
        
        String fileName;
        if((fileName = event.getArgs()).equals("")){
            event.reply("You need to specify a sound name or id.");
            return;
        }

        if(authorChannel == null){
            event.reply("You need to be in a voice channel to use this command.");
            return;
        }

        if(botChannel != null && (authorChannel != botChannel)){
            event.reply("The bot is already being used in another voice channel.");
            return;
        }
        
        QueryResult sounds = fileName.matches("[0123456789]*") 
                           ? DatabaseHandler.getSoundsById(fileName, guild.getId(), event.getAuthor().getId()) 
                           : DatabaseHandler.getSoundsByName(fileName, guild.getId(), event.getAuthor().getId());

        if(sounds.isEmpty()) {
            event.reply("Couldn't find a sound with that name/id.");
            return;
        }

        ResultRow toPlay = null;
        for(ResultRow sound : sounds) {
            if(sound.get("guild_id").equals(guild.getId())) {
                toPlay = sound;
                break;
            }
        }
        
        if(toPlay == null)
            toPlay = sounds.get((int)(Math.random() * sounds.size()));

        File soundBoard = new File("rsc" + File.separator + "SoundBoard");

        if(!soundBoard.exists())
            soundBoard.mkdirs();

        fileName = path + toPlay.get("id") + "." + toPlay.get("extension");

        pm.loadItemOrdered(guild, self, fileName, new ResultHandler(event, toPlay, fileName));
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final CommandEvent event;
        private final Guild guild;
        private final User self;
        private final Member author;
        private final ResultRow toPlay;
        private final String fileName;
        
        private ResultHandler(CommandEvent event, ResultRow toPlay, String fileName) {
            this.event = event;
            this.guild = event.getGuild();
            this.self = event.getSelfUser();
            this.author = event.getMember();
            this.toPlay = toPlay;
            this.fileName = fileName;
        }
        
        @Override
        public void trackLoaded(AudioTrack track) {
            pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track, AudioType.SOUND);

            guild.getAudioManager().openAudioConnection(author.getVoiceState().getChannel());

            DatabaseHandler.updateUserPlays(toPlay.get("id"), event.getAuthor().getId());
            ResultRow plays = DatabaseHandler.getPlays(toPlay.get("id"), event.getAuthor().getId());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(event.getAuthor().getName(), "https://github.com/SafJNest", event.getAuthor().getAvatarUrl());
            eb.setTitle("Playing now:");
            eb.setDescription("```" + toPlay.get("name") + " (ID: " + toPlay.get("id") + ") " + ((toPlay.getAsBoolean("public")) ? ":public:" : ":private:") + "```");
            eb.setColor(Color.decode(Bot.getColor()));
            eb.setThumbnail(event.getSelfUser().getAvatarUrl());

            eb.addField("Author", "```" 
                + event.getJDA().getUserById(toPlay.get("user_id")).getName() 
            + "```", true);

            try {
                eb.addField("Lenght", "```"
                    + (toPlay.get("extension").equals("opus") 
                    ? SafJNest.getFormattedDuration((Math.round(SoundBoard.getOpusDuration(fileName)))*1000)
                    : SafJNest.getFormattedDuration(track.getInfo().length))
                + "```", true);
            } catch (IOException e) {e.printStackTrace();}

            eb.addField("Format", "```" 
                + toPlay.get("extension").toUpperCase() 
            + "```", true);

            eb.addField("Guild", "```" 
                + event.getJDA().getGuildById(toPlay.get("guild_id")).getName() 
            + "```", true);

            eb.addField("Played", "```" 
                + plays.get("totalTimes") 
                + (plays.get("totalTimes").equals("1") ? " time" : " times") 
                + " (yours: "+plays.get("timesByUser") + ")"
            + "```", true);

            eb.addField("Creation time", 
                "<t:" + toPlay.getAsEpochSecond("time") + ":f>"  + " | <t:" + toPlay.getAsEpochSecond("time") + ":R>",
            false);

            event.reply(eb.build());
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {}

        @Override
        public void noMatches() {
            event.reply("No matches");
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            event.reply(throwable.getMessage());
        }
    }
}