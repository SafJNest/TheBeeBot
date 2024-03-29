package com.safjnest.SlashCommands.League;

import java.util.Arrays;
    
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Commands.League.Summoner;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SummonerInfoSlash extends SlashCommand {
 
    public SummonerInfoSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "summoner", "Name of the summoner you want to get information on", false),
            new OptionData(OptionType.USER, "user", "Discord user you want to get information on (if riot account is connected)", false),
            new OptionData(OptionType.STRING, "tag", "Tag of the summoner you want to get information on", false));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        Button left = Button.primary("lol-left", "<-");
        Button right = Button.primary("lol-right", "->");
        Button center = Button.primary("lol-center", "f");

        boolean searchByUser = false;
        
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;

        User theGuy = null;
        event.deferReply(false).queue();
        if(event.getOption("summoner") == null && event.getOption("user") == null){
            searchByUser = true;
            s = RiotHandler.getSummonerFromDB(event.getUser().getId());
            theGuy = event.getUser();
            if(s == null){
                event.getHook().editOriginal("You dont have a Riot account connected, check /help setUser (or write the name of a summoner).").queue();
                return;
            }
        }else if(event.getOption("user") != null){
            theGuy = event.getOption("user").getAsUser();
            s = RiotHandler.getSummonerFromDB(theGuy.getId());
            searchByUser = true;
            if(s == null){
                 event.getHook().editOriginal(theGuy.getEffectiveName() + " doesn't have a Riot account connected.").queue();
                return;
            }
        }else{
            String name = event.getOption("summoner").getAsString();
            String tag = (event.getOption("tag") != null) ? event.getOption("tag").getAsString() : "";
            s = RiotHandler.getSummonerByName(name, tag);
            if(s == null){
                event.reply("Couldn't find the specified summoner.");
                return;
            }
            
        }
        
        EmbedBuilder builder = Summoner.createEmbed(event.getJDA(),event.getJDA().getSelfUser().getId(), s);
        
        if(searchByUser && RiotHandler.getNumberOfProfile(theGuy.getId()) > 1){
            searchByUser = true;
            center = Button.primary("lol-center", s.getName());
            center = center.asDisabled();

            WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),s).build());
            action.setComponents(ActionRow.of(left, center, right)).queue();
            return;
        }

        event.getHook().editOriginalEmbeds(builder.build()).queue();
        

	}

}
