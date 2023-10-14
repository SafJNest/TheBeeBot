package com.safjnest.SlashCommands.Audio.Soundboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 2.1
 */
public class SoundboardSelectSlash extends SlashCommand{

    public SoundboardSelectSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "name", "Soundboard to play", true)
                .setAutoComplete(true)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        String soundboardID = event.getOption("name").getAsString();

        QueryResult sounds = DatabaseHandler.getSoundsFromSoundBoard(soundboardID);

        String soundboardName = DatabaseHandler.getSoundboardByID(soundboardID).get("name");

        EmbedBuilder eb = new  EmbedBuilder();
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setTitle("Soundboard: " + soundboardName);
        eb.setDescription("Press a button to play a sound");
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        
        List<LayoutComponent> rows = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        for (int i = 0; i < sounds.size(); i++) {
            row.add(Button.primary("soundboard-" + sounds.get(i).get("sound_id") + "." + sounds.get(i).get("extension"), sounds.get(i).get("name")));
            if (row.size() == 5 || i == sounds.size() - 1) {
                rows.add(ActionRow.of(row));
                row = new ArrayList<>();
            }
        }

        event.deferReply(false).addEmbeds(eb.build()).setComponents(rows).queue();
    }
}