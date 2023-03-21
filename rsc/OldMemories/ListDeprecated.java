package com.safjnest.Commands.Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

import com.safjnest.Utilities.SoundBoard;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;


import com.mpatric.mp3agic.Mp3File;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @deprecated
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class ListDeprecated extends Command {

    public ListDeprecated(){
        this.name = "listV2";
        this.aliases = new String[]{"listoideV2", "listinaV2"};
        this.help = "Il bot invia la lista di tutti i suoni su aws s3.";
        this.category = new Category("Audio");
        this.arguments = "[list] (album)";
    }

	@Override
	protected void execute(CommandEvent event) {

    /*ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
        .withBucketName("thebeebox");
    ObjectListing objectListing;
    do {
        objectListing = s3Client.listObjects(listObjectsRequest);
        for (S3ObjectSummary objectSummary : 
            objectListing.getObjectSummaries()) {
            System.out.println( " - " + objectSummary.getKey() + "  (size = " + objectSummary.getSize() + ")");
        }
        listObjectsRequest.setMarker(objectListing.getNextMarker());
    } while (objectListing.isTruncated());
    
    if(.get(0).getKey().equals(name)){
        event.reply("esiste gia");
        e.getJDA().removeEventListener(this);
        return;
    }*/
    
        MessageChannel channel = event.getChannel();
        HashMap<String, ArrayList<Mp3File>> tags = new HashMap<>();
        Mp3File[] files = SoundBoard.getMP3File();
        if(event.getArgs().equalsIgnoreCase("album")){
            for (Mp3File file : files){
                if(!tags.containsKey(file.getId3v2Tag().getAlbum()))
                    tags.put(file.getId3v2Tag().getAlbum(), new ArrayList<Mp3File>());
                tags.get(file.getId3v2Tag().getAlbum()).add(file);
            }
        }else{
            for (Mp3File file : files){
                if(!tags.containsKey(file.getId3v2Tag().getAlbumArtist()))
                    tags.put(file.getId3v2Tag().getAlbumArtist(), new ArrayList<Mp3File>());
                tags.get(file.getId3v2Tag().getAlbumArtist()).add(file);
            }
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("SoundBoard");
        String soundNames = "```\n";
            for(String k : tags.keySet()){
                for(Mp3File f : tags.get(k)){
                    soundNames+= f.getId3v2Tag().getTitle() + "\n";
                }
                soundNames+="```";
                eb.addField(k, soundNames, true);
                soundNames = "```\n";
            }
        eb.setDescription("Lista con tutti i suoni del tier 1 bot");
        eb.setColor(new Color(0, 128, 128));
        eb.setAuthor(event.getSelfUser().getName(), "https://github.com/SafJNest",event.getSelfUser().getAvatarUrl());
        eb.setFooter("*This is not SoundFx, this is much worse cit. steve jobs (probably)", null); //Questo non e' SoundFx, questa e' perfezione cit. steve jobs (probabilmente)
        File file = new File("rsc" + File.separator + "img" + File.separator + "mp3.png");
        eb.setThumbnail("attachment://mp3.png");
        channel.sendMessageEmbeds(eb.build())
                    .addFiles(FileUpload.fromData(file))
                    .queue();
                    
	}
}


