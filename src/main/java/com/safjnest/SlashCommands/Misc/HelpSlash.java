package com.safjnest.SlashCommands.Misc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Guild.GuildSettings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * This commands once is called sends a message with a full list of all
 * commands, grouped by category.
 * <p>
 * The user can then use the command to get more information about a specific
 * command.
 * </p>
 * 
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
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
                new OptionData(OptionType.STRING, "command", "Name of the command you want the information about",
                        false)
                        .setAutoComplete(true));
        this.gs = gs;
    }

    /**
     * This method is called every time a member executes the command.
     */
    @Override
    protected void execute(SlashCommandEvent event) {
        int nCom = 0;
        String command = (event.getOption("command") == null) ? "" : event.getOption("command").getAsString();
        
        HashMap<String, ArrayList<SlashCommand>> commands = new HashMap<>();
        EmbedBuilder eb = new EmbedBuilder();
        
        for (SlashCommand e : event.getClient().getSlashCommands()) {
            if (!e.isHidden() || PermissionHandler.isUntouchable(event.getMember().getId())) {
                if (!commands.containsKey(e.getCategory().getName()))
                    commands.put(e.getCategory().getName(), new ArrayList<SlashCommand>());
                commands.get(e.getCategory().getName()).add(e);
                nCom++;
            }
        }

        eb.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));

        if (command.equals("")) {
            eb.setTitle("📒INFO AND COMMAND📒", null);
            eb.setDescription("Current prefix is: **" + gs.getServer(event.getGuild().getId()).getPrefix() + "**\n"
                + "You can get more information using: **/help <command>.**");

            String ss = "```\n";
            for (String k : getKeysInDescendingOrder(commands)) {
                Collections.sort(commands.get(k), Comparator.comparing(Command::getName));
                for (Command c : commands.get(k)) {
                    ss += c.getName() + "\n";
                }
                ss += "```";
                eb.addField(k, ss, true);
                ss = "```\n";
            }
            eb.addField("Number of commands avaible:", "```" + nCom + "```", false);
            eb.setFooter("Beebot is continuously updated by the two KINGS ;D", null);

        } else {
            SlashCommand e = null;
            for (String k : commands.keySet()) {
                for (SlashCommand c : commands.get(k)) {
                    if (c.getName().equalsIgnoreCase(command) || Arrays.asList(c.getAliases()).contains(command)) {
                        e = c;
                        break;
                    }
                }
            }
            eb.setTitle(e.getName().toUpperCase(), null);
            eb.addField("**DESCRIPTION**", "```" +
                    ((e.getHelp().equals("json"))
                            ? new CommandsLoader().getString(e.getName(), "help")
                            : e.getHelp())
                    + "```", false);

            String args = "";
            /**
             * if the command has children, it means that it has subcommands
             * so the args are just /command subcommand 1...
             * otherwise, we have to generate all the possible combinations of the options
             */
            if (e.getChildren().length > 0) {
                args += "/" + e.getName() + "\n";
                for (SlashCommand c : e.getChildren())
                    args += "/" + e.getName() + " " + c.getName() + "\n";
            } else {
                List<OptionData> requiredOptions = new ArrayList<>();
                List<OptionData> optionalOptions = new ArrayList<>();

                for (OptionData op : e.getOptions()) {
                    if (op.isRequired()) {
                        requiredOptions.add(op);
                    } else {
                        optionalOptions.add(op);
                    }
                }

                List<List<OptionData>> combinations = new ArrayList<>();
                combinations.add(requiredOptions);

                if (!optionalOptions.isEmpty()) {
                    for (int i = 0; i < optionalOptions.size(); i++) {
                        List<List<OptionData>> temp = new ArrayList<>();
                        for (List<OptionData> combination : combinations) {
                            temp.add(new ArrayList<>(combination));
                            combination.add(optionalOptions.get(i));
                        }
                        combinations.addAll(temp);
                    }
                }

                StringBuilder argsBuilder = new StringBuilder();

                for (int i = combinations.size() - 1; i >= 0; i--) {
                    List<OptionData> combination = combinations.get(i);
                    argsBuilder.append("/").append(e.getName());
                    for (OptionData op : combination) {
                        argsBuilder.append(" ").append(op.getName());
                    }
                    argsBuilder.append("\n");
                }
                args = argsBuilder.toString();
            }

            eb.addField("**ARG**", "```" + args + "```", false);
            eb.addField("**CATEGORY**", "```" + e.getCategory().getName() + "```", true);
            eb.addField("**COOLDOWN**", "```" + e.getCooldown() + "```", true);
        }

        eb.addField("**OTHER INFORMATION**", "Beebot has been developed by only two people, so dont break the balls", false);
        eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest", event.getJDA().getSelfUser().getAvatarUrl());
        event.replyEmbeds(eb.build()).setEphemeral(false).queue();
    }

    public List<String> getKeysInDescendingOrder(HashMap<String, ArrayList<SlashCommand>> map) {
        List<String> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                return Integer.compare(map.get(key2).size(), map.get(key1).size());
            }
        });
        return keys;
    }

}