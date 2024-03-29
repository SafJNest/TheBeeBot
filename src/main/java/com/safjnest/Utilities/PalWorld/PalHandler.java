package com.safjnest.Utilities.PalWorld;

import java.io.File;

import java.util.HashMap;




public class PalHandler {
    @SuppressWarnings("unused")
    private String path = "rsc" + File.separator + "Testing" + File.separator + "palworld" + File.separator + "pals.json";
    @SuppressWarnings("unused")
    private HashMap<String, Pal> pals = new HashMap<String, Pal>();

    public PalHandler() {
        loadPals();
    }

    private void loadPals() {
        //  try {
        //     FileReader reader = new FileReader(path);
        //     JSONParser parser = new JSONParser();
        //     JSONArray file = (JSONArray) parser.parse(reader);
        //     for (int i = 0; i < file.size(); i++) {
        //         JSONObject obj = (JSONObject) file.get(i);
        //         String name = (String) obj.get("name");
        //         String icon = (String) obj.get("image");
        //         String description = (String) obj.get("description");

        //         JSONArray types = (JSONArray) obj.get("types");
        //         String[] typesArr = new String[types.size()];
        //         for (int j = 0; j < types.size(); j++) {
        //             typesArr[j] = (String) types.get(j);
        //         }

        //         HashMap<String, String> suitability = new HashMap<String, String>();
        //         JSONArray suit = (JSONArray) obj.get("suitability");
        //         for (int j = 0; j < suit.size(); j++) {
        //             JSONObject suitObj = (JSONObject) suit.get(j);
        //             String type = String.valueOf(suitObj.get("type"));
        //             String level = String.valueOf(suitObj.get("level"));
        //             suitability.put(type, level);
        //         }


        //         String aura = (String) obj.get("aura");
        //         String auraDescription = (String) obj.get("auraDescription");
                
        //     }
            
        // } catch (Exception e) {
        // }
    }
    

}
