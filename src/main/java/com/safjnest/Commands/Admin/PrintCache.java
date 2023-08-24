package com.safjnest.Commands.Admin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class PrintCache extends Command {
    
    private GuildSettings gs;
    private ExpSystem es;

    public PrintCache(GuildSettings gs, ExpSystem es) {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.hidden = true;

        this.gs = gs;
        this.es = es;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!PermissionHandler.isUntouchable(event.getAuthor().getId()))
            return;
        String msg = "";
        for(GuildData gd : gs.cache.values()){
            try {
                if(!event.getJDA().getGuildById(gd.getId()).getName().startsWith("Beebot"))
                msg += "SERVER: " + event.getJDA().getGuildById(gd.getId()).getName() + " | PREFIX: " + gd.getPrefix() + " | EXP: " + gd.getExpSystem() + "\n";
            } catch (Exception e) {
               continue;
            }
            
        }
        
        
        HashMap<String, ArrayList<User>> users = new HashMap<>();
        for(String s : es.getUsers().keySet()){
            if(!users.containsKey(s.split("-", 2)[1]))
                users.put(s.split("-", 2)[1], new ArrayList<>());
            users.get(s.split("-", 2)[1]).add(event.getJDA().getUserById(s.split("-", 2)[0]));
        }

        for(String s : users.keySet()){
            try {
                msg += "Guild: " + event.getJDA().getGuildById(s).getName() + "\n";
                for(User u : users.get(s))
                    msg += u.getName() + "\n";
            } catch (Exception e) {
                continue;
            }
        }
            

        MessageChannel channel = event.getChannel();
        try {
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
