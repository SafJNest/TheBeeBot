package com.safjnest.Utilities.EventHandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.safjnest.Commands.League.Summoner;
import com.safjnest.SlashCommands.ManageGuild.RewardsSlash;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.LOL.Augment;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * This class handles all events that could occur during the listening:
 * <ul>
 * <li>On update of a voice channel (to make the bot leave an empty voice
 * channel)</li>
 * <li>On join of a user (to make the bot welcome the new member)</li>
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.2
 */
public class EventHandler extends ListenerAdapter {
    private GuildSettings gs;
    private String PREFIX;

    public EventHandler(GuildSettings gs, String PREFIX) {
        this.gs = gs;
        this.PREFIX = PREFIX;
    }


    /**
     * On update of a voice channel (to make the bot leave an empty voice channel)
     */
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
        AudioChannel ac = e.getChannelJoined();
        AudioChannel bebyc = e.getGuild().getAudioManager().getConnectedChannel();

        if((e.getGuild().getAudioManager().isConnected() && e.getChannelLeft() != null) &&
            (e.getGuild().getAudioManager().getConnectedChannel().getId().equals(e.getChannelLeft().getId())) &&
            (e.getChannelLeft().getMembers().size() == 1)){
            e.getGuild().getAudioManager().closeAudioConnection();
        }


        if(e.getJDA().getUserById(e.getMember().getId()).isBot() || ac == null)
            return;

        if(bebyc != null && ac.getId().equals(bebyc.getId()) || bebyc == null){
            Member theGuy = e.getMember();
            ResultRow sound = DatabaseHandler.getGreet(theGuy.getId(), e.getGuild().getId(), e.getJDA().getSelfUser().getId());
            if(sound.emptyValues())
                return;

            PlayerManager pm = new PlayerManager();
            AudioManager audioManager = e.getGuild().getAudioManager();
            audioManager.setSendingHandler(pm.getAudioHandler());
            audioManager.openAudioConnection(ac);

            if(pm.getPlayer().getPlayingTrack() != null){
                //pm.stopAudioHandler();
            }

            String path = "rsc" + File.separator + "SoundBoard"+ File.separator + sound.get("id") + "." + sound.get("extension");
            pm.getAudioPlayerManager().loadItem(path, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    pm.getTrackScheduler().addQueue(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    /*
                    * for (AudioTrack track : playlist.getTracks()) {
                    * trackScheduler.queue(track);
                    * }
                    */
                }
                
                @Override
                public void noMatches() {
                    pm.getTrackScheduler().addQueue(null);
                }

                @Override
                public void loadFailed(FriendlyException throwable) {
                    System.out.println("error: " + throwable.getMessage());
                }
            });

            pm.getPlayer().playTrack(pm.getTrackScheduler().getTrack());
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        DatabaseHandler.insertGuild(event.getGuild().getId(), event.getJDA().getSelfUser().getId(), PREFIX);
    }


 

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!gs.getServer(event.getGuild().getId()).getCommandStatsRoom(event.getChannel().getIdLong()))
            return;
        String commandName = event.getName() + "Slash";
        String args = event.getOptions().toString();
        DatabaseHandler.insertCommand(event.getGuild().getId(), event.getJDA().getSelfUser().getId(), event.getMember().getId(), commandName, args);
    }


    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        ArrayList<Choice> choices = new ArrayList<>();
        String name = e.getName();

        if(e.getFullCommandName().equals("soundboard create"))
            name = "play";
        
        else if(e.getFocusedOption().getName().equals("sound_add"))
            name = "play";

        else if(e.getFocusedOption().getName().equals("sound_remove"))
            name = "sound_remove";

        else if(e.getFullCommandName().equals("soundboard select") || e.getFullCommandName().equals("soundboard add") || e.getFullCommandName().equals("soundboard remove") || e.getFullCommandName().equals("soundboard delete"))
            name = "soundboard_select";
        
        else if(e.getFullCommandName().equals("customizesound"))
            name = "user_sound";

        else if(e.getFullCommandName().equals("bugsnotifier"))
            name = "help";

        else if(e.getFullCommandName().equals("TTS"))
            name = "tts";
        
        switch (name) {
            case "play":
                if (e.getFocusedOption().getValue().equals("")) {
                    for (ResultRow sound : DatabaseHandler.getGuildRandomSound(e.getGuild().getId()))
                        choices.add(new Choice(sound.get("name"), sound.get("id")));
                } else {
                    for (ResultRow sound : DatabaseHandler.getFocusedGuildSound(e.getGuild().getId(), e.getFocusedOption().getValue()))
                        choices.add(new Choice(sound.get("name"), sound.get("id")));
                }

                break;
            case "user_sound":
                if (e.getFocusedOption().getValue().equals("")) {
                    for (ResultRow sound : DatabaseHandler.getUserRandomSound(e.getGuild().getId()))
                        choices.add(new Choice(sound.get("name"), sound.get("id")));
                } else {
                    for (ResultRow sound : DatabaseHandler.getFocusedUserSound(e.getGuild().getId(), e.getFocusedOption().getValue()))
                        choices.add(new Choice(sound.get("name"), sound.get("id")));
                }

                break;
            case "help":
                List<Command> allCommands = e.getJDA().retrieveCommands().complete();
                if (e.getFocusedOption().getValue().equals("")) {
                    Collections.shuffle(allCommands);
                    for (int i = 0; i < 10; i++)
                        choices.add(new Choice(allCommands.get(i).getName(), allCommands.get(i).getName()));
                } else {
                    for (Command c : allCommands) {
                        if (c.getName().startsWith(e.getFocusedOption().getValue()))
                            choices.add(new Choice(c.getName(), c.getName()));
                    }
                }
                break;

            case "champion":
                List<String> champions = Arrays.asList(RiotHandler.getChampions());
                if (e.getFocusedOption().getValue().equals("")) {
                    Collections.shuffle(champions);
                    for (int i = 0; i < 10; i++)
                        choices.add(new Choice(champions.get(i), champions.get(i)));
                } else {
                    int max = 0;
                    for (int i = 0; i < champions.size() && max < 10; i++) {
                        if (champions.get(i).toLowerCase().startsWith(e.getFocusedOption().getValue().toLowerCase())) {
                            choices.add(new Choice(champions.get(i), champions.get(i)));
                            max++;
                        }
                    }
                }
                break;

            case "infoaugment":
                List<Augment> augments = RiotHandler.getAugments();
                if (e.getFocusedOption().getValue().equals("")) {
                    Collections.shuffle(augments);
                    for (int i = 0; i < 10; i++)
                        choices.add(new Choice(augments.get(i).getName(), augments.get(i).getId()));
                } else {
                    int max = 0;
                    if (e.getFocusedOption().getValue().matches("\\d+")) {
                        for (int i = 0; i < augments.size() && max < 10; i++) {
                            if (augments.get(i).getId().startsWith(e.getFocusedOption().getValue())) {
                                choices.add(new Choice(augments.get(i).getName(), augments.get(i).getId()));
                                max++;
                            }
                        }
                    } else {
                        for (int i = 0; i < augments.size() && max < 10; i++) {
                            if (augments.get(i).getName().toLowerCase()
                                    .startsWith(e.getFocusedOption().getValue().toLowerCase())) {
                                choices.add(new Choice(augments.get(i).getName(), augments.get(i).getId()));
                                max++;
                            }
                        }
                    }
                }
                break;
                
            case "soundboard_select":
                if (e.getFocusedOption().getValue().equals("")) {
                    for (ResultRow sound : DatabaseHandler.getRandomSoundboard(e.getGuild().getId()))
                        choices.add(new Choice(sound.get("name"), sound.get("id")));
                } else {
                    for (ResultRow sound : DatabaseHandler.getFocusedSoundboard(e.getGuild().getId(), e.getFocusedOption().getValue()))
                        choices.add(new Choice(sound.get("name"), sound.get("id")));
                }
                break;
            case "sound_remove":
                if (e.getOption("name") == null)
                    return;
                String soundboardId = e.getOption("name").getAsString();
                if (e.getFocusedOption().getValue().equals("")) {
                    for (ResultRow sound : DatabaseHandler.getSoundsFromSoundBoard(soundboardId))
                        choices.add(new Choice(sound.get("sound.name"), sound.get("soundboard_sounds.sound_id")));
                } else {
                    for (ResultRow sound : DatabaseHandler.getFocusedSoundFromSounboard(soundboardId, e.getFocusedOption().getValue()))
                        choices.add(new Choice(sound.get("s.name"), sound.get("s.id")));
                }
                break;

            case "greet":
                if (e.getFocusedOption().getValue().equals("")) {
                    for (ResultRow greet : DatabaseHandler.getlistGuildSounds(e.getGuild().getId()))
                        choices.add(new Choice(greet.get("name"), greet.get("id")));
                } else {
                    for (ResultRow greet : DatabaseHandler.getFocusedListUserSounds(e.getUser().getId(), e.getGuild().getId(), e.getFocusedOption().getValue()))
                        choices.add(new Choice(greet.get("name") + " (" + greet.get("id") + ")", greet.get("id")));
                }
            break;
            case "tts":
                if (e.getFocusedOption().getValue().equals("")) {
                    //TODO non ho voglia
                    //TODO nemmeno io
                } else {

                }
            break;
        }
        e.replyChoices(choices).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if(event.getModalId().startsWith("rewards")){
            String role = event.getValue("rewards-role").getAsString();
            String lvl = event.getValue("rewards-lvl").getAsString();
            String msg = event.getValue("rewards-message").getAsString();

            if(msg.equals("//")) 
                msg = null;
            try {
                role = event.getGuild().getRolesByName(role.substring(1), true).get(0).getId();
            } catch (Exception e) {
                event.reply("Role not found").queue();
                return;
            }

            DatabaseHandler.insertRewards(event.getGuild().getId(), role, lvl, msg);
            event.deferEdit().queue();
            RewardsSlash.createEmbed(event.getMessage()).queue();
        }
    }

    /**
     * On join of a user (to make the bot welcome the new member)
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        /**
         * Welcome message 
         */
        MessageChannel channel = null;
        User newGuy = event.getUser();
        ResultRow alert = DatabaseHandler.getAlert(event.getGuild().getId(), event.getJDA().getSelfUser().getId());

        String notNullPls = alert.get("welcome_channel");
        if (notNullPls != null && alert.getAsBoolean("welcome_enabled")){
            channel = event.getGuild().getTextChannelById(notNullPls);
            String message = alert.get("welcome_message");

            message = message.replace("#user", newGuy.getAsMention());
            channel.sendMessage(message).queue();

            if(alert.get("welcome_role") != null)
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(alert.get("welcome_role"))).queue();
                
            /* 
            ArrayList<String> roles = sql.getAllRowsSpecifiedColumn(query, "role_id");
            if (roles.size() > 0) {
                for (String role : roles) {
                    event.getGuild().addRoleToMember(newGuy, event.getGuild().getRoleById(role)).queue();
                }
            }
            */
        }

        /**
         * Blacklist
         */
        int threshold = gs.getServer(event.getGuild().getId()).getThreshold();
        if(threshold == 0)
            return;
        
        int times = DatabaseHandler.getBlacklistBan(event.getUser().getId());

        if(!gs.getServer(event.getGuild().getId()).blacklistEnabled() || gs.getServer(event.getGuild().getId()).getThreshold() == 0 || gs.getServer(event.getGuild().getId()).getThreshold() > times)
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getJDA().getSelfUser().getName());
        eb.setThumbnail(newGuy.getAvatarUrl());
        eb.setTitle(":radioactive:Blacklist:radioactive:");
        eb.setDescription("The new member " + newGuy.getAsMention() + " is on the blacklist for being banned in " + times + " different guilds.\nYou have the discretion to choose the next steps.");

        channel = event.getGuild().getTextChannelById(gs.getServer(event.getGuild().getId()).getBlackChannelId());

        Button kick = Button.primary("kick-" + newGuy.getId(), "Kick");
        Button ban = Button.primary("ban-" + newGuy.getId(), "Ban");
        Button ignore = Button.primary("ignore-" + newGuy.getId(), "Ignore");


        kick = kick.withStyle(ButtonStyle.PRIMARY);
        ban = ban.withStyle(ButtonStyle.PRIMARY);
        ignore = ignore.withStyle(ButtonStyle.SUCCESS);
        channel.sendMessageEmbeds(eb.build()).addActionRow(ignore, kick, ban).queue();
        
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        
        MessageChannel channel = null;
        ResultRow alert = DatabaseHandler.getAlert(event.getGuild().getId(), event.getJDA().getSelfUser().getId());
        
        String notNullPls = alert.get("leave_channel");
        if (notNullPls == null || !alert.getAsBoolean("leave_enabled"))
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        
        String message = alert.get("leave_message");
        message = message.replace("#user", event.getUser().getAsMention());
        channel.sendMessage(message).queue();
    }


    @Override
    public void onGuildBan(GuildBanEvent event) {
        User theGuy = event.getUser();
        int threshold = gs.getServer(event.getGuild().getId()).getThreshold();

        if(threshold == 0)
            return;
        
        DatabaseHandler.insertUserBlacklist(event.getUser().getId(), event.getGuild().getId());

        int times = 0;
        times = times + DatabaseHandler.getBannedTimes(event.getUser().getId());

        QueryResult guilds = DatabaseHandler.getGuildByThreshold(times, event.getJDA().getSelfUser().getId(), event.getGuild().getId());
        if(guilds == null)
            return;
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getJDA().getSelfUser().getName());
        eb.setThumbnail(theGuy.getAvatarUrl());
        eb.setTitle(":radioactive:Blacklist:radioactive:");
        eb.setDescription("The member " + theGuy.getAsMention() + " is on the blacklist for being banned in " + times + " different guilds.\nYou have the discretion to choose the next steps.");
        for(ResultRow g : guilds){
            Guild gg = event.getJDA().getGuildById(g.get("guild_id"));
            if(gg.getMemberById(theGuy.getId()) == null)
                continue;

            TextChannel channel = gg.getTextChannelById(g.get("blacklist_channel"));

            Button kick = Button.primary("kick-" + theGuy.getId(), "Kick");
            Button ban = Button.primary("ban-" + theGuy.getId(), "Ban");
            Button ignore = Button.primary("ignore-" + theGuy.getId(), "Ignore");

            kick = kick.withStyle(ButtonStyle.PRIMARY);
            ban = ban.withStyle(ButtonStyle.PRIMARY);
            ignore = ignore.withStyle(ButtonStyle.SUCCESS);
            channel.sendMessageEmbeds(eb.build()).addActionRow(ignore, kick, ban).queue();
            //channel.sendMessage("THIS PIECE OF SHIT DOGSHIT RANDOM " + theGuy.getName() + " HAS BEEN BANNED " + times + " TIMES").queue();
        }
        
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        User theGuy = event.getUser();
        DatabaseHandler.deleteBlacklist(event.getGuild().getId(), theGuy.getId());
    }

    /**
     * On update of a user's boost time (to make the bot praise the user)
     */
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event) {
        MessageChannel channel = null;
        ResultRow alert = DatabaseHandler.getAlert(event.getGuild().getId(), event.getJDA().getSelfUser().getId());
        String notNullPls = alert.get("boost_channel");
        if (notNullPls == null || !alert.getAsBoolean("boost_enabled"))
            return;
        channel = event.getGuild().getTextChannelById(notNullPls);
        String message = alert.get("boost_message");
        message = message.replace("#user", event.getUser().getAsMention());
        channel.sendMessage(message).queue();
    }


    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("rank-select")) {
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = RiotHandler.getSummonerBySummonerId(event.getValues().get(0));
            event.deferReply().addEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(), s).build()).queue();
        }
    }

}