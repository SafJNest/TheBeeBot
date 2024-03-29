package com.safjnest.Utilities;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.SlashCommand;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BotCommand {
    private String name;
    private String category;
    private String help;
    private String[] aliases;
    private int cooldown;
    private String arguments;
    private boolean text;
    private boolean slash;
    private List<String> children = new ArrayList<String>();
    private List<OptionData> options = new ArrayList<OptionData>();

    public BotCommand(Command command) {
        this.name = command.getName();
        this.aliases = command.getAliases();
        this.help = command.getHelp();
        this.cooldown = command.getCooldown();
        this.category = command.getCategory().getName();
        this.arguments = command.getArguments();
        this.text = true;
        this.slash = false;
    }

    public BotCommand(SlashCommand command) {
        this.name = command.getName();
        this.aliases = command.getAliases();
        this.help = command.getHelp();
        this.cooldown = command.getCooldown();
        this.category = command.getCategory().getName();
        this.arguments = command.getArguments();
        this.text = false;
        this.slash = true;
        addSlash(command);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public boolean isSlash() {
        return slash;
    }

    public void setSlash(boolean slash) {
        this.slash = slash;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public void setChildren(SlashCommand[] children) {
        for(SlashCommand child : children) {
            this.children.add(child.getName());
        }
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public void setOptions(List<OptionData> options) {
        this.options = options;
    }

    public void addSlash(SlashCommand command) {
        setSlash(true);
        if(command.getChildren().length > 0)
            setChildren(command.getChildren());
        else
            setOptions(command.getOptions());
        
    }

}