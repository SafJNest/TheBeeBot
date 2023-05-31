package com.safjnest.Commands.Admin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class PrefixList extends Command {

    public PrefixList() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!PermissionHandler.isUntouchable(event.getAuthor().getId()))
            return;
        String msg = "";
        ArrayList<ArrayList<String>> arr = null;
        MessageChannel channel = event.getChannel();
        try {
            String query = "SELECT guild_id, prefix FROM guild_settings WHERE bot_id = '"+event.getSelfUser().getId()+"';";
            arr = DatabaseHandler.getSql().getAllRows(query, 2);
            for(int i = 0; i < arr.size(); i++){
                    msg += "*" + event.getJDA().getGuildById(arr.get(i).get(0)).getName() + "*: " + "**"+arr.get(i).get(1)+"**\n";
            }
            if (msg.length() > 2000) {
                File supp = new File("prefix.txt");
                FileWriter app;
                try {
                    app = new FileWriter(supp);
                    app.write(msg);
                    app.flush();
                    app.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                channel.sendMessage("Too many prefixes to be sent.").queue();
                channel.sendFiles(FileUpload.fromData(supp)).queue();
            } else {
                channel.sendMessage(msg).queue();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            channel.sendMessage(e.getMessage()).queue();
        }
    }
}
