package com.safjnest.SlashCommands.Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.amazonaws.services.s3.model.S3Object;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.AwsS3;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;



public class DownloadSoundSlash extends SlashCommand{
    String path = "rsc" + File.separator + "SoundBoard" + File.separator;
    SQL sql;
    AwsS3 s3Client;

    public DownloadSoundSlash(AwsS3 s3Client, SQL sql){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.s3Client = s3Client;
        this.sql = sql;
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sound", "Sound to download", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String fileName = event.getOption("sound").getAsString();
        String query = null;
        String id = null, extension;
        ArrayList<ArrayList<String>> arr = null;

        if(fileName.matches("[0123456789]*")){
            query = "SELECT id, guild_id, extension FROM sound WHERE id = '" + fileName + "';";
        }
        else{
            query = "SELECT id, guild_id, extension FROM sound WHERE name = '" + fileName + "';";
        }

        if((arr = sql.getTuple(query, 3)).isEmpty()){
            event.deferReply(true).addContent("There is no sound with that name/id").queue();
            return;
        }
        int indexForKeria = -1;
        for(int i = 0; i < arr.size(); i++){
            if(arr.get(i).get(1).equals(event.getGuild().getId())){
               indexForKeria = i;
               break;
            }
        }
        
        if(indexForKeria == -1){
            indexForKeria = (int)(Math.random()*arr.size());
            event.deferReply(false).addContent("I couldn't find this sound on your guild so i downloaded a random sound with this name").queue();
        }
        else{
            event.deferReply(false).addContent("I'm downloading the file with this name from your guild").queue();
        }

        id = arr.get(indexForKeria).get(0);
        extension = arr.get(indexForKeria).get(2);

        S3Object sound = s3Client.downloadFile(path, id, event);

        if(sound == null){
            event.deferReply(true).addContent("Error: sound not found in aws s3 (this is probably our fault, contact the developers)").queue();
            return;
        }
        
        fileName = id + "." + extension;

        File toSend = new File(path + fileName);
        event.getHook().sendFiles(FileUpload.fromData(toSend)).queue();

    }
}