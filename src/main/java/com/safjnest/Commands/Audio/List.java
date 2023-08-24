package com.safjnest.Commands.Audio;

import java.awt.Color;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
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
public class List extends Command{

    public List(){
        this.name = this.getClass().getSimpleName();;
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        
        Button left = Button.danger("list-left", "<-");
        left = left.asDisabled();
        Button right = Button.primary("list-right", "->");
        Button center = Button.primary("list-center", "Page: 1");
        center = center.withStyle(ButtonStyle.SUCCESS);
        center = center.asDisabled();


        EmbedBuilder eb = new  EmbedBuilder();
        eb.setAuthor(event.getAuthor().getName(), "https://github.com/SafJNest", event.getAuthor().getAvatarUrl());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setTitle("List of " + event.getGuild().getName());
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
        event.getChannel().sendMessageEmbeds(eb.build()).addActionRow(left, center, right).queue();
    }
    
}
