package com.safjnest.SlashCommands.Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;



public class DownloadSoundSlash extends SlashCommand{
    String path = "rsc" + File.separator + "SoundBoard" + File.separator;
    SQL sql;

    public DownloadSoundSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.sql = DatabaseHandler.getSql();
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

        if((arr = sql.getAllRows(query, 3)).isEmpty()){
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

       
        
        fileName = id + "." + extension;

        File toSend = new File(path + fileName);
        event.getHook().sendFiles(FileUpload.fromData(toSend)).queue();

    }
}