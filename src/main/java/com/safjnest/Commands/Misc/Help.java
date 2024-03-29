package com.safjnest.Commands.Misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.safjnest.Bot;
import com.safjnest.Utilities.BotCommand;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * This commands once is called sends a message with a full list of all commands, grouped by category.
 * <p>The user can then use the command to get more information about a specific command.</p>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1.01
 */
public class Help extends Command {

    GuildSettings gs;
    public Help(GuildSettings gs) {
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.gs = gs;
    }

    @Override
    protected void execute(CommandEvent event) {
        String prefix = gs.getServer(event.getGuild().getId()).getPrefix();
        String inputCommand = event.getArgs();
        EmbedBuilder eb = new EmbedBuilder();

        HashMap<String, BotCommand> commands = new HashMap<>();

        for (Command e : event.getClient().getCommands())
            if(!e.isHidden() || PermissionHandler.isUntouchable(event.getMember().getId()))
                commands.put(e.getName(), new BotCommand(e));

        for (SlashCommand e : event.getClient().getSlashCommands()) {
            if(!e.isHidden() || PermissionHandler.isUntouchable(event.getMember().getId())){
                if(!commands.containsKey(e.getName()))
                    commands.put(e.getName(), new BotCommand(e));
                else
                    commands.get(e.getName()).addSlash(e);
            }
        }

        HashMap<String, List<BotCommand>> categories = new HashMap<>();

        for(String command : commands.keySet())
            categories.computeIfAbsent(commands.get(command).getCategory(), k -> new ArrayList<>()).add(commands.get(command));

        for(String category : categories.keySet()) {
            categories.get(category).sort((c1, c2) -> {
                int c1p = (c1.isText() ? 2 : 0) + (c1.isSlash() ? 1 : 0);
                int c2p = (c2.isText() ? 2 : 0) + (c2.isSlash() ? 1 : 0);
                if(c1p == c2p)
                    return c1.getName().compareTo(c2.getName());
                return Integer.compare(c1p, c2p);
            });
        }

        if(inputCommand.equals("")) {
            eb.setTitle("📒INFO AND COMMANDS📒");
            eb.setDescription("Current prefix is: **" + prefix + "**\n"
                + "For more information on a command: **" + prefix + "help <command name>.**\n"
                + "In **brackets** is specified if the command is **text only** or **slash only**.\n"
                + "If it doesn't have brackets it's **both**. **s** means the command has **sub-commands**.");
            String ss = "```\n";

            for(String category : getCategoriesBySize(categories)) {
                for(BotCommand command : categories.get(category)) {
                    String brackets = "";
                    if(command.isText() != command.isSlash())
                        brackets += " [" + (command.isText() ? prefix : "") + (command.isSlash() ? "/" : "") + ((command.getChildren().size() != 0) ? "s" : "") + "]";
                    else if(command.getChildren().size() != 0)
                        brackets += " [s]";
                    ss += command.getName() + brackets + "\n";
                }
                ss +="```";
                eb.addField(category, ss, true);
                ss = "```\n";
            }

            int fieldNum = categories.size();
            while (fieldNum++ % 3 != 0)
                eb.addField("\u200E", "\u200E", true);
            
            eb.addField("Number of commands avaible:", "```" + commands.size() + "```", false);
            eb.setFooter("Sorry if things don't work, Beebot was made for fun by only 2 people.", null);
        } 
        else {
            if(!commands.containsKey(inputCommand.toLowerCase())) {
                event.reply("Command not found");
                return;
            }
            
            BotCommand commandToPrint = commands.get(inputCommand.toLowerCase());

            eb.setTitle("**📒" + commandToPrint.getName().toUpperCase() + " COMMAND📒**");
            eb.setDescription("```" + commandToPrint.getHelp() + "```");
            eb.addField("**Category**", "```" + commandToPrint.getCategory() + "```", true);
            eb.addField("**Cooldown**", "```" + commandToPrint.getCooldown() + "s```", true);
            if(commandToPrint.isText()) {
                eb.addField("**Arguments** [text]", "```" + commandToPrint.getArguments() + "```", false);
            }
            if(commandToPrint.isSlash()) {
                if(commandToPrint.getChildren().size() > 0) {
                    eb.addField("**Children** [slash]", "```" + commandToPrint.getChildren() + "```", false);
                }
                else if(commandToPrint.getOptions().size() > 0) {
                    String options = "";
                    for(OptionData option : commandToPrint.getOptions()) {
                        if(option.isRequired()) {
                            options += option.getName() + " - " + option.getDescription() + " [required]\n";
                        }
                    }
                    for(OptionData option : commandToPrint.getOptions()) {
                        if(!option.isRequired()) {
                            options += option.getName() + " - " + option.getDescription() + "\n";
                        }
                    }
                    eb.addField("**Options** [slash]", "```" + options + "```", false);
                }
            }
            
            String aliases = "";
            if(commandToPrint.getAliases().length > 0) {
                for(String a : commandToPrint.getAliases())
                    aliases += a + " - ";
                aliases = aliases.substring(0, aliases.length() - 3);
            }
            else {
                aliases = "No aliases";
            }
            eb.addField("**Aliases**", "```" + aliases + "```", false);

            eb.setFooter("In arguments [] is a required field and () is an optional field", null);
        }
        eb.setColor(Color.decode(Bot.getColor()));
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest", event.getJDA().getSelfUser().getAvatarUrl());
        event.reply(eb.build());
    }

    public List<String> getCategoriesBySize(HashMap<String, List<BotCommand>> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        keys.sort((k1, k2) -> {
            return Integer.compare(map.get(k2).size(), map.get(k1).size());
        });
        return keys;
    }
}
