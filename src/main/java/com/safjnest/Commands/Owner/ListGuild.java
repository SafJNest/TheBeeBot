package com.safjnest.Commands.Owner;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.3
 */
public class ListGuild extends Command {

    public ListGuild(){
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.ownerCommand = true;
        this.hidden = true;
    }

	@Override
	protected void execute(CommandEvent event) {
        User self = event.getJDA().getSelfUser();
        List<Guild> guilds = new ArrayList<>(event.getJDA().getGuilds());
        guilds.sort((g1, g2) -> {
            return Long.compare(g1.getMember(self).getTimeJoined().toEpochSecond(), g2.getMember(self).getTimeJoined().toEpochSecond());
        });
        
        String list = "Here is the list of guilds the bot is in (ordered by join date): \n";
        List<String> forbidden = List.of(RiotHandler.getForbiddenServers());
        for(Guild guild : guilds){
            if (!forbidden.contains(guild.getId())) {
                list += "<t:" + guild.getMember(self).getTimeJoined().toEpochSecond() + ":d> - **" + guild.getName() + "** (" + guild.getId() + ")\n";
            }
        }
        event.reply(list);
    }
}
