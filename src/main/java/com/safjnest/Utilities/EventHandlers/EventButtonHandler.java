package com.safjnest.Utilities.EventHandlers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.safjnest.Commands.League.GameRank;
import com.safjnest.Commands.League.InfoMatches;
import com.safjnest.Commands.League.Summoner;
import com.safjnest.SlashCommands.ManageGuild.RewardsSlash;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.LOL.RiotHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import no.stelar7.api.r4j.pojo.lol.spectator.SpectatorParticipant;

public class EventButtonHandler extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if(event.getButton().getId().startsWith("rewards-")){
            rewardsButtonEvent(event);
            return;
        }

        event.deferEdit().queue();
        if (event.getButton().getId().startsWith("lol-")) 
            lolButtonEvent(event);
        
        else if (event.getButton().getId().startsWith("match-")) 
            matchButtonEvent(event);

        else if (event.getButton().getId().startsWith("rank-")) 
            rankButtonEvent(event);

        else if (event.getButton().getId().startsWith("list-")) 
            listButtonEvent(event);

        else if (event.getButton().getId().startsWith("listuser-")) 
            listUserButtonEvent(event);
        
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
        String query = "SELECT guild_id FROM lol_user WHERE account_id = '" + RiotHandler.getAccountIdByName(nameSum)
                + "';";
        query = "SELECT summoner_id FROM lol_user WHERE guild_id = '"
                + DatabaseHandler.getSql().getString(query, "guild_id") + "';";
        ArrayList<ArrayList<String>> accounts = DatabaseHandler.getSql().getAllRows(query, 1);
        switch (args) {

            case "right":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get(0)).getName().equals(nameSum))
                        index = i;
                }

                if ((index + 1) == accounts.size())
                    index = 0;
                else
                    index += 1;

                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)).getName());
                center = center.asDisabled();
                
                event.getMessage()
                        .editMessageEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0))).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get(0)).getName().equals(nameSum))
                        index = i;

                }

                if (index == 0)
                    index = accounts.size() - 1;
                else
                    index -= 1;

                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)).getName());
                center = center.asDisabled();
                
                event.getMessage()
                        .editMessageEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0))).build())
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
        String query = "SELECT guild_id FROM lol_user WHERE account_id = '" + RiotHandler.getAccountIdByName(nameSum)
                + "';";
        query = "SELECT summoner_id FROM lol_user WHERE guild_id = '"
                + DatabaseHandler.getSql().getString(query, "guild_id") + "';";
        ArrayList<ArrayList<String>> accounts = DatabaseHandler.getSql().getAllRows(query, 1);
        switch (args) {

            case "right":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get(0)).getName().equals(nameSum))
                        index = i;
                }

                if ((index + 1) == accounts.size())
                    index = 0;
                else
                    index += 1;

                center = Button.primary("match-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)).getName());
                center = center.asDisabled();
                
                event.getMessage()
                        .editMessageEmbeds(InfoMatches.createEmbed(
                            RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)), event.getJDA()).build())
                        .setActionRow(left, center, right)
                        .queue();
                break;

            case "left":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get(0)).getName().equals(nameSum))
                        index = i;

                }

                if (index == 0)
                    index = accounts.size() - 1;
                else
                    index -= 1;

                center = Button.primary("match-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)).getName());
                center = center.asDisabled();
                
               event.getMessage()
                        .editMessageEmbeds(InfoMatches.createEmbed(
                            RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)), event.getJDA()).build())
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
        String query = "SELECT guild_id FROM lol_user WHERE account_id = '" + RiotHandler.getAccountIdByName(nameSum)
                + "';";
        query = "SELECT summoner_id FROM lol_user WHERE guild_id = '"
                + DatabaseHandler.getSql().getString(query, "guild_id") + "';";
        ArrayList<ArrayList<String>> accounts = DatabaseHandler.getSql().getAllRows(query, 1);
        List<SpectatorParticipant> users = null;

        no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = null;

        switch (args) {

            case "right":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get(0)).getName().equals(nameSum))
                        index = i;
                }

                if ((index + 1) == accounts.size())
                    index = 0;
                else

                    index += 1;

                s = RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0));
                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)).getName());
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
                            GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                    s, users).build());
                    action.setComponents(ActionRow.of(menu),
                            ActionRow.of(left, center, right));
                    action.queue();
                } catch (Exception e) {
                    event.getMessage()
                            .editMessageEmbeds(
                                    GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                            s, users).build())
                            .setActionRow(left, center, right)
                            .queue();
                }
                break;

            case "left":

                for (int i = 0; i < accounts.size(); i++) {
                    if (RiotHandler.getSummonerBySummonerId(accounts.get(i).get(0)).getName().equals(nameSum))
                        index = i;
                }

                if (index == 0)
                    index = accounts.size() - 1;
                else
                    index -= 1;

                s = RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0));
                center = Button.primary("lol-center",
                        RiotHandler.getSummonerBySummonerId(accounts.get(index).get(0)).getName());
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
                            GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
                                    s, users).build());
                    action.setComponents(ActionRow.of(menu),
                            ActionRow.of(left, center, right));
                    action.queue();
                } catch (Exception e) {
                    event.getMessage()
                            .editMessageEmbeds(
                                    GameRank.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(),
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

        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE guild_id = '"
                + event.getGuild().getId() + "' ORDER BY name ASC;";
        ArrayList<ArrayList<String>> sounds = DatabaseHandler.getSql().getAllRows(query, 2);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName(), "https://github.com/SafJNest",
                event.getUser().getAvatarUrl());
        eb.setTitle("List of " + event.getGuild().getName());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        eb.setDescription("Total Sound: " + sounds.size());
        
        
        switch (args) {

            case "right":
                for (Button b : event.getMessage().getButtons()) {
                    if (b.getLabel().startsWith("Page"))
                        page = Integer.valueOf(String.valueOf(b.getLabel().charAt(b.getLabel().indexOf(":") + 2)));
                }

                cont = 24 * page;
                while (cont < (24 * (page + 1)) && cont < sounds.size()) {
                    eb.addField("**" + sounds.get(cont).get(1) + "**", "ID: " + sounds.get(cont).get(0), true);
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
                    eb.addField("**" + sounds.get(cont).get(1) + "**", "ID: " + sounds.get(cont).get(0), true);
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

        String query = "SELECT id, name, guild_id, user_id, extension FROM sound WHERE user_id = '"
                + userId + "' ORDER BY name ASC;";
        ArrayList<ArrayList<String>> sounds = DatabaseHandler.getSql().getAllRows(query, 2);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getName(), "https://github.com/SafJNest",
                event.getUser().getAvatarUrl());
        eb.setTitle("List of " + event.getJDA().getUserById(userId).getName());
        eb.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
        eb.setColor(Color.decode(
                BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));
        eb.setDescription("Total Sound: " + sounds.size());
        

        switch (args) {

            case "right":
                cont = 24 * page;
                while (cont < (24 * (page + 1)) && cont < sounds.size()) {
                    eb.addField("**" + sounds.get(cont).get(1) + "**", "ID: " + sounds.get(cont).get(0), true);
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
                    eb.addField("**" + sounds.get(cont).get(1) + "**", "ID: " + sounds.get(cont).get(0), true);
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

    private void rewardsButtonEvent(ButtonInteractionEvent event) {
        if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)){
            event.deferReply().addContent("You don't have the permission to do that.").queue();
            return;
        }
        String args = event.getButton().getId().substring(event.getButton().getId().indexOf("-") + 1);
        
        switch (args){
            
            case "add":
                TextInput subject = TextInput.create("rewards-lvl", "Level", TextInputStyle.SHORT)
                    .setPlaceholder("1")
                    .setMinLength(1)
                    .setMaxLength(100) // or setRequiredRange(10, 100)
                    .build();

                TextInput body = TextInput.create("rewards-message", "Message (write // for no message)", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Contratulation #user you have reached level #level so you gain the role: #role")
                        .setMinLength(2)
                        .setMaxLength(1000)
                        .build();
                
                TextInput role = TextInput.create("rewards-role", "Role (@rolename)", TextInputStyle.SHORT)
                        .setPlaceholder("@king")
                        .setMinLength(2)
                        .setMaxLength(20)
                        .build();

                Modal modal = Modal.create("rewards", "Set a new Reward")
                        .addComponents(ActionRow.of(subject), ActionRow.of(body), ActionRow.of(role))
                        .build();
                event.replyModal(modal).queue();
                break;

            default:
                if(event.getButton().getId().startsWith("rewards-role-")){
                    if(event.getButton().getStyle() == ButtonStyle.DANGER){
                        String roleString = event.getButton().getId().split("-")[2];
                        String query = "DELETE FROM rewards_table WHERE role_id = '" + roleString + "';";
                        DatabaseHandler.getSql().runQuery(query);
                        event.deferEdit().queue();
                        RewardsSlash.createEmbed(event.getMessage(), event.getGuild()).queue();
                        return;
                    }
                    //modify button style into danger
                    
                    event.editButton(event.getButton().withStyle(ButtonStyle.DANGER)).queue();
                    
                }
                break;

        }
    }

}
