package com.safjnest.SlashCommands.League;


import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Commands.League.Opgg;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class OpggSlash extends SlashCommand {
 
    /**
     * Constructor
     */
    public OpggSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "user", "Name of the summoner you want to get information on", false));
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        Button left = Button.primary("match-left", "<-");
        Button right = Button.primary("match-right", "->");
        Button center = Button.primary("match-center", "f");

        boolean searchByUser = false;
        
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        event.deferReply(false).queue();
        if(event.getOption("user") == null){
            s = RiotHandler.getSummonerFromDB(event.getUser().getId());
            if(s == null){
                event.getHook().editOriginal("You dont have a Riot account connected, check /help setUser (or write the name of a summoner).").queue();
                return;
            }
            searchByUser = true;
            center = Button.primary("match-center", s.getName());
            center = center.asDisabled();
        }else{
            s = RiotHandler.getSummonerByName(event.getOption("user").getAsString());
            if(s == null){
                event.getHook().editOriginal("Couldn't find the specified summoner.").queue();
                return;
            }
            
        }
        
        EmbedBuilder builder = Opgg.createEmbed(s, event.getJDA());
        
        if(searchByUser && RiotHandler.getNumberOfProfile(event.getUser().getId()) > 1){
            WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(builder.build());
            action.setComponents(ActionRow.of(left, center, right)).queue();
            return;
        }

        event.getHook().editOriginalEmbeds(builder.build()).queue();
	}



}
