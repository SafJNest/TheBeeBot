package com.safjnest.Utilities.EventHandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.safjnest.Commands.League.Summoner;
import com.safjnest.Utilities.Audio.PlayerManager;
import com.safjnest.Utilities.Guild.BlacklistData;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertKey;
import com.safjnest.Utilities.Guild.Alert.AlertType;
import com.safjnest.Utilities.Guild.Alert.RewardData;
import com.safjnest.Utilities.LOL.AugmentData;
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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
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
        Guild guild = e.getGuild();
        User self = e.getJDA().getSelfUser();
        AudioChannel cj = e.getChannelJoined();
        AudioChannel cl = e.getChannelLeft();
        AudioChannel bebyc = guild.getAudioManager().getConnectedChannel();

        if(e.getMember().getId().equals(self.getId()) && cj == null) {
            PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler().clearQueue();
        }

        if((bebyc != null && cl != null) && (bebyc.getId().equals(cl.getId()))
            && (cl.getMembers().stream().filter(member -> !member.getUser().isBot()).count() == 0)) {
                guild.getAudioManager().closeAudioConnection();
                PlayerManager.get().getGuildMusicManager(guild, self).getTrackScheduler().clearQueue();
        }
        
        if(cj != null && ((bebyc != null && cj.getId().equals(bebyc.getId())) || bebyc == null)) {
            Member theGuy = e.getMember();
            ResultRow sound = DatabaseHandler.getGreet(theGuy.getId(), guild.getId());
            if(sound.emptyValues())
                return;

            PlayerManager pm = PlayerManager.get();

            String path = "rsc" + File.separator + "SoundBoard"+ File.separator + sound.get("id") + "." + sound.get("extension");

            pm.loadItemOrdered(guild, self, path, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    pm.getGuildMusicManager(guild, self).getTrackScheduler().playForce(track);
                    guild.getAudioManager().openAudioConnection(cj);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {}
                
                @Override
                public void noMatches() {}

                @Override
                public void loadFailed(FriendlyException throwable) {
                    System.out.println("error: " + throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event){
        System.out.println("[CACHE] Pushing new Guild into Database=> " + event.getGuild().getId());
        DatabaseHandler.insertGuild(event.getGuild().getId(), PREFIX);
    }


 

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!gs.getServer(event.getGuild().getId()).getCommandStatsRoom(event.getChannel().getIdLong()))
            return;
        String commandName = event.getName() + "Slash";
        String args = event.getOptions().toString();
        DatabaseHandler.insertCommand(event.getGuild().getId(), event.getMember().getId(), commandName, args);
    }


    /**
     * TODO: rifare questa merda
     */
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        ArrayList<Choice> choices = new ArrayList<>();
        String name = e.getName();
        
        if(e.getFullCommandName().equals("soundboard create") || e.getFocusedOption().getName().startsWith("sound-"))
            name = "play";
        
        else if(e.getFocusedOption().getName().equals("sound_add"))
            name = "play";

        else if(e.getFocusedOption().getName().equals("sound_remove"))
            name = "sound_remove";

        else if(e.getFullCommandName().equals("soundboard select") || e.getFocusedOption().getName().equals("soundboard_name") || e.getFullCommandName().equals("soundboard remove") || e.getFullCommandName().equals("soundboard delete"))
            name = "soundboard_select";
        
        else if(e.getFullCommandName().equals("customizesound"))
            name = "user_sound";

        else if(e.getFullCommandName().equals("bugsnotifier"))
            name = "help";

        else if(e.getFullCommandName().equals("TTS"))
            name = "tts";
        
        else if(e.getFocusedOption().getName().equals("role_remove")) 
            name = "alert_role";
        
        else if (e.getFocusedOption().getName().equals("reward_level")) 
            name = "rewards_level";
        
            else if (e.getFocusedOption().getName().equals("reward_roles")) 
            name = "reward_roles";
        
             
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
                List<AugmentData> augments = RiotHandler.getAugments();
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
                    for (ResultRow greet : DatabaseHandler.getlistGuildSounds(e.getGuild().getId(), 25))
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
            case "jumpto":
                List<AudioTrack> queue = PlayerManager.get().getGuildMusicManager(e.getGuild(), e.getJDA().getSelfUser()).getTrackScheduler().getQueue();
                if (e.getFocusedOption().getValue().equals("")) {
                    //Collections.shuffle(queue);
                    for (int i = 0; i < queue.size() && i < 10; i++)
                        choices.add(new Choice(queue.get(i).getInfo().title, String.valueOf(i + 1)));
                } else {
                    String query = e.getFocusedOption().getValue().toLowerCase();

                    int max = 0;
                    for (int i = 0; i < queue.size() && max < 10; i++) {
                        String title = queue.get(i).getInfo().title.toLowerCase();
                        if (title.contains(query)) {
                            choices.add(new Choice("[" + (i+1) +"] " + queue.get(i).getInfo().title, String.valueOf(i)));
                            max++;
                        }
                    }
                }
            case "alert_role":
                AlertData alert = gs.getServer(e.getGuild().getId()).getAlert(AlertType.WELCOME);
                if (alert != null && alert.getRoles() != null) {
                    HashMap<Integer, String> alertRoles = alert.getRoles();
                    List<Role> roles = new ArrayList<>();
                    for (Role r : e.getGuild().getRoles()) {
                        if (alertRoles.containsValue(r.getId()))
                            roles.add(r);
                    }
                    
                    Collections.shuffle(roles);
                    if (e.getFocusedOption().getValue().equals("")) {
                        for (int i = 0; i < roles.size() && i < 10; i++)
                            choices.add(new Choice(roles.get(i).getName(), roles.get(i).getId()));
                    } else {
                        for (Role role : roles) {
                            if (role.getName().toLowerCase().contains(e.getFocusedOption().getValue().toLowerCase()))
                                choices.add(new Choice(role.getName(), role.getId()));
                        }
                    
                    }
                }
                break;
            case "rewards_level":
                HashMap<AlertKey, AlertData> alerts = gs.getServer(e.getGuild().getId()).getAlerts();
                List<String> levels = new ArrayList<>();
                for (AlertData data : alerts.values()) {
                    if (data.getType() == AlertType.REWARD)
                        levels.add(String.valueOf(((RewardData) data).getLevel()));
                }
                if (e.getFocusedOption().getValue().equals("")) {
                    Collections.shuffle(levels);
                    for (int i = 0; i < levels.size() && i < 10; i++)
                        choices.add(new Choice(levels.get(i), levels.get(i)));
                } else {
                    for (String level : levels) {
                        if (level.startsWith(e.getFocusedOption().getValue()))
                            choices.add(new Choice(level, level));
                    }
                }
                break;
            case "reward_roles":
                if (e.getOption("reward_level") == null)
                    return;
                String rewardLevel = e.getOption("reward_level").getAsString();
                RewardData reward = gs.getServer(e.getGuild().getId()).getAlert(AlertType.REWARD, Integer.parseInt(rewardLevel));
                if (reward != null && reward.getRoles() != null) {
                    HashMap<Integer, String> rewardRoles = reward.getRoles();
                    List<Role> roles = new ArrayList<>();
                    for (Role r : e.getGuild().getRoles()) {
                        if (rewardRoles.containsValue(r.getId()))
                            roles.add(r);
                    }
                    Collections.shuffle(roles);
                    if (e.getFocusedOption().getValue().equals("")) {
                        for (int i = 0; i < roles.size() && i < 10; i++)
                            choices.add(new Choice(roles.get(i).getName(), roles.get(i).getId()));
                    } else {
                        for (Role role : roles) {
                            if (role.getName().toLowerCase().contains(e.getFocusedOption().getValue().toLowerCase()))
                                choices.add(new Choice(role.getName(), role.getId()));
                        }
                    }
                }
                break;
        }
        e.replyChoices(choices).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) { }

    /**
     * On join of a user (to make the bot welcome the new member)
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        MessageChannel channel = null;
        User newGuy = event.getUser();

        AlertData welcome = gs.getServer(event.getGuild().getId()).getAlert(AlertType.WELCOME);
        if(welcome != null && welcome.isValid()) {
            String channel_id = welcome.getChannelId();
    
            channel = event.getGuild().getTextChannelById(channel_id);
            String message = welcome.getMessage().replace("#user", newGuy.getAsMention());
    
            channel.sendMessage(message).queue();
                
            if (welcome.getRoles() != null) {
                String[] roles = welcome.getRoles().values().toArray(new String[0]);
                for (String role : roles) {
                    event.getGuild().addRoleToMember(newGuy, event.getGuild().getRoleById(role)).queue();
                }
            }
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
        AlertData leave = gs.getServer(event.getGuild().getId()).getAlert(AlertType.LEAVE);
        if(leave != null && leave.isValid()) {
            String channel_id = leave.getChannelId();
            channel = event.getGuild().getTextChannelById(channel_id);
            String message = leave.getMessage().replace("#user", event.getUser().getAsMention());
            channel.sendMessage(message).queue();
        }
        
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

        QueryResult guilds = DatabaseHandler.getGuildByThreshold(times, event.getGuild().getId());
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
        AlertData boost = gs.getServer(event.getGuild().getId()).getAlert(AlertType.BOOST);
        if(boost != null && boost.isValid()) {
            String channel_id = boost.getChannelId();
            channel = event.getGuild().getTextChannelById(channel_id);
            String message = boost.getMessage().replace("#user", event.getEntity().getAsMention());
            channel.sendMessage(message).queue();
        }
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event){
        String alertChannel = event.getGuild().getDefaultChannel().getId();
        GuildData g = gs.getServer(event.getGuild().getId());
        if(!event.getChannelType().isAudio()){
            String channelID = event.getChannel().getId();
            if (!g.deleteChannelData(channelID)) {
                return;
            }
            String alertMessage = "";
            String content = "";
            HashMap<AlertKey, AlertData> alerts = g.getAlerts();
            BlacklistData bld = g.getBlacklistData();
            if (alerts != null) {
                for (AlertData data : alerts.values()) {
                    if (data.getChannelId() != null && data.getChannelId().equals(channelID)) {
                        data.setAlertChannel(null);
                        content += data.getType().getDescription() + ", ";
                    }
                }
            }
            if (bld != null) {
                if (bld.getBlackChannelId() != null && bld.getBlackChannelId().equals(channelID)) {
                    bld.setBlackChannelId(null);
                    content += "Blacklist";
                }
            }
            if (!content.equals("")) {
                alertMessage = "These alerts need to be modified as the channel has been canceled:\n" + content;
                event.getJDA().getTextChannelById(alertChannel).sendMessage(alertMessage).queue();
            }
        }
    }


    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("rank-select")) {
            no.stelar7.api.r4j.pojo.lol.summoner.Summoner s = RiotHandler.getSummonerBySummonerId(event.getValues().get(0));
            event.deferReply().addEmbeds(Summoner.createEmbed(event.getJDA(), event.getJDA().getSelfUser().getId(), s).build()).queue();
        }
    }

}