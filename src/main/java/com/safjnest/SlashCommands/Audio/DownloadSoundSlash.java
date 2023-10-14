package com.safjnest.SlashCommands.Audio;

import java.io.File;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;

public class DownloadSoundSlash extends SlashCommand{
    String path = "rsc" + File.separator + "SoundBoard" + File.separator;

    public DownloadSoundSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "sound", "Sound to download", true)
        );
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String fileName = event.getOption("sound").getAsString();
        String message = null;

        QueryResult sounds = fileName.matches("\\d+") 
                           ? DatabaseHandler.getSoundsById(fileName, event.getGuild().getId(), event.getMember().getId()) 
                           : DatabaseHandler.getSoundsByName(fileName, event.getGuild().getId(), event.getMember().getId());

        if(sounds.isEmpty()) {
            event.deferReply(true).addContent("Couldn't find a sound with that name/id.");
            return;
        }

        ResultRow toDownload = null;

        if(sounds.size() == 1) {
            toDownload = sounds.get(0);
            message = "Downloaded sound **" + toDownload.get("name") + " (ID: " + toDownload.get("id") + ")**";
        }
        else {
            for(ResultRow sound : sounds) {
                if(sound.get("guild_id").equals(event.getGuild().getId())) {
                    toDownload = sound;
                    message = "Downloaded sound **" + toDownload.get("name") + " (ID: " + toDownload.get("id") + ")** from this guild.";
                }
            }
        }
        if(toDownload == null) {
            toDownload = sounds.get((int)(Math.random() * sounds.size()));
            message = "Downloaded a random sound named **" + toDownload.get("name") + " (ID: " + toDownload.get("id") + ")** ";
        }

        fileName = path + toDownload.get("id") + "." + toDownload.get("extension");

        event.deferReply(false).addContent(message)
            .setFiles(FileUpload.fromData(new File(fileName)))
            .queue();
    }
}