package com.safjnest.SlashCommands.Audio.Soundboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.Color;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 3.0
 */
public class SoundboardCreateSlash extends SlashCommand{
    private static final int maxSounds = 20;

    public SoundboardCreateSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = new ArrayList<>();
        this.options.add(new OptionData(OptionType.STRING, "name", "Leave blank to not save the soundboard.", false));
        for(int i = 1; i <= maxSounds; i++) {
            this.options.add(new OptionData(OptionType.STRING, "sound-" + i, "Sound " + i, false).setAutoComplete(true));
        }
        //this.options = Arrays.asList(options);
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        Set<String> soundIDs = new HashSet<String>();
        for(OptionMapping option : event.getOptions())
            if(option != null && !option.getName().equals("name"))
                soundIDs.add(option.getAsString());

        if(soundIDs.isEmpty()) {
            event.deferReply(true).addContent("You need to insert at least a sound.").queue();
            return;
        }

        String soundboardName = "temporary";
        if(event.getOption("name") != null) {
            soundboardName = event.getOption("name").getAsString();
            if(DatabaseHandler.soundboardExists(soundboardName, event.getGuild().getId())) {
                event.deferReply(true).addContent("A soundboard with that name in this guild already exists.").queue();
                return;
            }
            DatabaseHandler.insertSoundBoard(soundboardName, event.getGuild().getId(), soundIDs.toArray(new String[0]));
        }

        QueryResult sounds = DatabaseHandler.getSoundsById(soundIDs.toArray(new String[0]));

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getMember().getUser().getName());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setTitle("Soundboard: " + soundboardName);
        eb.setDescription("Press a button to play a sound");
        eb.setColor(Color.decode(Bot.getColor()));

        List<LayoutComponent> rows = new ArrayList<>();
        List<Button> row = new ArrayList<>();
        for (int i = 0; i < sounds.size(); i++) {
            row.add(Button.primary("soundboard-" + sounds.get(i).get("id") + "." + sounds.get(i).get("extension"), sounds.get(i).get("name")));
            if (row.size() == 5 || i == sounds.size() - 1) {
                rows.add(ActionRow.of(row));
                row = new ArrayList<>();
            }
        }

        event.deferReply(false).addEmbeds(eb.build()).setComponents(rows).queue();
    }    
}