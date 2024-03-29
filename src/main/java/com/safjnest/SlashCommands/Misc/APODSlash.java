package com.safjnest.SlashCommands.Misc;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class APODSlash extends SlashCommand {
    private String nasaApiKey;

    public APODSlash(String nasaApiKey) {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "date", "The date of the APOD (YYYY-MM-DD).", false)
        );

        this.nasaApiKey = nasaApiKey;
    }


    @Override
    protected void execute(SlashCommandEvent event) {
        JSONObject jsonResponse = null;
        int responseCode = 0;
        try {
            String urlString = "https://api.nasa.gov/planetary/apod?api_key=" + nasaApiKey;
            if(event.getOption("date") != null)
                urlString += "&date=" + URLEncoder.encode(event.getOption("date").getAsString(), "UTF-8");

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream;
            if ((responseCode = connection.getResponseCode()) == 200)
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

        if(responseCode != 200) {
            String errorMessage = "Error " + jsonResponse.get("code").toString() + ": " + jsonResponse.get("msg").toString();
            event.deferReply(true).addContent(errorMessage).queue();
            return;
        }

        String type = jsonResponse.get("media_type").toString();
        if(!type.equals("image") && !type.equals("video")) {
            event.deferReply(true).addContent("Something went wrong.").queue();
            return;
        }

        String title = jsonResponse.get("title").toString();
        String explanation = jsonResponse.get("explanation").toString();
        String date = jsonResponse.get("date").toString();
        
        String apodUrl = "https://apod.nasa.gov/apod/ap" + date.replace("-", "").substring(2) + ".html";
        
        EmbedBuilder eb = new EmbedBuilder();

        eb.setAuthor("Astronomy Picture of the Day");
        eb.setTitle(title, apodUrl);
        eb.setDescription(explanation);
        eb.setColor(Color.decode(Bot.getColor()));

        if(type.equals("image")) {
            eb.setImage(jsonResponse.get("hdurl").toString());
        }
        else if(type.equals("video")) {
            eb.appendDescription("\n\n**The apod is a video, click the link in the title to watch it**");
            Pattern pattern = Pattern.compile("(?:https://)?(?:www\\.)?youtube\\.com/embed/([A-Za-z0-9_-]+)\\?");
            Matcher matcher = pattern.matcher(jsonResponse.get("url").toString());
            if(matcher.find()) {
                eb.setImage("https://img.youtube.com/vi/" + matcher.group(1) + "/hqdefault.jpg");
            }
        }
        
        eb.setFooter("Date: " + date);

        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}