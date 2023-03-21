package com.safjnest.Commands.Misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 * This commands once is called sends a message with a full list of all commands, grouped by category.
 * <p>The user can then use the command to get more information about a specific command.</p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1.01
 */
public class Help extends Command {
    /**
     * Default constructor for the class.
     */

    GuildSettings gs;
    public Help(GuildSettings gs) {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.gs = gs;
    }
    /**
     * This method is called every time a member executes the command.
     */
    @Override
    protected void execute(CommandEvent event) {
        int nCom = 0;
        String command = event.getArgs();
        EmbedBuilder eb = new EmbedBuilder();
        HashMap<String, ArrayList<Command>> commands = new HashMap<>();
        for (Command e : event.getClient().getCommands()) {
            if(!e.isHidden() || PermissionHandler.isUntouchable(event.getMember().getId())){
                if(!commands.containsKey(e.getCategory().getName()))
                    commands.put(e.getCategory().getName(), new ArrayList<Command>());
                commands.get(e.getCategory().getName()).add(e);
                nCom++;
            }
        }
        eb.setTitle("📒INFO AND COMMAND📒", null);
        eb.setDescription("Current prefix is: **" + gs.getServer(event.getGuild().getId()).getPrefix() + "**\n"
        + "You can get more information using: **"+ gs.getServer(event.getGuild().getId()).getPrefix() +"help <nameCommand>.**");
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        if(command.equals("")){
            String ss = "```\n";
            for(String k : commands.keySet()){ 
                for(Command c : commands.get(k)){
                    ss+= c.getName() + "\n";
                }
                ss+="```";
                eb.addField(k, ss, true);
                ss = "```\n";
            }
            eb.addField("Number of commands avaible:", "```"+nCom+"```", false);
            eb.setFooter("Beebot is continuously updated by the two KINGS ;D", null);
        }else{
            Command e = null;
            for(String k : commands.keySet()){
                for(Command c : commands.get(k)){
                    if(c.getName().equalsIgnoreCase(command) || Arrays.asList(c.getAliases()).contains(command) ){
                        e = c;
                        break;
                    }
                }
            }
            eb.setDescription("**COMMAND " + e.getName().toUpperCase() + "**");
            eb.addField("**DESCRIPTION**","```"+e.getHelp()+"```", false);
            eb.addField("**CATEGORY**","```"+e.getCategory().getName()+"```", false);
            eb.addField("**ARG**","```"+e.getArguments()+"```", true);
            eb.addField("**COOLDOWN**","```"+e.getCooldown()+"```", true);
            if(e.getAliases().length > 0){
                String aliases = "";
                for(String a : e.getAliases())
                    aliases+=a+" - ";
                eb.addField("**ALIASES**","```"+aliases+"```", false);
            }else{
                eb.addField("**ALIASES**","```"+"NULL"+"```", false);
            }

            
            eb.setFooter("IN CASE ARGS IS NULL ITS ENOUGH WRITE JUST THE COMMAND, [] MEANS A REQUIRED FIELD WHITE () DONT ", null);
        }
        eb.addField("**OTHER INFORMATION**", "Beebot has been developed by only two people, so dont break the balls", false);
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",
                event.getJDA().getSelfUser().getAvatarUrl());

        event.getChannel().sendMessageEmbeds(eb.build())
                .queue();

    }

}
