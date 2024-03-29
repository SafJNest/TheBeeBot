package com.safjnest.Utilities.EventHandlers;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.safjnest.Bot;
import com.safjnest.Commands.League.Livegame;
import com.safjnest.Commands.League.Opgg;
import com.safjnest.Commands.League.Summoner;
import com.safjnest.Commands.Queue.Queue;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Audio.TrackScheduler;
import com.safjnest.Utilities.Guild.Alert.RewardData;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

public class EventButtonHandler extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();

        event.deferEdit().queue();

        if (buttonId.startsWith("lol-")) 
            lolButtonEvent(event);
        
        else if (buttonId.startsWith("match-")) 
            matchButtonEvent(event);

        else if (buttonId.startsWith("rank-")) 
            rankButtonEvent(event);

        else if (buttonId.startsWith("list-")) 
            listButtonEvent(event);

        else if (buttonId.startsWith("listuser-")) 
            listUserButtonEvent(event);

        else if(buttonId.startsWith("ban-"))
            banUserEvent(event);

        else if(buttonId.startsWith("kick-"))
            kickUserEvent(event);

        else if(buttonId.startsWith("ignore-"))
            ignoreUserEvent(event);

        else if(buttonId.startsWith("unban-"))
            pardonUserEvent(event);

        else if(buttonId.startsWith("soundboard-"))
            soundboardEvent(event);

        else if(buttonId.startsWith("queue-"))
            queue(event);
        
        else if (buttonId.startsWith("reward-"))
            reward(event);
    }


    public void reward(ButtonInteractionEvent event) {
        String args = event.getButton().getId().split("-")[1];

        Guild guild = event.getGuild();

        Button left = Button.primary("reward-left", "<-");
        Button right = Button.primary("reward-right", "->");
        Button center = null;

        String level = "";
        for (Button b : event.getMessage().getButtons()) {
            if (b.getLabel().startsWith("Level")) {
                level = b.getLabel().substring(b.getLabel().indexOf(":") + 2);
            }
        }
        
        switch (args) {
            case "right":
                RewardData nextReward = Bot.getGuildData(guild.getId()).getHigherReward(Integer.parseInt(level));
                RewardData nextNextReward = Bot.getGuildData(guild.getId()).getHigherReward(nextReward.getLevel());
                
                if (nextNextReward == null) {
                    right = right.asDisabled();
                    right = right.withStyle(ButtonStyle.DANGER);
                }

                center = Button.primary("center", "Level: " + nextReward.getLevel());
                center = center.withStyle(ButtonStyle.SUCCESS);
                center = center.asDisabled();
                event.getMessage().editMessageEmbeds(nextReward.getSampleEmbed(guild).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":
                
                RewardData previousRewardData = Bot.getGuildData(guild.getId()).getLowerReward(Integer.parseInt(level));
                RewardData previousPreviousRewardData = Bot.getGuildData(guild.getId()).getLowerReward(previousRewardData.getLevel());
                if (previousPreviousRewardData == null) {
                    left = left.asDisabled();
                    left = left.withStyle(ButtonStyle.DANGER);
                }

                center = Button.primary("center", "Level: " + previousRewardData.getLevel());
                center = center.withStyle(ButtonStyle.SUCCESS);
                center = center.asDisabled();
                event.getMessage().editMessageEmbeds(previousRewardData.getSampleEmbed(guild).build())
                        .setActionRow(left, center, right)
                        .queue();


                break;
        }
    }

    public void queue(ButtonInteractionEvent event) {
        String args = event.getButton().getId().split("-")[1];

        Guild guild = event.getGuild();
        User self = event.getJDA().getSelfUser();

        PlayerManager pm = PlayerManager.get();
        TrackScheduler ts = pm.getGuildMusicManager(guild, self).getTrackScheduler();


        int previousIndex = ts.getIndex() - 11;
        if(previousIndex < 0)
            previousIndex = 0;

        int nextIndex = ts.getIndex() + 11;
        if(nextIndex > ts.getQueue().size())
            nextIndex = ts.getQueue().size() - 1;

        // for (Button b : event.getMessage().getButtons()) {
        //     if(b.getId().split("-")[1].equalsIgnoreCase("previouspage"))
        //         previousIndex = Integer.parseInt(b.getId().split("-", 3)[2]);

        //     if(b.getId().split("-")[1].equalsIgnoreCase("nextpage"))
        //         nextIndex = Integer.parseInt(b.getId().split("-", 3)[2]);
            
        // }

        Button repeat = Button.secondary("queue-repeat", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "repeat"));  
        Button previous = Button.primary("queue-previous", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "previous"));
        Button play = Button.primary("queue-pause", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "pause"));
        Button next = Button.primary("queue-next", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "next"));
        Button shurima = Button.secondary("queue-shurima", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "shuffle"));

        

        java.util.List<LayoutComponent> rows = new ArrayList<>();
        int startIndex = ts.getIndex(); 

        switch (args) {
            case "repeat":
                ts.setRepeat(!ts.isRepeat());
            
                break;
            case "previouspage":
                startIndex = Integer.parseInt(event.getButton().getId().split("-", 3)[2]);
                if(startIndex < 0)
                    startIndex = 0;
                
                previousIndex = (startIndex == ts.getIndex() ? 0 : startIndex - 11);
                nextIndex = startIndex + 11;

                break;
            case "previous":
                if(event.getButton().getStyle() == ButtonStyle.DANGER)
                    break;

                ts.playForcePrev();
                startIndex = ts.getIndex();
                break;
            case "pause":

                ts.pause(true);
                break;
            case "play":

                ts.pause(false);
                break;
            case "next":
                if(event.getButton().getStyle() == ButtonStyle.DANGER)
                    break;

                ts.playForceNext();
                startIndex = ts.getIndex();
                break;
            case "nextpage":
                startIndex = Integer.parseInt(event.getButton().getId().split("-")[2]);

                nextIndex = startIndex + 11;
                previousIndex = startIndex - 11;

                break;
            case "shurima":
                if(!ts.isShuffled()) 
                    ts.shuffleQueue();
                else
                    ts.unshuffleQueue();
                    
                startIndex = ts.getIndex();
                previousIndex = startIndex - 11;
                if(previousIndex < 0)
                    previousIndex = 0;

                nextIndex = startIndex + 11;
                if(nextIndex > ts.getQueue().size())
                    nextIndex = ts.getQueue().size() - 1;
                
                break;
            case "clear":
                ts.clearQueue();
                break;
            default:
            
                break;
        }

        LinkedList<AudioTrack> queue = ts.getQueue(); 
        
        if(ts.isRepeat()) {
            repeat = repeat.withStyle(ButtonStyle.DANGER);
        }
            
        
        if(ts.isShuffled()) {
            shurima = shurima.withStyle(ButtonStyle.DANGER);
        }

        if(ts.getIndex() == 0) {
            previous = previous.withStyle(ButtonStyle.DANGER);
            previous = previous.asDisabled();
        }

        if(ts.getIndex() == ts.getQueue().size() - 1) {
            next = next.withStyle(ButtonStyle.DANGER);
            next = next.asDisabled();
        }

        if(!ts.isPaused()) {
            play = Button.primary("queue-pause", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "pause"));
        } else {
            play = Button.primary("queue-play", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "play"));
        }
        
        rows.add(ActionRow.of(
            repeat,
            previous,
            play,
            next,
            shurima
        ));


        Button previousPage = Button.secondary("queue-previouspage-" + previousIndex, " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "leftarrow"));
        Button nextPage = Button.secondary("queue-nextpage-" + nextIndex, " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "rightarrow"));

        if(startIndex > ts.getQueue().size()) {
            startIndex = ts.getQueue().size() - 1;
            nextPage = nextPage.asDisabled();
        }
        
        if(startIndex < ts.getIndex() && (startIndex + 11) > ts.getIndex()) { 
            nextIndex = ts.getIndex();
        }
        else {
            nextIndex = startIndex + 11;
        }

        if(previousIndex < 0) {
            previousPage = previousPage.asDisabled();
        }

        nextPage = nextPage.withId("queue-nextpage-" + nextIndex);
        previousPage = previousPage.withId("queue-previouspage-" + previousIndex);

        rows.add(ActionRow.of(
            Button.secondary("queue-blank", " ").asDisabled().withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "blank")),
            previousPage,
            Button.secondary("queue-blank1", " ").asDisabled().withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "blank")),
            nextPage,
            Button.secondary("queue-clear", " ").withEmoji(RiotHandler.getRichEmoji(event.getJDA(), "bin"))
        ));

        event.getMessage()
                .editMessageEmbeds(Queue.getEmbed(event.getJDA(), guild, queue, startIndex).build())
                .setComponents(rows)
                .queue();
    }

    public void lolButtonEvent(ButtonInteractionEvent event) {
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        Button left = Button.primary("lol-left", "<-");
        Button right = Button.primary("lol-right", "->");
        Button center = null;
        String nameSum = "";
        int index = 0;

        for (Button b : event.getMessage().getButtons()) {
            if (!b.getLabel().equals("->") && !b.getLabel().equals("<-"))
                nameSum = b.getLabel();
        }

        String user_id = DatabaseHandler.getUserIdByLOLAccountId(RiotHandler.getAccountIdByName(nameSum));
        QueryResult accounts = DatabaseHandler.getLolAccounts(user_id);
        switch (args) {

            case "right":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get("summoner_id")).getName().equals(nameSum))
                        index = i;
                }

                if ((index + 1) == accounts.size())
                    index = 0;
                else
                    index += 1;

                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")).getName());
                center = center.asDisabled();
                
                event.getMessage()
                        .editMessageEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id"))).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get("summoner_id")).getName().equals(nameSum))
                        index = i;

                }

                if (index == 0)
                    index = accounts.size() - 1;
                else
                    index -= 1;

                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")).getName());
                center = center.asDisabled();
                
                event.getMessage()
                        .editMessageEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id"))).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;
        }
    }

    public void matchButtonEvent(ButtonInteractionEvent event) {
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        Button left = Button.primary("match-left", "<-");
        Button right = Button.primary("match-right", "->");
        Button center = null;
        String nameSum = "";
        int index = 0;

        for (Button b : event.getMessage().getButtons()) {
            if (!b.getLabel().equals("->") && !b.getLabel().equals("<-"))
                nameSum = b.getLabel();
        }
        String user_id = DatabaseHandler.getUserIdByLOLAccountId(RiotHandler.getAccountIdByName(nameSum));
        QueryResult accounts = DatabaseHandler.getLolAccounts(user_id);
        switch (args) {

            case "right":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get("summoner_id")).getName().equals(nameSum))
                        index = i;
                }

                if ((index + 1) == accounts.size())
                    index = 0;
                else
                    index += 1;

                center = Button.primary("match-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")).getName());
                center = center.asDisabled();
                
                event.getMessage()
                        .editMessageEmbeds(Opgg.createEmbed(
                            RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")), event.getJDA()).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get("summoner_id")).getName().equals(nameSum))
                        index = i;

                }

                if (index == 0)
                    index = accounts.size() - 1;
                else
                    index -= 1;

                center = Button.primary("match-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")).getName());
                center = center.asDisabled();
                
               event.getMessage()
                        .editMessageEmbeds(Opgg.createEmbed(
                            RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")), event.getJDA()).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;
        }
    }

    public void rankButtonEvent(ButtonInteractionEvent event) {
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        Button left = Button.primary("rank-left", "<-");
        Button right = Button.primary("rank-right", "->");
        Button center = null;
        String nameSum = "";
        int index = 0;

        for (Button b : event.getMessage().getButtons()) {
            if (!b.getLabel().equals("->") && !b.getLabel().equals("<-"))
                nameSum = b.getLabel();
        }

        String user_id = DatabaseHandler.getUserIdByLOLAccountId(RiotHandler.getAccountIdByName(nameSum));
        QueryResult accounts = DatabaseHandler.getLolAccounts(user_id);
        List<SpectatorParticipant> users = null;

        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;

        switch (args) {

            case "right":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get("summoner_id")).getName().equals(nameSum))
                        index = i;
                }

                if ((index + 1) == accounts.size())
                    index = 0;
                else

                    index += 1;

                s = RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id"));
                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")).getName());
                center = center.asDisabled();

                try {
                    users = s.getCurrentGame().getParticipants();

                    ArrayList<SelectOption> options = new ArrayList<>();
                    for (SpectatorParticipant p : users) {
                        Emoji icon = Emoji.fromCustom(
                                RiotHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId()).getName(),
                                Long.parseLong(RiotHandler.getEmojiId(event.getJDA(),
                                        RiotHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId())
                                                .getName())),
                                false);
                        if (!p.getSummonerId().equals(s.getSummonerId()))
                            options.add(SelectOption.of(
                                    p.getSummonerName().toUpperCase(),
                                    p.getSummonerId()).withEmoji(icon));
                    }

                    StringSelectMenu menu = StringSelectMenu.create("rank-select")
                            .setPlaceholder("Select a summoner")
                            .setMaxValues(1)
                            .addOptions(options)
                            .build();

                    MessageEditAction action = event.getMessage().editMessageEmbeds(
                            Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                    s, users).build());
                    action.setComponents(ActionRow.of(menu),
                            ActionRow.of(left, center, right));
                    action.queue();
                } catch (Exception e) {
                    event.getMessage()
                            .editMessageEmbeds(
                                    Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                            s, users).build())
                            .setActionRow(left, center, right)
                            .queue();
                }
                break;

            case "left":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get("summoner_id")).getName().equals(nameSum))
                        index = i;
                }

                if (index == 0)
                    index = accounts.size() - 1;
                else
                    index -= 1;

                s = RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id"));
                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get("summoner_id")).getName());
                center = center.asDisabled();
                
                try {
                    users = s.getCurrentGame().getParticipants();

                    ArrayList<SelectOption> options = new ArrayList<>();
                    for (SpectatorParticipant p : users) {
                        Emoji icon = Emoji.fromCustom(
                                RiotHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId()).getName(),
                                Long.parseLong(RiotHandler.getEmojiId(event.getJDA(),
                                        RiotHandler.getRiotApi().getDDragonAPI().getChampion(p.getChampionId())
                                                .getName())),
                                false);
                        if (!p.getSummonerId().equals(s.getSummonerId()))
                            options.add(SelectOption.of(
                                    p.getSummonerName().toUpperCase(),
                                    p.getSummonerId()).withEmoji(icon));
                    }

                    StringSelectMenu menu = StringSelectMenu.create("rank-select")
                            .setPlaceholder("Select a summoner")
                            .setMaxValues(1)
                            .addOptions(options)
                            .build();

                    MessageEditAction action = event.getMessage().editMessageEmbeds(
                            Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                    s, users).build());
                    action.setComponents(ActionRow.of(menu),
                            ActionRow.of(left, center, right));
                    action.queue();
                } catch (Exception e) {
                    event.getMessage()
                            .editMessageEmbeds(
                                    Livegame.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                            s, users).build())
                            .setActionRow(left, center, right)
                            .queue();
                }
                break;
        }
    }

    public void listButtonEvent(ButtonInteractionEvent event) {
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);

        int page = 1;
        int cont = 0;

        Button left = Button.primary("list-left", "<-");
        Button right = Button.primary("list-right", "->");
        Button center = null;

        QueryResult sounds = DatabaseHandler.getlistGuildSounds(event.getGuild().getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName(), "https://github.com/SafJNest",
                event.getUser().getAvatarUrl());
        eb.setTitle("List of " + event.getGuild().getName());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));
        eb.setDescription("Total Sound: " + sounds.size());
        
        
        switch (args) {

            case "right":
                for (Button b : event.getMessage().getButtons()) {
                    if (b.getLabel().startsWith("Page"))
                        page = Integer.valueOf(String.valueOf(b.getLabel().charAt(b.getLabel().indexOf(":") + 2)));
                }

                cont = 24 * page;
                while (cont < (24 * (page + 1)) && cont < sounds.size()) {
                    String locket = (!sounds.get(cont).getAsBoolean("public")) ? ":lock:" : "";
                    eb.addField("**"+sounds.get(cont).get("name")+"**" + locket, "ID: " + sounds.get(cont).get("id"), true);
                    cont++;
                }

                if (24 * (page + 1) >= sounds.size()) {
                    right = right.asDisabled();
                    right = right.withStyle(ButtonStyle.DANGER);
                }
                center = Button.primary("center", "Page: " + (page + 1));
                center = center.withStyle(ButtonStyle.SUCCESS);
                center = center.asDisabled();
                event.getMessage().editMessageEmbeds(eb.build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":

                for (Button b : event.getMessage().getButtons()) {
                    if (b.getLabel().startsWith("Page"))
                        page = Integer.valueOf(String.valueOf(b.getLabel().charAt(b.getLabel().indexOf(":") + 2)));
                }
                cont = (24 * (page - 2) < 0) ? 0 : 24 * (page - 2);

                while (cont < (24 * (page - 1)) && cont < sounds.size()) {
                    String locket = (!sounds.get(cont).getAsBoolean("public")) ? ":lock:" : "";
                    eb.addField("**"+sounds.get(cont).get("name")+"**" + locket, "ID: " + sounds.get(cont).get("id"), true);
                    cont++;
                }

                if ((page - 1) == 1) {
                    left = left.asDisabled();
                    left = left.withStyle(ButtonStyle.DANGER);
                }

                center = Button.primary("center", "Page: " + (page - 1));
                center = center.withStyle(ButtonStyle.SUCCESS);
                center = center.asDisabled();
                event.getMessage().editMessageEmbeds(eb.build())
                        .setActionRow(left, center, right)
                        .queue();
                break;
        }
    }

    public void listUserButtonEvent(ButtonInteractionEvent event) {
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);

        int page = 1;
        int cont = 0;
        String userId = "";

        Button left = Button.primary("listuser-left", "<-");
        Button right = Button.primary("listuser-right", "->");
        Button center = null;

        for (Button b : event.getMessage().getButtons()) {
            if (b.getLabel().startsWith("Page")) {
                page = Integer.valueOf(String.valueOf(b.getLabel().charAt(b.getLabel().indexOf(":") + 2)));
                userId = b.getId().split("-")[2];
            }
        }

        QueryResult sounds = (userId.equals(event.getMember().getId())) 
                           ? DatabaseHandler.getlistUserSounds(userId) 
                           : DatabaseHandler.getlistUserSounds(userId, event.getGuild().getId());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName(), "https://github.com/SafJNest",
                event.getUser().getAvatarUrl());
        eb.setTitle("List of " + event.getJDA().getUserById(userId).getName());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));
        eb.setDescription("Total Sound: " + sounds.size());
        

        switch (args) {

            case "right":
                cont = 24 * page;
                while (cont < (24 * (page + 1)) && cont < sounds.size()) {
                   String locket = (!sounds.get(cont).getAsBoolean("public")) ? ":lock:" : "";
                    eb.addField("**"+sounds.get(cont).get("name")+"**" + locket, "ID: " + sounds.get(cont).get("id"), true);
                    cont++;
                }

                if (24 * (page + 1) >= sounds.size()) {
                    right = right.asDisabled();
                    right = right.withStyle(ButtonStyle.DANGER);
                }
                center = Button.primary("listuser-center-" + userId, "Page: " + (page + 1));
                center = center.withStyle(ButtonStyle.SUCCESS);
                center = center.asDisabled();
                event.getMessage().editMessageEmbeds(eb.build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":
                cont = (24 * (page - 2) < 0) ? 0 : 24 * (page - 2);

                while (cont < (24 * (page - 1)) && cont < sounds.size()) {
                    String locket = (!sounds.get(cont).getAsBoolean("public")) ? ":lock:" : "";
                    eb.addField("**"+sounds.get(cont).get("name")+"**" + locket, "ID: " + sounds.get(cont).get("id"), true);
                    cont++;
                }

                if ((page - 1) == 1) {
                    left = left.asDisabled();
                    left = left.withStyle(ButtonStyle.DANGER);
                }

                center = Button.primary("listuser-center-" + userId, "Page: " + (page - 1));
                center = center.withStyle(ButtonStyle.SUCCESS);
                center = center.asDisabled();
                event.getMessage().editMessageEmbeds(eb.build())
                        .setActionRow(left, center, right)
                        .queue();
                break;
        }
    }


    private void banUserEvent(ButtonInteractionEvent event) {
        if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)){
            event.deferReply().addContent("You don't have the permission to do that.").queue();
            return;
        }

        if(event.getButton().getStyle() != ButtonStyle.DANGER){
            event.editButton(event.getButton().withStyle(ButtonStyle.DANGER)).queue();
            return;
        }

        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        Member theGuy = event.getGuild().getMemberById(args);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName());
        eb.setTitle(theGuy.getUser().getName() + " has been banned");
        eb.setThumbnail(theGuy.getUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));
        Button pardon = Button.primary("unban-" + theGuy.getId(), "Pardon");
        event.getGuild().ban(theGuy, 0, TimeUnit.SECONDS).reason("Entered the blacklist").queue(
                    (e) -> event.getMessage().editMessageEmbeds(eb.build()).setActionRow(pardon).queue(),
                    new ErrorHandler().handle(
                        ErrorResponse.MISSING_PERMISSIONS,
                        (e) -> event.deferReply(true).addContent("Error. " + e.getMessage()).queue())
                );
        
    }

    private void kickUserEvent(ButtonInteractionEvent event) {
        if(!event.getMember().hasPermission(Permission.KICK_MEMBERS)){
            event.deferReply().addContent("You don't have the permission to do that.").queue();
            return;
        }

        if(event.getButton().getStyle() != ButtonStyle.DANGER){
            event.editButton(event.getButton().withStyle(ButtonStyle.DANGER)).queue();
            return;
        }

        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        Member theGuy = event.getGuild().getMemberById(args);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName());
        eb.setTitle(theGuy.getUser().getName() + " has been kicked");
        eb.setThumbnail(theGuy.getUser().getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));
        event.getGuild().kick(theGuy).reason("Entered the blacklist").queue(
            (e) -> event.getMessage().editMessageEmbeds(eb.build()).setComponents().queue(),
            new ErrorHandler().handle(
                ErrorResponse.MISSING_PERMISSIONS,
                (e) -> event.deferReply(true).addContent("Error. " + e.getMessage()).queue())
        );
        
    }

    private void ignoreUserEvent(ButtonInteractionEvent event) {
        if(!event.getMember().hasPermission(Permission.KICK_MEMBERS)){
            event.deferReply().addContent("You don't have the permission to do that.").queue();
            return;
        }
        event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds().get(0)).setComponents().queue();
        
    }


    private void pardonUserEvent(ButtonInteractionEvent event) {

        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        User theGuy = event.getJDA().getUserById(args);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName());
        eb.setTitle(theGuy.getName() + " has been unbanned");
        eb.setThumbnail(theGuy.getAvatarUrl());
        eb.setColor(Color.decode(Bot.getColor()));

        event.getGuild().unban(theGuy).queue(
            (e) -> event.getMessage().editMessageEmbeds(eb.build()).setComponents().queue(), 
            new ErrorHandler().handle(
                ErrorResponse.MISSING_PERMISSIONS,
                (e) -> event.deferReply(true).addContent("Error. " + e.getMessage()).queue())
        );
    }


    private void soundboardEvent(ButtonInteractionEvent event){
        Guild guild = event.getGuild();
        User self = event.getJDA().getSelfUser();
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        TextChannel textChannel = event.getChannel().asTextChannel();
        AudioChannel audioChannel = event.getMember().getVoiceState().getChannel();
        String path = "rsc" + File.separator + "SoundBoard"+ File.separator + args;

        PlayerManager pm = PlayerManager.get();
        pm.loadItemOrdered(guild, self, path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track);
                guild.getAudioManager().openAudioConnection(audioChannel);

                String id = args.split("\\.")[0];
                DatabaseHandler.updateUserPlays(id, event.getMember().getId());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {}
            
            @Override
            public void noMatches() {
                textChannel.sendMessage("File not found").queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("error: " + throwable.getMessage());
            }
        });
        
    }

}
