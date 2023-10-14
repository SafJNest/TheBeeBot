package com.safjnest.Utilities.LOL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 */
public class MobalyticsHandler {

    /**
     * Return a json with all the statistic for a champion
     * 
     * @param champName the name of the champion
     * 
     * @param lane the lane of the champion
     * 
     * @return a json with all the statistic for a champion
     * 
     */
    public static String getChampioStats(String champName, String lane) {
        try {
            URL url = new URL("https://widget.mobalytics.gg/lol/graphql/v1/query");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\r\n  \"query\": \"query ChampionQuery($champion: String! = \\\"akali\\\", $role: Rolename! = UNKNOWN) {\\n  lol {\\n    champion(filters: {slug: $champion, role: $role}) {\\n      build {\\n        championSlug\\n        items {\\n          type\\n          items\\n          __typename\\n        }\\n        name\\n        patch\\n        perks {\\n          IDs\\n          style\\n          subStyle\\n          __typename\\n        }\\n        role\\n        skillOrder\\n        spells\\n        stats {\\n          matchCount\\n          __typename\\n        }\\n        type\\n        __typename\\n      }\\n      stats {\\n        tier\\n        winRateHistory {\\n          x\\n          value\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\",\r\n  \"operationName\": \"ChampionQuery\",\r\n  "
                    +
                    "\"variables\": { \"champion\": \"" + champName + "\", \"role\": \"" + lane + "\" }\r\n}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            String json = null;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                json = response.toString();
            }
            return json;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the runes of a champion and the shards
     * <p>
     * The array has a lenght of 9:
     * <li>0-3: left side
     * <li>4-5: right side
     * <li>6-8: shards
     * </p>
     * @param json
     * @return the runes of a champion
     * @throws Exception
     */
    public static String[] getRunes(String json) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject set = (JSONObject) win.get("build");
            JSONObject perks = (JSONObject) set.get("perks");
            JSONArray runes = (JSONArray) perks.get("IDs");
            String[] result = new String[runes.size()];
            for (int i = 0; i < runes.size(); i++) {
                result[i] = String.valueOf(runes.get(i));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the runes root of a champion (as resolve, precision, domination, sorcery, inspiration)
     * <p>
     * The array has a lenght of 2:
     * <li>0: left side
     * <li>1: right side
     * </p>
     * @param json
     * @return the runes root of a champion
     * @throws Exception
     */
    public static String[] getRunesRoot(String json) {
        String[] roots = new String[2];
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject set = (JSONObject) win.get("build");
            JSONObject perks = (JSONObject) set.get("perks");
            roots[0] = perks.get("style").toString();
            roots[1] = perks.get("subStyle").toString();
            return roots;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the summoner spells of a champion
     * @param json
     * @return
     */
    public static String[] getSummonerSpell(String json) {
        String[] spells = new String[2];
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject set = (JSONObject) win.get("build");
            JSONArray spellsJson = (JSONArray) set.get("spells");
            for (int i = 0; i < spellsJson.size(); i++) {
                spells[i] = String.valueOf(spellsJson.get(i));
            }
            return spells;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the skill order of a champion
     * @param json
     * @return
     */
    public static String[] getSkillsOrder(String json) {
        String[] skills = new String[18];
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject set = (JSONObject) win.get("build");
            JSONArray skillsJson = (JSONArray) set.get("skillOrder");
            for (int i = 0; i < skillsJson.size(); i++) {
                skills[i] = String.valueOf(skillsJson.get(i));
            }
            return skills;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the build of a champion
     * @param json
     * @param index the index of the build 0: starter, 1: boots, 
     * , 2: core
     * , 3: full build
     * , 4: situational
     * @return
     */
    public static String[] getBuild(String json, int index) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject set = (JSONObject) win.get("build");
            JSONArray items = (JSONArray) set.get("items");
            JSONObject build = (JSONObject) items.get(index);
            JSONArray starter = (JSONArray) build.get("items");
            String[] result = new String[starter.size()];
            for (int i = 0; i < starter.size(); i++) {
                result[i] = String.valueOf(starter.get(i));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getPatch(String json){
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject stats = (JSONObject) win.get("stats");
            JSONArray items = (JSONArray) stats.get("winRateHistory");
            JSONObject build;
            if(items.size() > 1)
                build = (JSONObject) items.get(1);
            else 
                build = (JSONObject) items.get(0);
            return build.get("x").toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWinRate(String json){
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject stats = (JSONObject) win.get("stats");
            JSONArray items = (JSONArray) stats.get("winRateHistory");
            JSONObject build;
             if(items.size() > 1)
                build = (JSONObject) items.get(1);
            else 
                build = (JSONObject) items.get(0);
            return build.get("value").toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMatchCount(String json){
        JSONParser parser = new JSONParser();
        try {
            JSONObject file = (JSONObject) parser.parse(json);
            JSONObject summary = (JSONObject) file.get("data");
            JSONObject keria = (JSONObject) summary.get("lol");
            JSONObject win = (JSONObject) keria.get("champion");
            JSONObject set = (JSONObject) win.get("build");
            JSONObject stats = (JSONObject) set.get("stats");
            return stats.get("matchCount").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
