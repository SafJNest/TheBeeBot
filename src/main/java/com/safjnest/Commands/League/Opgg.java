package com.safjnest.Commands.League;


import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.match.v5.PerkSelection;
import no.stelar7.api.r4j.pojo.lol.match.v5.PerkStyle;
import no.stelar7.api.r4j.pojo.shared.RiotAccount;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class Opgg extends Command {
    /**
     * Constructor
     */
    public Opgg() {
        this.name = this.getClass().getSimpleName().toLowerCase();
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
        Button left = Button.primary("match-left", "<-");
        Button right = Button.primary("match-right", "->");
        Button center = Button.primary("match-center", "f");

        boolean searchByUser = false;
        String args = event.getArgs();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        if(args.equals("")){
            s = RiotHandler.getSummonerFromDB(event.getAuthor().getId());
            if(s == null){
                event.reply("You dont have a Riot account connected, check /help setUser (or write the name of a summoner).");
                return;
            }
            searchByUser = true;
            center = Button.primary("center", s.getName());
            center = center.asDisabled();
            
        }
        else if(event.getMessage().getMentions().getMembers().size() != 0){
            s = RiotHandler.getSummonerFromDB(event.getMessage().getMentions().getMembers().get(0).getId());
            if(s == null){
                event.reply(event.getMessage().getMentions().getMembers().get(0).getEffectiveName() + " doesn't have a Riot account connected.");
                return;
            }
        }else{
            String name = "";
            String tag = "";
            if (!args.contains("#")){
                name = args;
                tag = "EUW";
            }
            else {
                name = args.split("#", 2)[0];
                tag = args.split("#", 2)[1];
            }
            s = RiotHandler.getSummonerByName(name, tag);
            if(s == null){
                event.reply("Couldn't find the specified summoner. Remember to use the tag!");
                return;
            }
        }
        
        
        EmbedBuilder builder = createEmbed(s, event.getJDA());
        
        if(searchByUser && RiotHandler.getNumberOfProfile(event.getAuthor().getId()) > 1){
            event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(left, center, right).queue();
            return;
        }

        event.reply(builder.build());
        
    }
    
    
    
    public static EmbedBuilder createEmbed(no.stelar7.api.r4j.pojo.lol.summoner.Summoner s , JDA jda){
        RiotAccount account = RiotHandler.getRiotApi().getAccountAPI().getAccountByPUUID(RegionShard.EUROPE, s.getPUUID());
        EmbedBuilder eb = new EmbedBuilder();
        MatchParticipant me = null;
        LOLMatch match = null;
        R4J r4j = RiotHandler.getRiotApi();
        eb.setAuthor(account.getName() + "#" + account.getTag());
        
        for(int i = 0; i < 5; i++){
            try {
                
                match = r4j.getLoLAPI().getMatchAPI().getMatch(RegionShard.EUROPE, s.getLeagueGames().get().get(i));
                for(MatchParticipant mp : match.getParticipants()){
                    if(mp.getSummonerId().equals(s.getSummonerId())){
                        me = mp;
                    }
                }
                ArrayList<String> blue = new ArrayList<>();
                ArrayList<String> red = new ArrayList<>();
                for(MatchParticipant searchMe : match.getParticipants()){
                    RiotAccount searchAccount = r4j.getAccountAPI().getAccountByPUUID(RegionShard.EUROPE, searchMe.getPuuid());
                    if(searchMe.getSummonerId().equals(s.getSummonerId()))
                        me = searchMe;
                    String supp = RiotHandler.getFormattedEmoji(jda, searchMe.getChampionName()) 
                                    + " " 
                                    + (searchMe.getSummonerName().equals(me.getSummonerName()) 
                                        ? "**" + searchAccount.getName()+ "#" + searchAccount.getTag() + "**" 
                                        : searchAccount.getName()+ "#" + searchAccount.getTag());
    
                    if(searchMe.getTeam() == TeamType.BLUE)
                        blue.add(supp);
                    else
                        red.add(supp);
                }
    
                String kda = me.getKills() + "/" + me.getDeaths()+ "/" + me.getAssists();
                String content = "";
                Instant instant = Instant.ofEpochMilli(match.getGameCreation() + match.getGameDurationAsDuration().toMillis() + 3600000*2);
                ZoneOffset offset = ZoneOffset.UTC;
                OffsetDateTime offsetDateTime = instant.atOffset(offset);
                String date = DateHandler.formatDate(offsetDateTime);
                date = date.substring(date.indexOf("(")+1, date.indexOf(")"));
                switch (match.getQueue()){

                    case CHERRY:
                        
                        content = RiotHandler.getFormattedEmoji(jda, me.getChampionName()) + kda +"\n"
                        + date + " | **"+ getFormattedDuration((match.getGameDuration()))  + "**\n"
                        + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getSummoner1Id()) + "_") + RiotHandler.getFormattedEmoji(jda, "a" + String.valueOf(me.getPlayerAugment1())) + " " + RiotHandler.getFormattedEmoji(jda, "a" + String.valueOf(me.getPlayerAugment2())) + "\n"
                        + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getSummoner2Id()) + "_") + RiotHandler.getFormattedEmoji(jda, "a" + String.valueOf(me.getPlayerAugment3())) + " " + RiotHandler.getFormattedEmoji(jda, "a" + String.valueOf(me.getPlayerAugment4())) + "\n"
                        + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem0())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem1())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem2())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem3())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem4())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem5())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem6()));
                       
                        eb.addField(
                            "ARENA: " + (me.didWin() ? "WIN" : "LOSE") , content, true);

                        HashMap<String, ArrayList<String>> prova = new HashMap<>();
                        prova.put("teamscuttles", new ArrayList<>());
                        prova.put("teamporos", new ArrayList<>());
                        prova.put("teamkrugs", new ArrayList<>());
                        prova.put("teamminions", new ArrayList<>());
                        int cont = 0;
                        for(MatchParticipant mt : match.getParticipants()){
                            RiotAccount searchAccount = r4j.getAccountAPI().getAccountByPUUID(RegionShard.EUROPE, mt.getPuuid());

                            String nameAccount = searchAccount.getName()+ "#" + searchAccount.getTag();
                            String name = ((mt.getSummonerName().equals(s.getName())) ? "**" + nameAccount + "**" : nameAccount);
                            if(cont < 2){
                                prova.get("teamscuttles").add(RiotHandler.getFormattedEmoji(jda, "teamscuttles") + " " + RiotHandler.getFormattedEmoji(jda, mt.getChampionName()) +name);
                            }
                            if(cont >= 2 && cont < 4){
                                prova.get("teamporos").add(RiotHandler.getFormattedEmoji(jda, "teamporos") + " " + RiotHandler.getFormattedEmoji(jda, mt.getChampionName()) +name);
                            }
                            if(cont >= 4 && cont < 6){
                                prova.get("teamkrugs").add(RiotHandler.getFormattedEmoji(jda, "teamkrugs") + " " + RiotHandler.getFormattedEmoji(jda, mt.getChampionName()) +name);
                            }
                            if(cont >= 6 ){
                                prova.get("teamminions").add(RiotHandler.getFormattedEmoji(jda, "teamminions") + " " + RiotHandler.getFormattedEmoji(jda, mt.getChampionName()) +name);
                            }
                            cont++;
                        }
                        String blueTeam = "";
                        String redTeam = "";
                        blueTeam = ""
                                    + prova.get("teamminions").get(0)  + "\n"
                                    + prova.get("teamminions").get(1)+ "\n\n"
                                    + prova.get("teamkrugs").get(0) + "\n"
                                    + prova.get("teamkrugs").get(1) + "\n";
                        redTeam = ""
                                + prova.get("teamporos").get(0) + "\n"
                                + prova.get("teamporos").get(1) + "\n\n"
                                + prova.get("teamscuttles").get(0) + "\n"
                                + prova.get("teamscuttles").get(1) + "\n";
                        eb.addField("Participant", blueTeam, true);
                        eb.addField("Participant", redTeam, true);
                    break;

                    default:
                     content = RiotHandler.getFormattedEmoji(jda, me.getChampionName()) + kda + " | " + "**Vision: **"+ me.getVisionScore()+"\n"
                                + date  + " | ** " + getFormattedDuration((match.getGameDuration())) + "**\n"
                                + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getSummoner1Id()) + "_") + getFormattedRunes(me, jda, 0) + "\n"
                                + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getSummoner2Id()) + "_") + getFormattedRunes(me, jda, 1) + "\n"
                                + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem0())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem1())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem2())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem3())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem4())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem5())) + " " + RiotHandler.getFormattedEmoji(jda, String.valueOf(me.getItem6()));
                                eb.addField(
                                    match.getQueue().commonName() + ": " + (me.didWin() ? "WIN" : "LOSE") , content, true);
                                String blueS = "";
                                String redS = "";
                                for(int j = 0; j < 5; j++)
                                    blueS += blue.get(j) + "\n";
                                 for(int j = 0; j < 5; j++)
                                    redS += red.get(j) + "\n";
                                eb.addField("Blue Side", blueS, true);
                                eb.addField("Red Side", redS, true);
                    break;


                }

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return eb;
    }

    private static String getFormattedDuration(int seconds) {
        int S = seconds % 60;
        int H = seconds / 60;
        int M = H % 60;
        return M + "m: " + S + "s";
    }

    private static String getFormattedRunes(MatchParticipant me, JDA jda, int row) {
        String prova = "";
        PerkStyle perkS = me.getPerks().getPerkStyles().get(row);

        prova += RiotHandler.getFormattedEmoji(jda, RiotHandler.getFatherRune(perkS.getSelections().get(0).getPerk()));
        for (PerkSelection perk : perkS.getSelections()) {
            prova += RiotHandler.getFormattedEmoji(jda, perk.getPerk());
        }
        return prova;

    }

   
}
