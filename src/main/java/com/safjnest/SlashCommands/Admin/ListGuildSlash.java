package com.safjnest.SlashCommands.Admin;

import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.entities.Guild;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.3
 */
public class ListGuildSlash extends SlashCommand {

    public ListGuildSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.hidden = true;
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        List<Guild> guilds = event.getJDA().getGuilds();
        String list = "Here the list where the bot is in: \n";
        for(Guild guild : guilds){
            list+="**"+guild.getName()+"("+guild.getId()+")** - ";
        }
        list = list.substring(0, list.length()-3);
        event.deferReply(false).addContent(list).queue();
    }
}