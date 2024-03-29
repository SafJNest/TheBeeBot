package com.safjnest.Commands.Owner;



import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * 
 * @since 1.0
 */
public class PrintCache extends Command {
    
    private GuildSettings gs;

    public PrintCache(GuildSettings gs) {
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.ownerCommand = true;
        this.hidden = true;

        this.gs = gs;
    }

    @Override
    protected void execute(CommandEvent event) {
        ArrayList<String> cache = new ArrayList<>();
        String msg = "";
        msg += "Guilds cached: " + gs.cache.size() + "\n";

        for(GuildData gd : gs.cache.values()){
            try {
                if(!event.getJDA().getGuildById(gd.getId()).getName().startsWith("Beebot")) {
                    msg += "**" + event.getJDA().getGuildById(gd.getId()).getName() + "**```"
                        + "Prefix: " + gd.getPrefix() + "\n"
                        + "ExpSystem: " + (gd.isExpSystemEnabled() ? "enabled" : "disabled") + "\n"
                        + "Users: " + gd.getUsers().size() + "\n"
                        + "Channels: " + gd.getChannels().size() + "\n"
                        + "Alerts: " + (gd.isAlertsCached() ? "cached" : "not cached") + "\n"
                        + "BlackList: " + (gd.isBlackListCached() ? "cached" : "not cached") + "```";
                    cache.add(msg);
                    msg = "";
                }

            } catch (Exception e) {
               continue;
            }
        }

        MessageChannel channel = event.getChannel();
        for(String s : cache){
            channel.sendMessage(s).queue();
        }
    }
}
