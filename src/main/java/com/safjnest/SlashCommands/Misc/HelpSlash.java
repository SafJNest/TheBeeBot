package com.safjnest.SlashCommands.Misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * This commands once is called sends a message with a full list of all commands, grouped by category.
 * <p>The user can then use the command to get more information about a specific command.</p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1.01
 */
public class HelpSlash extends SlashCommand {
    /**
     * Default constructor for the class.
     */
    GuildSettings gs;
    public HelpSlash(GuildSettings gs) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "command", "Name of the command you want the information about", false));
        this.gs = gs;
    }
    /**
     * This method is called every time a member executes the command.
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        int nCom = 0;
        String command = (event.getOption("command") == null)? "" : event.getOption("command").getAsString();
        EmbedBuilder eb = new EmbedBuilder();
        HashMap<String, ArrayList<SlashCommand>> commands = new HashMap<>();
        for (SlashCommand e : event.getClient().getSlashCommands()) {
            if(!e.isHidden() || PermissionHandler.isUntouchable(event.getMember().getId())){
                if(!commands.containsKey(e.getCategory().getName()))
                    commands.put(e.getCategory().getName(), new ArrayList<SlashCommand>());
                commands.get(e.getCategory().getName()).add(e);
                nCom++;
            }
        }
        eb.setTitle("📒INFO AND COMMAND📒", null);
        eb.setDescription("Current prefix is: **" + gs.getServer(event.getGuild().getId()).getPrefix() + "**\n"
        + "You can get more information using: **/help <command>.**");
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
            SlashCommand e = null;
            for(String k : commands.keySet()){
                for(SlashCommand c : commands.get(k)){
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

        if(PermissionHandler.isUntouchable(event.getMember().getId()))
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
        else
            event.replyEmbeds(eb.build()).setEphemeral(false).queue();
    }

}
