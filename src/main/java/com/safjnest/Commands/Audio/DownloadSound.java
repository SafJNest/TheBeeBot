package com.safjnest.Commands.Audio;

import java.io.File;
import java.util.ArrayList;

import com.amazonaws.services.s3.model.S3Object;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.AwsS3;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PostgreSQL;



public class DownloadSound extends Command{
    String path = "rsc" + File.separator + "SoundBoard" + File.separator;
    PostgreSQL sql;
    AwsS3 s3Client;

    public DownloadSound(AwsS3 s3Client, PostgreSQL sql){
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
        String fileName = event.getArgs();
        if(fileName.isEmpty()){
            event.reply("Missing sound's name.");
            return;
        }

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
            event.reply("There is no sound with that name/id");
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
            event.reply("I couldn't find this sound on your guild so i downloaded a random sound with this name");
        }
        else{
            event.reply("I'm downloading the file with this name from your guild");
        }

        id = arr.get(indexForKeria).get(0);
        extension = arr.get(indexForKeria).get(2);

        S3Object sound = s3Client.downloadFile(path, id, event);

        if(sound == null){
            event.reply("Error: sound not found in aws s3 (this is probably our fault, contact the developers)");
            return;
        }
        
        fileName = id + "." + extension;

        File toSend = new File(path + fileName);
        event.reply(toSend, fileName);

    }
}