package com.safjnest.Commands.Audio;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class List extends Command {

    public List(){
        this.name = this.getClass().getSimpleName().toLowerCase();
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
        eb.setColor(Color.decode(Bot.getColor()));

        QueryResult sounds = DatabaseHandler.getlistGuildSounds(event.getGuild().getId());

        eb.setDescription("Total Sound: " + sounds.size());
        

        for(int i = 0; i < sounds.size() && i < 24; i++){
            ResultRow sound = sounds.get(i);
            String locket = sound.getAsBoolean("public") ? "" : ":lock:";
            eb.addField("**" + sound.get("name") + "**" + locket, "ID: " + sound.get("id"), true);
        }
         
        if(sounds.size() <= 24){
            right = right.withStyle(ButtonStyle.DANGER);
            right = right.asDisabled();
        }
        
        event.getChannel().sendMessageEmbeds(eb.build()).addActionRow(left, center, right).queue();
    }
}