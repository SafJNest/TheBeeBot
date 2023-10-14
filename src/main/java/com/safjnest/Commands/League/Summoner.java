package com.safjnest.Commands.League;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class Summoner extends Command {

    public Summoner(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        Button left = Button.primary("lol-left", "<-");
        Button right = Button.primary("lol-right", "->");
        Button center = Button.primary("lol-center", "f");

        boolean searchByUser = false;
        String args = event.getArgs();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        User theGuy = null;
        if(args.equals("")){
            s = RiotHandler.getSummonerFromDB(event.getAuthor().getId());
            searchByUser = true;
            if(s == null){
                event.reply("You dont have a Riot account connected, check /help setUser (or write the name of a summoner).");
                return;
            }
            theGuy = event.getAuthor();
        }
        else if(event.getMessage().getMentions().getMembers().size() != 0){
            s = RiotHandler.getSummonerFromDB(event.getMessage().getMentions().getMembers().get(0).getId());
            searchByUser = true;
            theGuy = event.getMessage().getMentions().getUsers().get(0);
            if(s == null){
                event.reply(event.getMessage().getMentions().getMembers().get(0).getEffectiveName() + " doesn't have a Riot account connected.");
                return;
            }
        }else{
            s = RiotHandler.getSummonerByName(args);
            if(s == null){
                event.reply("Couldn't find the specified summoner.");
                return;
            }
        }
        
        EmbedBuilder builder = createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(), s);
        
        if(searchByUser && RiotHandler.getNumberOfProfile(theGuy.getId()) > 1){
            searchByUser = true;
            center = Button.primary("center", s.getName());
            center = center.asDisabled();
            event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(left, center, right).queue();
            return;
        }

        event.reply(builder.build());
	}

    public static EmbedBuilder createEmbed(JDA jda, String id, no.stelar7.api.r4j.pojo.lol.summoner.Summoner s){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(s.getName());
        builder.setColor(Color.decode(BotSettingsHandler.map.get(id).color));
        builder.setThumbnail(RiotHandler.getSummonerProfilePic(s));

        String userId = DatabaseHandler.getUserIdByLOLAccountId(s.getAccountId());
        if(userId != null){
            User theGuy = jda.getUserById(userId);
            builder.addField("User:", theGuy.getName(), true);
            builder.addField("Level:", String.valueOf(s.getSummonerLevel()), true);
            builder.addBlankField(true);
        }else{
            builder.addField("Level:", String.valueOf(s.getSummonerLevel()), false);
        }
        
        builder.addField("Solo/duo Queue", RiotHandler.getSoloQStats(jda, s), true);
        builder.addField("Flex Queue", RiotHandler.getFlexStats(jda, s), true);
        String masteryString = "";
        for(int i = 1; i < 4; i++)
            masteryString += RiotHandler.getMastery(jda, s, i) + "\n";
        
        builder.addField("Top 3 Champs", masteryString, false); 
        builder.addField("Activity", RiotHandler.getActivity(jda, s), true);
        return builder;
    }
}