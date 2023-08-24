package com.safjnest.Commands.League;
import java.awt.Color;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.Augment;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class InfoAugment extends Command {
    

    /**
     * Constructor
     */
    public InfoAugment(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.hidden = true;
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(CommandEvent event) {
        String args = event.getArgs();
        ArrayList<Augment> augments = RiotHandler.getAugments();
        ArrayList<String> augmentNames = new ArrayList<>();
        for(Augment a : augments){
            augmentNames.add(a.getName());
        }
        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        
        Augment augment = null;
        for(Augment a : augments){
            //regex is number
            if(args.matches("\\d+")){
                if(a.getId().equalsIgnoreCase(args)){
                    augment = a;
                    break;
                }
            }else{
                args = SafJNest.findSimilarWord(args, augmentNames);
                if(a.getName().equalsIgnoreCase(args)){
                    augment = a;
                    break;
                }
            }
        }
        
        RichCustomEmoji emoji = RiotHandler.getRichEmoji(event.getJDA(), "a"+augment.getId());
        eb.setTitle(augment.getName().toUpperCase() + " (" + augment.getId() + ")");
        eb.setDescription(augment.getFormattedDesc());
        eb.setThumbnail(emoji.getImageUrl());
        event.reply(eb.build());
	}

}