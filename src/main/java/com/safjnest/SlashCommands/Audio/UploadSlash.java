package com.safjnest.SlashCommands.Audio;

import java.io.File;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class UploadSlash extends SlashCommand{
    private String soundName;
    
    public UploadSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "name", "Sound name", true),
            new OptionData(OptionType.ATTACHMENT, "file", "Sound file (mp3 or opus)", true),
            new OptionData(OptionType.BOOLEAN, "public", "true or false", false)
        );
    }
    
	@Override
	protected void execute(SlashCommandEvent event) {
        soundName = event.getOption("name").getAsString();
        Attachment attachment = event.getOption("file").getAsAttachment();

        boolean isPublic;
        if(event.getOption("public") != null)
            isPublic = event.getOption("public").getAsBoolean();
        else
            isPublic = true;
        
        if(soundName.matches("[0123456789]*")){
            event.reply("You can't use a name that only contains numbers.");
            return;
        }

        if(!attachment.getFileExtension().equals("mp3") && !attachment.getFileExtension().equals("opus")){
            event.deferReply(true).addContent("Only upload the sound in **mp3** or **opus** format.").queue();
            return;
        }

        QueryResult sounds = DatabaseHandler.getDuplicateSoundsByName(soundName, event.getGuild().getId(), event.getMember().getId());

        if(!sounds.isEmpty()) {
            for(ResultRow sound : sounds) {
                if(sound.get("guild_id").equals(event.getGuild().getId()))
                    event.deferReply(true).addContent("That name is already in use by you.").queue();
                if(sound.get("user_id").equals(event.getMember().getId()))
                    event.deferReply(true).addContent("That name is already in use in this guild.").queue();
            }
            return;
        }

        String id = DatabaseHandler.insertSound(soundName, event.getGuild().getId(), event.getMember().getId(), attachment.getFileExtension(), isPublic);

        if(id == null){
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        File saveFile = new File("rsc" + File.separator + "SoundBoard" + File.separator + (id + "." + attachment.getFileExtension()));

        attachment.getProxy().downloadToFile(saveFile);
        event.deferReply(false).addContent("Sound uploaded succesfully").queue();
	}
}