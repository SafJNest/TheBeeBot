package com.safjnest.SlashCommands.League;


import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.Augment;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.Color;

public class InfoAugmentSlash extends SlashCommand {

    public InfoAugmentSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "augment", "Augment name", true).setAutoComplete(true));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        String aug = event.getOption("augment").getAsString();
        Augment augment = null;
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        
        for(Augment a : RiotHandler.getAugments()){
            if(a.getId().equalsIgnoreCase(aug)){
                    augment = a;
                    break;
            }
        }
        
        RichCustomEmoji emoji = RiotHandler.getRichEmoji(event.getJDA(), "a"+augment.getId());
        eb.setTitle(augment.getName().toUpperCase() + " (" + augment.getId() + ")");
        eb.setDescription(augment.getFormattedDesc());
        eb.setThumbnail(emoji.getImageUrl());
        event.replyEmbeds(eb.build()).queue();
    }
}
