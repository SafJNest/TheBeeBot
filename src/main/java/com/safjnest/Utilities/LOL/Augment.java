package com.safjnest.Utilities.LOL;

import java.util.HashMap;

public class Augment {
    private String id;
    private String name;
    private String desc;
    HashMap<String, String> spellDataValues = new HashMap<>();


    public Augment(String id, String name, String desc, HashMap<String, String> spellDataValues){
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.spellDataValues = spellDataValues;

    }

    public String getId() {
        return id;
    }

    public String getFormattedDesc(){
       String result = fixOne(desc);
         result = fixTwo(result);

        
        return result;
    }   

    public String getName() {
        return name;
    }

    private String fixOne(String desc){
        for(int i = 0; i < desc.length(); i++){
            if(desc.charAt(i) == '<'){
                for(int j = i+1; j < desc.length(); j++){
                    if(desc.charAt(j) == '>'){
                        String key = desc.substring(i+1, j);
                        if(key.startsWith("/"))
                            key = key.substring(1);
                        if(desc.charAt(i+1) == '/')
                            desc = desc.replace("</" + key + ">", ""); 
                        else
                            desc = desc.replace("<" + key + ">", "");
                        break;
                    }
                }
            
            }
        }
        return desc;
    }

    private String fixTwo(String desc){
        //"Your Ultimate Ability is sealed. Gain @DamageAmp*100@% <scaleLevel>Damage</scaleLevel>
        for(int i = 0; i < desc.length(); i++){
            if(desc.charAt(i) == '@'){
                String result = "";
                String keya = "";
                String op = "";
                for(int j = i+1; j < desc.length(); j++){
                    if(desc.charAt(j) == '*'){
                        String key = desc.substring(i+1, j);
                        for (int k = j + 1; k < desc.length(); k++) {
                            if (desc.charAt(k) == '@') {
                                keya = desc.substring(i + 1, j);
                                op = desc.substring(j + 1, k);
                                switch (desc.charAt(j)){
                                    case '+':
                                        result = Math.round(Double.valueOf(op) + Double.valueOf(spellDataValues.get(key))) + "";
                                        break;
                                    case '-':
                                        result = Math.round(Double.valueOf(op) - Double.valueOf(spellDataValues.get(key))) + "";
                                        break;
                                    case '*':
                                        result = Math.round(Double.valueOf(op) * Double.valueOf(spellDataValues.get(key))) + "";
                                        break;
                                    case '/':
                                        result = Math.round(Double.valueOf(op) / Double.valueOf(spellDataValues.get(key))) + "";
                                        break;
                                }
                                break;
                            }
                        }
                        
                    }else if(desc.charAt(j) == '@'){
                        if(result.equals("")){
                            String key = desc.substring(i+1, j);
                            String value = spellDataValues.get(key);
                            desc = desc.replace("@" + key + "@", value);
                        }
                        else
                            desc = desc.replace("@" + keya + "*" + op + "@", result);   
                        break;
                    }
                }
            }
        }
        
        return desc;
    }


    


}
