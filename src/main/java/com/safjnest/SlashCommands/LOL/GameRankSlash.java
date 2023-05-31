package com.safjnest.SlashCommands.LOL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Commands.LOL.GameRank;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Commands.CommandsHandler;
import com.safjnest.Utilities.LOL.LOLHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class GameRankSlash extends SlashCommand {
    private R4J r;
    private SQL sql;
    /**
     * Constructor
     */
    public GameRankSlash(R4J r, SQL sql  ){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "user", "Summoner name you want to get data", false));
        this.r = r;
        this.sql = sql;
    }

    /**
     * This method is called every time a member executes the command.
     */
	@Override
	protected void execute(SlashCommandEvent event) {
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        boolean searchByUser = false;
        Button left = Button.primary("rank-left", "<-");
        Button right = Button.primary("rank-right", "->");
        Button center = Button.primary("rank-center", "f");
        event.deferReply(false).queue();


        if(event.getOption("user") == null){
            String query = "SELECT account_id FROM lol_user WHERE discord_id = '" + event.getMember().getId() + "';";
            try {
                s = r.getLoLAPI().getSummonerAPI().getSummonerByAccount(LeagueShard.EUW1, sql.getString(query, "account_id"));
                searchByUser = true;
                center = Button.primary("center", s.getName());
                center = center.asDisabled();

            } catch (Exception e) {
                event.getHook().editOriginal("You dont have connected your Riot account.").queue();
               return;
            }
        }else{
            try {
                s = r.getLoLAPI().getSummonerAPI().getSummonerByName(LeagueShard.EUW1, event.getOption("user").getAsString());
            } catch (Exception e) {
                event.getHook().editOriginal("Didn't found the user you asked for").queue();
                return;
            }
        }


        try {
            List<SpectatorParticipant> users = null;
            try {
                users = s.getCurrentGame().getParticipants();
            } catch (Exception e) { }
            EmbedBuilder builder = GameRank.createEmbed(event.getJDA(), event.getMember().getId(), s, users);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
            
        } catch (Exception e) {
            event.getHook().editOriginal(s.getName() + " is not in a match.").queue();
        }

        
        List<SpectatorParticipant> users = null;
        EmbedBuilder builder = null;
        try {
            users = s.getCurrentGame().getParticipants();
            builder = GameRank.createEmbed(event.getJDA(), event.getMember().getId(), s, users);
        
            ArrayList<SelectOption> options = new ArrayList<>();
            for(SpectatorParticipant p : users){
                Emoji icon = Emoji.fromCustom(
                    LOLHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId()).getName(), 
                    Long.parseLong(LOLHandler.getEmojiId(event.getJDA(), LOLHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId()).getName())), 
                    false);
                if(!p.getSummonerId().equals(s.getSummonerId()))
                    options.add(SelectOption.of(
                                    p.getSummonerName().toUpperCase(), 
                                    p.getSummonerId()).withEmoji(icon));
            }

            StringSelectMenu menu = StringSelectMenu.create("rank-select")
                .setPlaceholder("Select a summoner")
                .setMaxValues(1)
                .addOptions(options)
                .build();

            if (searchByUser && LOLHandler.getNumberOfProfile(event.getMember().getId()) > 1) {
                WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                        s, users).build());
                        action.setComponents(ActionRow.of(menu),
                                            ActionRow.of(left, center, right)).queue();
                return;
            }
            WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                                s, users).build());
            action.setComponents(ActionRow.of(menu)).queue();
                
        } catch (Exception e) {

            builder = GameRank.createEmbed(event.getJDA(), event.getMember().getId(), s, users);
            if (searchByUser && LOLHandler.getNumberOfProfile(event.getMember().getId()) > 1) {
                WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                        s, users).build());
                        action.setComponents(ActionRow.of(left, center, right))
                        .queue();
                return;
            }
            event.getHook().editOriginalEmbeds(builder.build()).queue();
        }
	}

}
