package com.safjnest.Commands.Settings;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.Permission;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Prefix extends Command{
    private GuildSettings gs;
    
    public Prefix(GuildSettings gs){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.gs = gs;
    }

    @Override
    protected void execute(CommandEvent event) {
        String prefix = event.getArgs();
        if(prefix.equals("")) {
            event.reply("Write the new prefix.");
            return;
        }
        String guildId = event.getGuild().getId();
        
        if(DatabaseHandler.updatePrefix(guildId, event.getSelfUser().getId(), prefix)){
            gs.getServer(guildId).setPrefix(prefix);
            event.reply("The new prefix is: " + prefix);
        }
        else
            event.reply("Couldn't change the prefix due to an unknown error, please try again later or report this with /bugsnotifier.");
    }
}