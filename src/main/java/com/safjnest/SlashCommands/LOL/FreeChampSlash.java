package com.safjnest.SlashCommands.LOL;

import java.awt.Color;
import java.io.File;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.FileUpload;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.lol.builders.champion.ChampionBuilder;
import no.stelar7.api.r4j.pojo.lol.champion.ChampionRotationInfo;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class FreeChampSlash extends SlashCommand {
    
    /**
     * Constructor
     */
    public FreeChampSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    /**
     * This method is called every time a member executes the command.
     */
    @Override
	protected void execute(SlashCommandEvent event) {
        String img = "iconLol.png";
        File file = new File("rsc" + File.separator + "img" + File.separator + img);
        event.deferReply()
            .addFiles(FileUpload.fromData(file))
            .queue();
        ChampionBuilder builder = new ChampionBuilder().withPlatform(LeagueShard.EUW1);
        ChampionRotationInfo c = builder.getFreeToPlayRotation();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getMember().getEffectiveName());
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        eb.setTitle("List of free champion:");
        String s = "";
        for(StaticChampion ce : c.getFreeChampions()){
            s+=ce.getName()+" | ";
        }
        eb.setDescription(s);
        eb.setThumbnail("attachment://" + img);
        event.getHook().editOriginalEmbeds(eb.build())
            .queue();
	}

}
