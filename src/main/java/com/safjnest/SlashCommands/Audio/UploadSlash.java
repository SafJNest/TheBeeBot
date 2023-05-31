package com.safjnest.SlashCommands.Audio;

import java.io.File;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileProxy;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class UploadSlash extends SlashCommand{
    private String fileName;
    private SQL sql;
    
    public UploadSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "name", "Soundn name", true));
        this.sql = DatabaseHandler.getSql();
    }
    
	@Override
	protected void execute(SlashCommandEvent event) {
        fileName = event.getOption("name").getAsString();
        if(fileName.matches("[0123456789]*")){
            event.reply("You can't use a name that only contains numbers");
            return;
        }

        event.deferReply(false).addContent("Ok, now upload the sound here in mp3 or **opus** format").queue();
        FileListener fileListener = new FileListener(event, fileName, event.getChannel(),sql);
        event.getJDA().addEventListener(fileListener);
	}
}

class FileListener extends ListenerAdapter {
    private String name;
    private SlashCommandEvent event;
    private MessageChannel channel;
    private float maxFileSize = 1049000; //in bytes
    private SQL sql;

    public FileListener(SlashCommandEvent event, String name, MessageChannel channel, SQL sql){
        this.name = name;
        this.event = event;
        this. channel = channel;
        this.sql = sql;
    }

    
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(!e.getChannel().equals(channel) || e.getAuthor().isBot()){
            return;
        }
        if(e.getMessage().getAttachments().size() <= 0){
            event.deferReply(true).addContent("You have to upload the sound, you can try again by reusing the command").queue();
            e.getJDA().removeEventListener(this);
            return;
        }

        Attachment attachment = e.getMessage().getAttachments().get(0);

        if(attachment.getSize() > maxFileSize && !PermissionHandler.isUntouchable(event.getMember().getId())){
            event.deferReply(true).addContent("The file is too big (" + maxFileSize/1048576 + "mb max)").queue();
            e.getJDA().removeEventListener(this);
            return;
        }

        String query = "INSERT INTO sound(name, guild_id, user_id, extension) VALUES('" 
                     + name + "','" + event.getGuild().getId() + "','" + event.getMember().getId() + "','" + attachment.getFileExtension() + "');";
        query = "SELECT id FROM sound WHERE name = '" + name + "' AND guild_id = '" + event.getGuild().getId() + "' AND user_id = '" + event.getMember().getId() + "';";
        String id = sql.getString(query, "id");

        if(id.equals(null)){
            event.deferReply(true).addContent("An error with the PostgreSQL database occured").queue();
            e.getJDA().removeEventListener(this);
            return;
        }

        File saveFile = new File("rsc" + File.separator + "SoundBoard" + File.separator + (id + "." + attachment.getFileExtension()));

        new FileProxy(attachment.getUrl()).downloadToFile(saveFile);
        event.deferReply(false).addContent("File uploaded succesfully").queue();
        
        e.getJDA().removeEventListener(this);
    }
}