package com.safjnest.Commands.League;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;
import no.stelar7.api.r4j.pojo.shared.RiotAccount;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 1.3
 */
public class Livegame extends Command {
    /**
     * Constructor
     */
    public Livegame() {
        this.name = this.getClass().getSimpleName().toLowerCase();
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

        Button left = Button.primary("rank-left", "<-");
        Button right = Button.primary("rank-right", "->");
        Button center = Button.primary("rank-center", "f");

        boolean searchByUser = false;
        String args = event.getArgs();
        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;

        if (args.equals("")) {
            s = RiotHandler.getSummonerFromDB(event.getAuthor().getId());
            if (s == null) {
                event.reply("You dont have a Riot account connected, check /help setUser (or write the name of a summoner).");
                return;
            }
            searchByUser = true;
            center = Button.primary("center", s.getName());
            center = center.asDisabled();

        } else if (event.getMessage().getMentions().getMembers().size() != 0) {
            s = RiotHandler.getSummonerFromDB(event.getMessage().getMentions().getMembers().get(0).getId());
            if (s == null) {
                event.reply(event.getMessage().getMentions().getMembers().get(0).getEffectiveName()
                        + " doesn't have a Riot account connected.");
                return;
            }
        } else {
            String name = "";
            String tag = "";
            if (!args.contains("#")){
                name = args;
                tag = "EUW";
            }
            else {
                name = args.split("#", 2)[0];
                tag = args.split("#", 2)[1];
            }
            s = RiotHandler.getSummonerByName(name, tag);
            if(s == null){
                event.reply("Couldn't find the specified summoner. Remember to use the tag!");
                return;
            }
        }
        List<SpectatorParticipant> users = null;
        EmbedBuilder builder = null;
        try {
            users = s.getCurrentGame().getParticipants();
            builder = createEmbed(event.getJDA(), event.getAuthor().getId(), s, users);
        
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

            if (searchByUser && RiotHandler.getNumberOfProfile(event.getAuthor().getId()) > 1) {
                MessageCreateAction action = event.getChannel().sendMessageEmbeds(builder.build());
                action.addComponents(ActionRow.of(menu));
                action.addComponents(ActionRow.of(left, center, right));
                action.queue();
                return;
            }
            event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(menu).queue();
                
        } catch (Exception e) {
            builder = createEmbed(event.getJDA(), event.getAuthor().getId(), s, users);
            if (searchByUser && RiotHandler.getNumberOfProfile(event.getAuthor().getId()) > 1) {
                event.getChannel().sendMessageEmbeds(builder.build()).addActionRow(left, center, right).queue();
                return;
            }
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
        }

    }

    public static EmbedBuilder createEmbed(JDA jda, String id, no.stelar7.api.r4j.pojo.lol.summoner.Summoner s, List<SpectatorParticipant> users) {
        try {
            RiotAccount account = RiotHandler.getRiotApi().getAccountAPI().getAccountByPUUID(RegionShard.EUROPE, s.getPUUID());

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(account.getName() + "#" + account.getTag() + "'s Game");
            builder.setColor(Color.decode(Bot.getColor()));
            builder.setThumbnail(RiotHandler.getSummonerProfilePic(s));
            String blueSide = "";
            String redSide = "";
            for (SpectatorParticipant partecipant : users) {
                String sum = RiotHandler.getFormattedEmoji(
                        jda,
                        RiotHandler.getRiotApi().getDDragonAPI().getChampion(partecipant.getChampionId()).getName())
                        + " " + partecipant.getSummonerName();
                String stats = "";
                if (s.getCurrentGame().getGameQueueConfig().commonName().equals("5v5 Ranked Flex Queue")) {

                    stats = RiotHandler.getFlexStats(jda, RiotHandler.getSummonerBySummonerId(partecipant.getSummonerId()));
                    stats = stats.substring(0, stats.lastIndexOf("P") + 1) + " | "
                            + stats.substring(stats.lastIndexOf(":") + 1);

                } else {
                    stats = RiotHandler.getSoloQStats(jda, RiotHandler.getSummonerBySummonerId(partecipant.getSummonerId()));
                    stats = stats.substring(0, stats.lastIndexOf("P") + 1) + " | "
                            + stats.substring(stats.lastIndexOf(":") + 1);
                }
                if (partecipant.getTeam() == TeamType.BLUE)
                    blueSide += "**" + sum + "** " + stats + "\n";
                else
                    redSide += "**" + sum + "** " + stats + "\n";

            }
            if (s.getCurrentGame().getGameQueueConfig().commonName().equals("5v5 Ranked Flex Queue"))
                builder.addField("Ranked stats", "FLEX", false);

            else
                builder.addField("Ranked stats", "SOLOQ", false);

            builder.addField("**BLUE SIDE**", blueSide, false);
            builder.addField("**RED SIDE**", redSide, true);
            return builder;

        } catch (Exception e) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(s.getName() + "'s Game");
            builder.setColor(Color.decode(Bot.getColor()));
            builder.setThumbnail(RiotHandler.getSummonerProfilePic(s));
            builder.setDescription("This user is not in a game.");
            return builder;
        }
    }

}
