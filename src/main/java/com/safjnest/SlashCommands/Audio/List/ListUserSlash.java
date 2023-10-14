package com.safjnest.SlashCommands.Audio.List;

import java.awt.Color;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 2.1
 */
public class ListUserSlash extends SlashCommand{

    public ListUserSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to get the list of", true)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        User mentionedUser = event.getOption("user").getAsUser();

        Button left = Button.danger("listuser-left", "<-");
        left = left.asDisabled();
        Button right = Button.primary("listuser-right", "->");
        Button center = Button.primary("listuser-center-" + mentionedUser.getId(), "Page: 1");
        center = center.withStyle(ButtonStyle.SUCCESS);
        center = center.asDisabled();

        EmbedBuilder eb = new  EmbedBuilder();
        eb.setAuthor(mentionedUser.getName(), "https://github.com/SafJNest", mentionedUser.getAvatarUrl());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setTitle("List of " + mentionedUser.getName());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));

        QueryResult sounds = (mentionedUser.getId().equals(event.getMember().getId())) 
                           ? DatabaseHandler.getlistUserSounds(mentionedUser.getId()) 
                           : DatabaseHandler.getlistUserSounds(mentionedUser.getId(), event.getGuild().getId());

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

        event.deferReply(false).addEmbeds(eb.build()).addActionRow(left, center, right).queue();
    }    
}