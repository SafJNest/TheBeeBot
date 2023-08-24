package com.safjnest.SlashCommands.Audio.List;

import java.awt.Color;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1
 */
public class ListGuildSlash extends SlashCommand{

    public ListGuildSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

	@Override
	public void execute(SlashCommandEvent event) {
        Button left = Button.danger("list-left", "<-");
        left = left.asDisabled();
        Button right = Button.primary("list-right", "->");
        Button center = Button.primary("list-center", "Page: 1");
        center = center.withStyle(ButtonStyle.SUCCESS);
        center = center.asDisabled();


        EmbedBuilder eb = new  EmbedBuilder();
        eb.setAuthor(event.getUser().getName(), "https://github.com/SafJNest", event.getUser().getAvatarUrl());
        eb.setTitle("List of " + event.getGuild().getName());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE guild_id = '" + event.getGuild().getId() + "' ORDER BY name ASC;";
        ArrayList<ArrayList<String>> sounds = DatabaseHandler.getSql().getAllRows(query, 2);
        eb.setDescription("Total Sound: " + sounds.size());
        int cont = 0;
        while(cont <24 && cont < sounds.size()){
            eb.addField("**"+sounds.get(cont).get(1)+"**", "ID: " + sounds.get(cont).get(0), true);
            cont++;
        }
        
        if(sounds.size() <= 24){
            right = right.withStyle(ButtonStyle.DANGER);
            right = right.asDisabled();
        }
        event.deferReply(false).addEmbeds(eb.build()).addActionRow(left, center, right).queue();
    }

    

    

    
   
}