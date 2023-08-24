package com.safjnest.Commands.League;

import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class Champ extends Command {
    

    /**
     * Constructor
     */
    public Champ(){
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
        
        R4J r = RiotHandler.getRiotApi();
        
        String champ = event.getArgs().split(" ")[0]; //samira
        String lane = event.getArgs().split(" ")[0];

        for(StaticChampion c : r.getDDragonAPI().getChampions().values()){
            if(c.getName().equalsIgnoreCase(champ))
                champ = String.valueOf(c.getId());
        }
        URL url;
        String msg = "";
        try {
            url = new URL("https://axe.lolalytics.com/mega/?ep=rune&p=d&v=1&cid="+champ+"&lane="+lane);
            String json = IOUtils.toString(url, Charset.forName("UTF-8"));
            String page =  getRunePage(json, "pri");
            msg = "**" + RiotHandler.getRunesHandler().get(page).getName() + "**\n";
            for(String id : getPrin(json, "pri")){
                msg+= RiotHandler.getRunesHandler().get(page).getRune(id).getName() + "\n";
            }

            page =  getRunePage(json, "sec");
            msg += "\n**" + RiotHandler.getRunesHandler().get(page).getName() + "**\n";
            for(String id : getPrin(json, "sec")){
                msg+= RiotHandler.getRunesHandler().get(page).getRune(id).getName() + "\n";
            }

            
        
            event.reply(msg);
        } catch (Exception e) {
            e.printStackTrace();
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

    public String getMainRuneName(String json, String id){
        JSONParser parser = new JSONParser();
        try {
            
            JSONArray file = (JSONArray) parser.parse(json);
            for(int i = 0; i < 5; i++){
                JSONObject a = (JSONObject)file.get(i);
                if(String.valueOf(a.get("id")).equals(id)){
                    return String.valueOf(a.get("name"));
                }
            }
            return null;
              
        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }
    }

    public String getRuneName(String json, String mainId, String id, int row){
        JSONParser parser = new JSONParser();
        try {
            
            JSONArray file = (JSONArray) parser.parse(json);
            for(int i = 0; i < 5; i++){
                JSONObject a = (JSONObject)file.get(i);
                if(String.valueOf(a.get("id")).equals(mainId)){
                    JSONArray slots = (JSONArray)a.get("slots");
                    JSONObject runes = (JSONObject)slots.get(row);
                    JSONArray rowRune = (JSONArray)runes.get("runes");
                    for(int j = 0; j < rowRune.size(); j++){
                        JSONObject rune = (JSONObject)rowRune.get(j);
                        if(String.valueOf(rune.get("id")).equals(id)){
                            return String.valueOf(rune.get("name"));
                        }
                        
                    }
                }
            }
            return null;
              
        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }
    }

    

}