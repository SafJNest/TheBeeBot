package com.safjnest.SlashCommands.League;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Commands.League.Livegame;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class LivegameSlash extends SlashCommand {


    public LivegameSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "summoner", "Name of the summoner you want to get information on", false),
            new OptionData(OptionType.STRING, "tag", "Tag of the summoner you want to get information on", false),
            new OptionData(OptionType.USER, "user", "Discord user you want to get information on (if riot account is connected)", false)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;
        boolean searchByUser = false;
        Button left = Button.primary("rank-left", "<-");
        Button right = Button.primary("rank-right", "->");
        Button center = Button.primary("rank-center", "f");
        event.deferReply(false).queue();

        User theGuy = null;
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

        if(searchByUser && RiotHandler.getNumberOfProfile(theGuy.getId()) > 1){
            searchByUser = true;
            center = Button.primary("lol-center", s.getName());
            center = center.asDisabled();
        }

        try {
            List<SpectatorParticipant> users = null;
            try {
                users = s.getCurrentGame().getParticipants();
            } catch (Exception e) { }
            EmbedBuilder builder = Livegame.createEmbed(event.getJDA(), event.getMember().getId(), s, users);
            event.getHook().editOriginalEmbeds(builder.build()).queue();
            
        } catch (Exception e) {
            event.getHook().editOriginal(s.getName() + " is not in a match.").queue();
        }

        List<SpectatorParticipant> users = null;
        EmbedBuilder builder = null;
        try {
            users = s.getCurrentGame().getParticipants();
            builder = Livegame.createEmbed(event.getJDA(), event.getMember().getId(), s, users);
        
            ArrayList<SelectOption> options = new ArrayList<>();
            for(SpectatorParticipant p : users){
                Emoji icon = Emoji.fromCustom(
                    RiotHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId()).getName(), 
                    Long.parseLong(RiotHandler.getEmojiId(event.getJDA(), RiotHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId()).getName())), 
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

            if (searchByUser && RiotHandler.getNumberOfProfile(event.getMember().getId()) > 1) {
                WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                        s, users).build());
                        action.setComponents(ActionRow.of(menu),
                                            ActionRow.of(left, center, right)).queue();
                return;
            }
            WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                                s, users).build());
            action.setComponents(ActionRow.of(menu)).queue();
                
        } catch (Exception e) {
            builder = Livegame.createEmbed(event.getJDA(), event.getMember().getId(), s, users);
            if (searchByUser && RiotHandler.getNumberOfProfile(event.getMember().getId()) > 1) {
                WebhookMessageEditAction<Message> action = event.getHook().editOriginalEmbeds(Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                        s, users).build());
                        action.setComponents(ActionRow.of(left, center, right))
                        .queue();
                return;
            }
            event.getHook().editOriginalEmbeds(builder.build()).queue();
        }
	}
}
