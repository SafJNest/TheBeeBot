package com.safjnest.Commands.Audio;

import java.awt.Color;
import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;


/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 2.1
 */
public class ListUser extends Command{

    public ListUser(){
        this.name = this.getClass().getSimpleName();;
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        

        User theGuy;
        if(event.getMessage().getMentions().getMembers().size() > 0)
            theGuy = event.getMessage().getMentions().getMembers().get(0).getUser();
        else{
            try {
                theGuy = event.getJDA().retrieveUserById(event.getArgs()).complete();
            } catch (Exception e) {
                theGuy = event.getAuthor();
            }
        }

        Button left = Button.danger("listuser-left", "<-");
        left = left.asDisabled();
        Button right = Button.primary("listuser-right", "->");
        Button center = Button.primary("listuser-center-" + theGuy.getId(), "Page: 1");
        center = center.withStyle(ButtonStyle.SUCCESS);
        center = center.asDisabled();


        EmbedBuilder eb = new  EmbedBuilder();
        eb.setAuthor(theGuy.getName(), "https://github.com/SafJNest", theGuy.getAvatarUrl());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setTitle("List of " + theGuy.getName());
        eb.setColor(Color.decode(
        BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE user_id = '" + theGuy.getId() + "' ORDER BY name ASC;";
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
