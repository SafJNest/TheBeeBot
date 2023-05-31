package com.safjnest.SlashCommands.LOL;

import java.util.Arrays;

import java.awt.Color;
    
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;


/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class RuneSlash extends SlashCommand {
 
    /**
     * Constructor
     */
    public RuneSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "champ", "Champion Name", true),
            new OptionData(OptionType.STRING, "lane", "Champion Lane", true)
                .addChoice("Top Lane", "top")
                .addChoice("Jungle", "jungle")
                .addChoice("Mid Lane", "middle")
                .addChoice("ADC", "bottom")
                .addChoice("Support", "sup"));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        event.deferReply(false).queue();
        R4J r = LOLHandler.getRiotApi();
        
        String champ = "";
        String champName = event.getOption("champ").getAsString();
        String lane = event.getOption("lane").getAsString();
        String laneFormatName =  "";
        switch(lane){
            case "top":
                laneFormatName = "Top Lane";
                break;
            case "jungle":
                laneFormatName = "Jungle";
                break;
            case "middle":
                laneFormatName = "Mid Lane";
                break;
            case "bottom":
                laneFormatName = "ADC";
                break;
            case "sup":
                laneFormatName = "Support";
                break;
        }
        
        if(champName.equalsIgnoreCase("nunu"))
            champName+="willump";

        String[] champions = {"Aatrox", "Ahri", "Akali", "Alistar", "Amumu", "Anivia", "Annie", "Aphelios", "Ashe",
            "Aurelion Sol", "Azir", "Bard", "Blitzcrank", "Brand", "Braum", "Caitlyn", "Camille", "Cassiopeia",
            "Cho'Gath", "Corki", "Darius", "Diana", "Dr. Mundo", "Draven", "Ekko", "Elise", "Evelynn", "Ezreal",
            "Fiddlesticks", "Fiora", "Fizz", "Galio", "Gangplank", "Garen", "Gnar", "Gragas", "Graves", "Hecarim",
            "Heimerdinger", "Illaoi", "Irelia", "Ivern", "Janna", "Jarvan IV", "Jax", "Jayce", "Jhin", "Jinx",
            "Kai'Sa", "Kalista", "Karma", "Karthus", "Kassadin", "Katarina", "Kayle", "Kayn", "Kennen", "Kha'Zix",
            "Kindred", "Kled", "Kog'Maw", "LeBlanc", "Lee Sin", "Leona", "Lillia", "Lissandra", "Lucian", "Lulu",
            "Lux", "Malphite", "Malzahar", "Maokai", "Master Yi", "Milio","Miss Fortune", "Mordekaiser", "Morgana", "Nami",
            "Nasus", "Nautilus", "Neeko", "Nidalee", "Nocturne", "Nunu & Willump", "Olaf", "Orianna", "Ornn", "Pantheon",
            "Poppy", "Pyke", "Qiyana", "Quinn", "Rakan", "Rammus", "Rek'Sai", "Rell", "Renekton", "Rengar", "Riven",
            "Rumble", "Ryze", "Samira", "Sejuani", "Senna", "Seraphine", "Sett", "Shaco", "Shen", "Shyvana", "Singed",
            "Sion", "Sivir", "Skarner", "Sona", "Soraka", "Swain", "Sylas", "Syndra", "Tahm Kench", "Taliyah", "Talon",
            "Taric", "Teemo", "Thresh", "Tristana", "Trundle", "Tryndamere", "Twisted Fate", "Twitch", "Udyr", "Urgot",
            "Varus", "Vayne", "Veigar", "Vel'Koz", "Vi", "Viktor", "Vladimir", "Volibear", "Warwick", "Wukong", "Xayah",
            "Xerath", "Xin Zhao", "Yasuo", "Yone", "Yorick", "Yuumi", "Zac", "Zed", "Ziggs", "Zilean", "Zoe", "Zyra"};
        
        champName = findChampion(champName, champions);
        for(StaticChampion c : r.getDDragonAPI().getChampions().values()){
            if(c.getName().equalsIgnoreCase(champName))
                champ = String.valueOf(c.getId());
            
        }

        URL url;
        String msg = "";
        try {
            url = new URL("https://axe.lolalytics.com/mega/?ep=rune&p=d&v=1&cid="+champ+"&lane="+lane);
            System.out.println(url);
            String json = IOUtils.toString(url, Charset.forName("UTF-8"));
            EmbedBuilder eb = new EmbedBuilder(); 
            eb = new EmbedBuilder(); 
            eb.setTitle(":sparkles:Beebot Rune Command"); 
            eb.setDescription("**Highest Win Rate** info for " + LOLHandler.getFormattedEmoji(event.getJDA(), champName) + " " + champName + " " + LOLHandler.getFormattedEmoji(event.getJDA(), laneFormatName) + " **" + laneFormatName + "**");
            eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl()); 
            
            msg = "​\n"; //!!there is a 0 width character before the /n
            for(String id : getPrin(json, "pri")){
                msg += LOLHandler.getFormattedEmoji(event.getJDA(), id) + " " + LOLHandler.getRunesHandler().get(getRunePage(json, "pri")).getRune(id).getName() + "\n";
            }
            String support = LOLHandler.getRunesHandler().get(getRunePage(json, "pri")).getName();
            eb.addField(LOLHandler.getFormattedEmoji(event.getJDA(), support) + " " + support, msg, true);

            msg = "​\n"; //!!there is a 0 width character before the /n
            for(String id : getPrin(json, "sec")){
                msg += LOLHandler.getFormattedEmoji(event.getJDA(), id) + " " + LOLHandler.getRunesHandler().get(getRunePage(json, "sec")).getRune(id).getName() + "\n";
            }
            support = LOLHandler.getRunesHandler().get(getRunePage(json, "sec")).getName();
            eb.addField(LOLHandler.getFormattedEmoji(event.getJDA(), support) + " " + support, msg, true);
            
            eb.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
            ));
            
            champName = LOLHandler.transposeChampionNameForDataDragon(champName);
            eb.setThumbnail(LOLHandler.getChampionProfilePic(champName));
            eb.setFooter("There could be some issues with champions name, like Dr. Mundo, Aurelion Sol...Be careful when you digit it.", null); 

            
            event.getHook().editOriginalEmbeds(eb.build()).queue();
        } catch (Exception e) { 
            event.getHook().editOriginal("Could be some problem with our database or lack of data due to new patch. Try again later.").queue();
        } 

	}

    public String[] getPrin(String json, String thing){ 
        JSONParser parser = new JSONParser(); 
        try {
            JSONObject file = (JSONObject) parser.parse(json); 
            JSONObject summary = (JSONObject) file.get("summary"); 
            JSONObject runes = (JSONObject) summary.get("runes"); 
            JSONObject win = (JSONObject) runes.get("pick"); 
            JSONObject set = (JSONObject) win.get("set"); 
            JSONArray theThing = (JSONArray) set.get(thing); 
            String[] result = new String[theThing.size()]; 
            for (int i = 0; i < theThing.size(); i++) { 
                result[i] = String.valueOf(theThing.get(i)); 
            } 
            return result;       
        } catch (Exception e) { 
            e.printStackTrace(); 
            return null; 
        }
    }

    public String getRunePage(String json,String thing){
        JSONParser parser = new JSONParser();
        try {
            
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("summary");
            JSONObject runes = (JSONObject) summary.get("runes");
            JSONObject win = (JSONObject) runes.get("pick");
            JSONObject page = (JSONObject) win.get("page");
            return String.valueOf(page.get(thing));
              
        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }
    }

    public static String findChampion(String input, String[] champions) {
        double maxSimilarity = 0;
        String championName = "";
        
        for (String champion : champions) {
            double similarity = calculateSimilarity(input, champion);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                championName = champion;
            }
        }
        
        return championName;
    }
    
    private static double calculateSimilarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
    
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
    
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }
        
        

}
