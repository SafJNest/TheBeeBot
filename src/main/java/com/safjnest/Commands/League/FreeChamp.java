package com.safjnest.Commands.League;

import java.awt.Color;
import java.io.File;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.RiotHandler;

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
public class FreeChamp extends Command {
    
    /**
     * Constructor
     */
    public FreeChamp(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(CommandEvent event) {
        ChampionBuilder builder = new ChampionBuilder().withPlatform(LeagueShard.EUW1);
        ChampionRotationInfo c = builder.getFreeToPlayRotation();
            
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getAuthor().getName());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        eb.setTitle("Current free champion rotation:");

        String s = "";
        int cont = 1;
        for(StaticChampion ce : c.getFreeChampions()){
            s += RiotHandler.getFormattedEmoji(event.getJDA(), ce.getName()) + " **" + ce.getName()+"**\n";
            if(cont % 10 == 0){
                eb.addField("", s, true);
                cont = 0;
                s = "";
            }
            cont++;
        }
        String img = "iconLol.png";
        File file = new File("rsc" + File.separator + "img" + File.separator + img);
        eb.setThumbnail("attachment://" + img);
        event.getChannel().sendMessageEmbeds(eb.build())
            .addFiles(FileUpload.fromData(file))
            .queue();
	}

}
