package com.safjnest.SlashCommands.LOL;

import java.util.Arrays;
    
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Commands.LOL.Summoner;
import com.safjnest.Utilities.CommandsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class SummonerSlash extends SlashCommand {
 
    /**
     * Constructor
     */
    public SummonerSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(new OptionData(OptionType.STRING, "user", "Summoner name you want to get data", false));
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        Button left = Button.primary("left", "<-");
        Button right = Button.primary("right", "->");
        Button center = Button.primary("center", "f");

        boolean searchByUser = false;
        
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        event.deferReply(true).queue();
        if(event.getOption("user") == null){
            s = LOLHandler.getSummonerFromDB(event.getUser().getId());
            if(s == null){
                event.reply("You dont have connected a Riot account, for more information /help setUser");
                return;
            }
            searchByUser = true;
            center = Button.primary("center", s.getName());
            center.asDisabled();
        }else{
            s = LOLHandler.getSummonerByName(event.getOption("user").getAsString());
            if(s == null){
                event.reply("Didn't find this user. ");
                return;
            }
            
        }
        
        EmbedBuilder builder = Summoner.createEmbed(event.getJDA().getSelfUser().getId(), s);
        
        if(searchByUser && LOLHandler.getNumberOfProfile(event.getUser().getId()) > 1){
            event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(left, center, right).queue();
            return;
        }

        event.deferReply(false).addEmbeds(builder.build()).queue();
        

	}

}
