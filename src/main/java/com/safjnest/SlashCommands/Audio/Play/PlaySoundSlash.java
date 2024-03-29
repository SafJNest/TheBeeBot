package com.safjnest.SlashCommands.Audio.Play;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class PlaySoundSlash extends SlashCommand{
    
    private final String path = "rsc" + File.separator + "SoundBoard"+ File.separator;
    private PlayerManager pm;

    public PlaySoundSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sound", "Sound to play", true)
                .setAutoComplete(true)
        );
        this.pm = PlayerManager.get();
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        User self = event.getJDA().getSelfUser();
        AudioChannel authorChannel = event.getMember().getVoiceState().getChannel();
        AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
        
        String fileName = event.getOption("sound").getAsString();

        if(authorChannel == null){
            event.reply("You need to be in a voice channel to use this command.");
            return;
        }

        if(botChannel != null && (authorChannel != botChannel)){
            event.reply("The bot is already being used in another voice channel.");
            return;
        }
        
        QueryResult sounds = fileName.matches("[0123456789]*") 
                           ? DatabaseHandler.getSoundsById(fileName, guild.getId(), event.getUser().getId()) 
                           : DatabaseHandler.getSoundsByName(fileName, guild.getId(), event.getUser().getId());

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
        private final SlashCommandEvent event;
        private final Guild guild;
        private final User self;
        private final Member author;
        private final ResultRow toPlay;
        private final String fileName;
        
        private ResultHandler(SlashCommandEvent event, ResultRow toPlay, String fileName) {
            this.event = event;
            this.guild = event.getGuild();
            this.self = event.getJDA().getSelfUser();
            this.author = event.getMember();
            this.toPlay = toPlay;
            this.fileName = fileName;
        }
        
        @Override
        public void trackLoaded(AudioTrack track) {
            pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track, AudioType.SOUND);

            guild.getAudioManager().openAudioConnection(author.getVoiceState().getChannel());

            DatabaseHandler.updateUserPlays(toPlay.get("id"), event.getMember().getId());
            ResultRow plays = DatabaseHandler.getPlays(toPlay.get("id"), event.getMember().getId());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor(event.getMember().getEffectiveName(), "https://github.com/SafJNest", event.getMember().getAvatarUrl());
            eb.setTitle("Playing now:");
            eb.setDescription("```" + toPlay.get("name") + " (ID: " + toPlay.get("id") + ") " + ((toPlay.getAsBoolean("public")) ? ":public:" : ":private:") + "```");
            eb.setColor(Color.decode(Bot.getColor()));
            eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());

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

            event.deferReply(false).addEmbeds(eb.build()).queue();
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