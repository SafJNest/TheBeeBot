package com.safjnest.Utilities.Commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Contains the methods to read the JSON file with all the commands descriptions.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * @since 1.3
 */
public class CommandsHandler {
    private String path = "rsc" + File.separator + "commands.json";
    private FileReader reader;
    private JSONParser jsonParser;

    /**
     * default constructor
     */
    public CommandsHandler(){
        jsonParser = new JSONParser();
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a string with a specific arguments of the command.
     * @param nameCommand name of the command
     * @param thing name of the argument
     * @return a string with the argument, otherwise {@code null}
     */
    public String getString(String nameCommand, String thing){
        nameCommand = nameCommand.toLowerCase();
        thing = thing.toLowerCase();
        Object obj;
        try {
            obj = jsonParser.parse(reader);
            JSONObject commands = (JSONObject) obj;
            JSONObject command = (JSONObject) commands.get(nameCommand);
            return (String) command.get(thing);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Gets an array of the arguments of a command.
     * @param nameCommand name of the command
     * @param thing name of the argument 
     * @return an array of strings with the argument, otherwise {@code null}
     */
    public String[] getArray(String nameCommand, String thing){
        nameCommand = nameCommand.toLowerCase();
        thing = thing.toLowerCase();
        Object obj;
        try {
            obj = jsonParser.parse(reader);
            JSONObject commands = (JSONObject) obj;
            JSONObject command = (JSONObject) commands.get(nameCommand);
            JSONArray array = (JSONArray)command.get(thing);
            String[] result = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                result[i] = (String)array.get(i);
            }
            return result;
        } catch (IOException | ParseException e) {
            return null;
        }
    }
    
    /**
     * Gets the cooldown of a command.
     * @param nameCommand name of the command
     * @return a int with the cooldown, otherwise {@code null}
     */
    public int getCooldown(String nameCommand){
        nameCommand = nameCommand.toLowerCase();
        Object obj;
        try {
            obj = jsonParser.parse(reader);
            JSONObject commands = (JSONObject) obj;
            JSONObject command = (JSONObject) commands.get(nameCommand);
            return (command.get("cooldown") == null) ? 0 : Integer.valueOf((String)command.get("cooldown"));
        } catch (IOException | ParseException e) {
            return 0;
        }
    }
}
