package com.safjnest.Commands.Audio;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.amazonaws.services.s3.model.S3Object;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.App;
import com.safjnest.Utilities.AwsS3;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PlayerManager;
import com.safjnest.Utilities.PostgreSQL;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.SoundBoard;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

public class PlaySound extends Command{
    PostgreSQL sql;
    AwsS3 s3Client;
    String path = "rsc" + File.separator + "SoundBoard"+ File.separator;
    String fileName;
    PlayerManager pm;


    public PlaySound(AwsS3 s3Client, PostgreSQL sql){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.s3Client = s3Client;
        this.sql = sql;
    }

    @Override
    protected void execute(CommandEvent event) {
        
        if((fileName = event.getArgs()) == ""){
            event.reply("Missing name");
            return;
        }
        
        File soundBoard = new File("rsc" + File.separator + "SoundBoard");
        if(!soundBoard.exists())
            soundBoard.mkdirs();

        //TODO fix | deletare il file vecchio ogni ps bene
        for (File file : soundBoard.listFiles())
            file.delete();

        String query = null;
        String id = null, name, guildId, userId, extension;
        ArrayList<ArrayList<String>> arr = null;

        if(fileName.matches("[0123456789]*")){
            query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE id = '" + fileName + "';";
        }
        else{
            query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE name = '" + fileName + "';";
        }

        if((arr = sql.getTuple(query, 5)).isEmpty()){
            event.reply("There is no sound with that name/id");
            return;
        }

        int indexForKeria = -1;
        for(int i = 0; i < arr.size(); i++){
            if(arr.get(i).get(2).equals(event.getGuild().getId())){
               indexForKeria = i;
               break;
            }
        }
        
        if(indexForKeria == -1){
            indexForKeria = (int)(Math.random()*arr.size());
        }

        id = arr.get(indexForKeria).get(0);
        name = arr.get(indexForKeria).get(1);
        guildId = arr.get(indexForKeria).get(2);
        userId = arr.get(indexForKeria).get(3);
        extension = arr.get(indexForKeria).get(4);

        S3Object sound = s3Client.downloadFile(path, id, event);

        if(sound == null){
            event.reply("sound not found in aws s3");
            return;
        }
        
        fileName = path + id + "." + extension;

        pm = new PlayerManager();
        
        MessageChannel channel = event.getChannel();
        AudioChannel myChannel = event.getMember().getVoiceState().getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.setSendingHandler(pm.getAudioHandler());
        audioManager.openAudioConnection(myChannel);

        if(pm.getPlayer().getPlayingTrack() != null){
            //pm.stopAudioHandler();
        }
        
        pm.getAudioPlayerManager().loadItem(fileName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                pm.getTrackScheduler().addQueue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                /*
                 * for (AudioTrack track : playlist.getTracks()) {
                 * trackScheduler.queue(track);
                 * }
                 */
            }
            
            @Override
            public void noMatches() {
                channel.sendMessage("File not found").queue();
                pm.getTrackScheduler().addQueue(null);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("error: " + throwable.getMessage());
            }
        });

        pm.getPlayer().playTrack(pm.getTrackScheduler().getTrack());
        

        query = "SELECT times FROM play where play.id_sound = '" + id + "' and play.user_id = '" + event.getAuthor().getId() + "';";
        if(sql.getString(query, "times") == null){
            query = "INSERT INTO play(user_id, id_sound, times) VALUES('" + event.getAuthor().getId() + "','" + id + "', 1);";
        }
        else{
            query = "UPDATE play SET times = times + 1 WHERE id_sound = (" + id + ") AND user_id = '" + event.getAuthor().getId() + "';";
        }
        
        sql.runQuery(query);
        query = "SELECT SUM(times) FROM PLAY where id_sound='" + id + "';";
        String timesPlayed = sql.getString(query, "sum");
        query = "SELECT times FROM PLAY where id_sound='" + id + "' AND user_id='"+event.getAuthor().getId()+"';";
        String timesPlayedByUser = sql.getString(query, "times");
        
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor(event.getAuthor().getName(), "https://github.com/SafJNest", event.getAuthor().getAvatarUrl());

        eb.setTitle("Playing now:");
        eb.setDescription("```" + name + " (ID: " + id + ")" + "```");

        eb.addField("Author", "```" + event.getJDA().getUserById(userId).getName() + "```", true);
        try {
            eb.addField("Lenght","```" + (extension.equals("opus") 
            ? SafJNest.getFormattedDuration((Math.round(SoundBoard.getOpusDuration(fileName)))*1000)
            : SafJNest.getFormattedDuration(pm.getPlayer().getPlayingTrack().getInfo().length)) + "```", true);
        } catch (IOException e) {e.printStackTrace();}
        
        eb.addBlankField(true);

        //Mp3File mp = SoundBoard.getMp3FileByName(player.getPlayingTrack().getInfo().title);

        eb.addField("Guild", "```" + event.getJDA().getGuildById(guildId).getName() + "```", true);

        eb.addField("Played", "```" + timesPlayed + (timesPlayed.equals("1") ? " time" : " times") + " (yours: "+timesPlayedByUser+")```", true);

        String img = "idk";
        if(extension.equals("opus"))
            img = "opus.png";
        
        else
            img = "mp3.png"; 
           
        eb.setColor(Color.decode(App.color));
        eb.setFooter("*This is not SoundFx, this is much worse. cit. steve jobs (probably)", null); //Questo non e' SoundFx, questa e' perfezione cit. steve jobs (probabilmente)
            
        File imgFile = new File("rsc" + File.separator + "img" + File.separator + img);
        eb.setThumbnail("attachment://" + img);

        channel.sendMessageEmbeds(eb.build())
            .addFiles(FileUpload.fromData(imgFile))
            .queue();
    }
}
