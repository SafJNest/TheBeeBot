package com.safjnest.Commands.League;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
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
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    /**
     * This method is called every time a member executes the command.
     */
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
                event.reply("You dont have a Riot account connected, for more information /help setUser");
                return;
            }
            theGuy = event.getAuthor();
        }
        else if(event.getMessage().getMentions().getMembers().size() != 0){
            s = RiotHandler.getSummonerFromDB(event.getMessage().getMentions().getMembers().get(0).getId());
            searchByUser = true;
            theGuy = event.getMessage().getMentions().getUsers().get(0);
            if(s == null){
                event.reply(event.getMessage().getMentions().getMembers().get(0).getEffectiveName() + " has not connected his Riot account.");
                return;
            }
        }else{
            s = RiotHandler.getSummonerByName(args);
            if(s == null){
                event.reply("Didn't find this user. ");
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
        builder.setColor(Color.decode(
            BotSettingsHandler.map.get(id).color
        ));
        builder.setThumbnail(RiotHandler.getSummonerProfilePic(s));
        String query = "SELECT guild_id FROM lol_user WHERE account_id = '" + s.getAccountId() + "';";
        String userId = DatabaseHandler.getSql().getString(query, "guild_id");
        if(userId != null){
            User theGuy = jda.getUserById(userId);
            builder.addField("User:", theGuy.getName(), true);
            builder.addField("Level:", String.valueOf(s.getSummonerLevel()), true);
            builder.addBlankField(true);
        }else{
            builder.addField("Level:", String.valueOf(s.getSummonerLevel()), false);
        }
        builder.addField("5v5 Ranked Solo", RiotHandler.getSoloQStats(jda, s), true);
        builder.addField("5v5 Ranked Flex Queue", RiotHandler.getFlexStats(jda, s), true);
        String masteryString = "";
        for(int i = 1; i < 4; i++)
            masteryString += RiotHandler.getMastery(jda, s, i) + "\n";
        
        builder.addField("Top 3 Champ", masteryString, false); 
        builder.addField("Activity", RiotHandler.getActivity(jda, s), true);
        return builder;
    }

}
