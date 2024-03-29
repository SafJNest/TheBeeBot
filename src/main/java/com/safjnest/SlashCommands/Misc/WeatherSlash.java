package com.safjnest.SlashCommands.Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.awt.Color;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class WeatherSlash extends SlashCommand {
    private String weatherApiKey;

    public WeatherSlash(String weatherApiKey) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "location", "The location of the weather (in english).", true)
        );

        this.weatherApiKey = weatherApiKey;
    }


    @Override
    protected void execute(SlashCommandEvent event) {
        String locationString = event.getOption("location").getAsString();
        
        JSONObject jsonResponse = null;
        try {
            URL url = new URL("https://api.weatherapi.com/v1/current.json?key=" + weatherApiKey + "&q=" + URLEncoder.encode(locationString, "UTF-8"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream;
            if (connection.getResponseCode() == 200)
                inputStream = connection.getInputStream();
            else
                inputStream = connection.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();

            JSONParser JsonParser = new JSONParser();
            jsonResponse = (JSONObject) JsonParser.parse(response.toString());

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        if(jsonResponse.get("error") != null) {
            JSONObject error = (JSONObject) jsonResponse.get("error");
            String errorMessage = "Error " + error.get("code").toString() + ": " + error.get("message").toString();
            event.deferReply(true).addContent(errorMessage).queue();
            return;
        }

        JSONObject location = (JSONObject) jsonResponse.get("location");
        JSONObject currentWeather = (JSONObject) jsonResponse.get("current");
        JSONObject condition = (JSONObject) currentWeather.get("condition");

        String locationName = location.get("name").toString();
        String conditionText = condition.get("text").toString();
        String iconURL = condition.get("icon").toString();
        String temp_c = currentWeather.get("temp_c").toString();
        String wind_kph = currentWeather.get("wind_kph").toString();
        String pressure_mb = currentWeather.get("pressure_mb").toString();
        String precip_mm = currentWeather.get("precip_mm").toString();
        String humidity = currentWeather.get("humidity").toString();
        String feelslike_c = currentWeather.get("feelslike_c").toString();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**Weather in **" + locationName);
        eb.setDescription(conditionText);
        eb.setThumbnail("https:" + iconURL);
        eb.setColor(Color.decode(Bot.getColor()));

        eb.addField("Temperature", temp_c + " C", true);
        eb.addField("Feels like", feelslike_c + " C", true);
        eb.addField("Humidity", humidity + "%", true);
        eb.addField("Wind", wind_kph + " km/h", true);
        eb.addField("Pressure", pressure_mb + " mb", true);
        eb.addField("Precipitations", precip_mm + " mm", true);

        event.deferReply(false).setEmbeds(eb.build()).queue();
    }
}