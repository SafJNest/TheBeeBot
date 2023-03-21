package com.safjnest.SlashCommands.LOL;

import java.awt.Color;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class RankMatchSlash extends SlashCommand {
    private R4J r;
    private SQL sql;
    /**
     * Constructor
     */
    public RankMatchSlash(R4J r, SQL sql  ){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "type", "Ranked type", true)
                            .addChoice("Flex", "flex")
                            .addChoice("SoloQueue", "soloqueue"),
            new OptionData(OptionType.STRING, "user", "Summoner name you want to get data", false));
        this.r = r;
        this.sql = sql;
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        event.deferReply(false).queue();
        if(event.getOption("user") == null){
            String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getMember().getId() + "';";
            try {
                s = r.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, sql.getString(query, "account_id"));
            } catch (Exception e) {
               event.deferReply().addContent("You dont have connected your Riot account.").queue();
               return;
            }
        }else{
            try {
                s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, event.getOption("user").getAsString());
            } catch (Exception e) {
                event.deferReply().addContent("Didn't found the user you asked for").queue();
                return;
            }
        }
        try {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(s.getName() + "'s Game");
            builder.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
            ));
            builder.setThumbnail("https://ddragon.leagueoflegends.com/cdn/12.16.1/img/profileicon/"+s.getProfileIconId()+".png");
            String blueSide = "";
            String redSide = "";
            if(event.getOption("type").getAsString().equals("soloqueue")){
                for(SpectatorParticipant partecipant : s.getCurrentGame().getParticipants()){
                    String sum = partecipant.getSummonerName();
                    String stats = LOLHandler.getSoloQStats(LOLHandler.getSummonerById(partecipant.getSummonerId()));
                    stats = stats.substring(0, stats.lastIndexOf("P")+1) + " | " +stats.substring(stats.lastIndexOf(":")+1);
                    if(partecipant.getTeam() == TeamType.BLUE)
                        blueSide += "**" + sum + "** " + stats+ "\n";
                    else
                        redSide += "**" + sum + "** " + stats+ "\n";
                    
                }
            }else{
                for(SpectatorParticipant partecipant : s.getCurrentGame().getParticipants()){
                    String sum = partecipant.getSummonerName();
                    String stats = LOLHandler.getFlexStats(LOLHandler.getSummonerById(partecipant.getSummonerId()));
                    stats = stats.substring(0, stats.lastIndexOf("P")+1) + " | " +stats.substring(stats.lastIndexOf(":")+1);
                    if(partecipant.getTeam() == TeamType.BLUE)
                        blueSide += "**" + sum + "** " + stats+ "\n";
                    else
                        redSide += "**" + sum + "** " + stats+ "\n";
                    
                }
            }
            builder.addField("**BLUE SIDE**", blueSide, false);
            builder.addField("**RED SIDE**", redSide, true);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
            
        } catch (Exception e) {
            event.reply(s.getName() + " is not in a match.");
        }
	}

}
