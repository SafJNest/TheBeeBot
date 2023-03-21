package com.safjnest.Commands.LOL;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class Summoner extends Command {
    
    /**
     * Constructor
     */
    public Summoner(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(CommandEvent event) {
        Button left = Button.primary("left", "<-");
        Button right = Button.primary("right", "->");
        Button center = Button.primary("center", "f");

        boolean searchByUser = false;
        String args = event.getArgs();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        if(args.equals("")){
            s = LOLHandler.getSummonerFromDB(event.getAuthor().getId());
            if(s == null){
                event.reply("You dont have a Riot account connected, for more information /help setUser");
                return;
            }
            searchByUser = true;
            center = Button.primary("center", s.getName());
            center.asDisabled();
            
        }
        else if(event.getMessage().getMentions().getMembers().size() != 0){
            s = LOLHandler.getSummonerFromDB(event.getMessage().getMentions().getMembers().get(0).getId());
            if(s == null){
                event.reply(event.getMessage().getMentions().getMembers().get(0).getEffectiveName() + " has not connected his Riot account.");
                return;
            }
        }else{
            s = LOLHandler.getSummonerByName(args);
            if(s == null){
                event.reply("Didn't find this user. ");
                return;
            }
        }
        
        
        EmbedBuilder builder = createEmbed(event.getJDA().getSelfUser().getId(), s);
        
        if(searchByUser && LOLHandler.getNumberOfProfile(event.getAuthor().getId()) > 1){
            event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(left, center, right).queue();
            return;
        }

        event.reply(builder.build());
            
       

	}

    public static EmbedBuilder createEmbed(String id, no.stelar7.api.r4j.pojo.lol.summoner.Summoner s){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(s.getName());
        builder.setColor(Color.decode(
            BotSettingsHandler.map.get(id).color
        ));
        builder.setThumbnail(LOLHandler.getSummonerProfilePic(s));
        builder.addField("Level:", String.valueOf(s.getSummonerLevel()), false);
        
        builder.addField("5v5 Ranked Solo", LOLHandler.getSoloQStats(s), true);
        builder.addField("5v5 Ranked Flex Queue", LOLHandler.getFlexStats(s), true);
        String masteryString = "";
        for(int i = 1; i < 4; i++)
            masteryString += LOLHandler.getMastery(s, i) + "\n";
        
        builder.addField("Top 3 Champ", masteryString, false); 
        builder.addField("Activity", LOLHandler.getActivity(s), true);
        return builder;
    }

}
