package com.safjnest.Commands.Audio;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.AwsS3;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SQL;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileProxy;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.2.5
 */
public class Upload extends Command{
    private AwsS3 s3Client;
    private String fileName;
    private SQL sql;
    
    public Upload(AwsS3 s3Client, SQL sql){
        this.name = this.getClass().getSimpleName();;
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
            event.reply("You have to write the name of the sound");
            return;
        }
        if(fileName.matches("[0123456789]*")){
            event.reply("You can't use a name that only contains numbers");
            return;
        }

        event.reply("Ok, now upload the sound here in mp3 or **opus** format");
        FileListener fileListener = new FileListener(event, fileName, event.getChannel(), s3Client.getS3Client(), sql);
        event.getJDA().addEventListener(fileListener);
	}
}

class FileListener extends ListenerAdapter {
    private String name;
    private AmazonS3 s3Client;
    private CommandEvent event;
    private MessageChannel channel;
    private float maxFileSize = 1049000; //in bytes
    private SQL sql;

    public FileListener(CommandEvent event, String name, MessageChannel channel, AmazonS3 s3Client, SQL sql){
        this.name = name;
        this.s3Client = s3Client;
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
            event.reply("You have to upload the sound, you can try again by reusing the command");
            e.getJDA().removeEventListener(this);
            return;
        }

        Attachment attachment = e.getMessage().getAttachments().get(0);

        if(attachment.getSize() > maxFileSize && !PermissionHandler.isUntouchable(event.getAuthor().getId())){
            event.reply("The file is too big (" + maxFileSize/1048576 + "mb max)");
            e.getJDA().removeEventListener(this);
            return;
        }

        String query = "INSERT INTO sound(name, guild_id, user_id, extension) VALUES('" 
                     + name + "','" + event.getGuild().getId() + "','" + event.getAuthor().getId() + "','" + attachment.getFileExtension() + "');";
        sql.runQuery(query);
        query = "SELECT id FROM sound WHERE name = '" + name + "' AND guild_id = '" + event.getGuild().getId() + "' AND user_id = '" + event.getAuthor().getId() + "';";
        String id = sql.getString(query, "id");
        if(id == null){
            event.reply("An error with the SQL database occured");
            e.getJDA().removeEventListener(this);
            return;
        }

        File uploadFolder = new File("rsc" + File.separator + "Upload");
        if(!uploadFolder.exists())
            uploadFolder.mkdir();

        File saveFile = new File("rsc" + File.separator + "Upload" + File.separator + (name + "." + attachment.getFileExtension()));

        new FileProxy(attachment.getUrl()).downloadToFile(saveFile)
            .thenAccept(file -> {
                System.out.println("Uploading the file on aws s3 " + file.getName());
                try {
                    PutObjectRequest request = new PutObjectRequest("thebeebot", id, file);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType("audio/mpeg");
                    metadata.addUserMetadata("format", attachment.getFileExtension());
                    request.setMetadata(metadata);
                    s3Client.putObject(request);
                }catch(AmazonClientException ace){
                    ace.printStackTrace();
                }
                file.delete();
            })
            .exceptionally(t -> { // handle failure
                event.reply("An error occured while uploading the file");
                t.printStackTrace();
                e.getJDA().removeEventListener(this);
                return null;
            });
        event.reply("File uploaded succesfully");
        
        e.getJDA().removeEventListener(this);
    }
}