package com.safjnest.Utilities;

import java.io.File;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileProxy;

/**
 * This is an auxiliary class for the command {@link com.safjnest.Commands.Audio.Upload Upload}.
 * <p>Every time a member wants to upload a new file, this class will be called.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class FileListener extends ListenerAdapter {
    /**name of the file to be uploaded */
    private String name;

    /**
     * Constructor for the class.	
     * @param name name of the file to be uploaded
     */
    public FileListener(String name){
        this.name = name;
    }
    
    /**
     * This method is called every time a member sends a message or a file.
     * <p>If a message is sent there will be an exception, otherwise the file will be
     * temporarily saved in the directory {@code rsc/Soundboard}
     * @param event the event that was triggered
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        File saveFile = new File("rsc" + File.separator + "Upload" + File.separator + (name +"."+ event.getMessage().getAttachments().get(0).getFileExtension()));
        new FileProxy(event.getMessage().getAttachments().get(0).getUrl()).downloadToFile(saveFile)
            .thenAccept(file -> System.out.println("Saved attachment to " + file.getName()))
            .exceptionally(t ->
            { // handle failure
                t.printStackTrace();
                return null;
            });
        event.getJDA().removeEventListener(this);
    }
}