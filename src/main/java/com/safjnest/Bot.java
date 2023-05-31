package com.safjnest;

/**
 * Copyright (c) 22 Giugno anno 0, 2022, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 */

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.safjnest.Utilities.*;
import com.safjnest.Utilities.Bot.BotSettings;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Commands.SlashCommandsHandler;
import com.safjnest.Utilities.EventHandlers.EventButtonHandler;
import com.safjnest.Utilities.EventHandlers.EventHandler;
import com.safjnest.Utilities.EventHandlers.EventHandlerBeebot;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.tts.TTSHandler;
import com.safjnest.Commands.LOL.*;
import com.safjnest.Commands.Misc.*;
import com.safjnest.Commands.Settings.SetLeaveMessage;
import com.safjnest.Commands.Settings.SetLevelUpMessage;
import com.safjnest.Commands.Settings.SetPrefix;
import com.safjnest.Commands.Settings.SetRoom;
import com.safjnest.Commands.Settings.SetSummoner;
import com.safjnest.Commands.Settings.SetVoice;
import com.safjnest.Commands.Settings.SetWelcomeMessage;
import com.safjnest.Commands.Math.*;
import com.safjnest.Commands.Admin.ListGuild;
import com.safjnest.Commands.Admin.Ping;
import com.safjnest.Commands.Admin.PrefixList;
import com.safjnest.Commands.Admin.Query;
import com.safjnest.Commands.Admin.Ram;
import com.safjnest.Commands.Admin.RawMessage;
import com.safjnest.Commands.Admin.Restart;
import com.safjnest.Commands.Admin.Shutdown;
import com.safjnest.Commands.Admin.ThreadCounter;
import com.safjnest.Commands.Audio.*;
import com.safjnest.Commands.Dangerous.*;
import com.safjnest.Commands.ManageGuild.*;
import com.safjnest.Commands.ManageMembers.*;
import com.safjnest.Commands.ManageMembers.Move;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import no.stelar7.api.r4j.impl.R4J;

/**
 * Main class of the bot.
 * <p>
 * The {@code JDA} is instantiated and his parameters are
 * specified (token, activity, cache, ...). The bot connects to
 * discord and AWS S3. The bot's commands are instantiated.
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @version 2.0
 */
public class Bot extends ListenerAdapter implements Runnable {

    private BotSettingsHandler bs;

    private JDA jda;
    public String PREFIX;
    public String botId;
    private Activity activity;
    public String color;
    private String ownerID;
    private String helpWord;

    private String token;
    private String youtubeApiKey;

    private int maxPrime;

    private HashMap<String, String> tierOneLink = new HashMap<>();

    private TTSHandler tts;
    private SQL sql;
    private R4J riotApi;
    private SlashCommandsHandler sch;

    public Bot(BotSettingsHandler bs, TTSHandler tts, SQL sql, R4J riotApi) {
        this.tts = tts;
        this.sql = sql;
        this.riotApi = riotApi;
        this.bs = bs;
    }

    /**
     * Where the magic happens.
     *
     */
    @Override
    public void run() {
        // fastest way to compile
        // ctrl c ctrl v
        // assembly:assembly -DdescriptorId=jar-with-dependencies

        JSONParser parser = new JSONParser();
        JSONObject settings = null, discordSettings = null, settingsSettings = null;

        try (Reader reader = new FileReader("rsc" + File.separator + "settings.json")) {
            settings = (JSONObject) parser.parse(reader);
            discordSettings = (JSONObject) settings.get(Thread.currentThread().getName());
            settingsSettings = (JSONObject) settings.get("settings");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PREFIX = discordSettings.get("prefix").toString();
        activity = Activity.playing(MessageFormat.format(discordSettings.get("activity").toString().replace("{0}", PREFIX), PREFIX));
        token = discordSettings.get("discordToken").toString();
        color = discordSettings.get("embedColor").toString();
        ownerID = discordSettings.get("ownerID").toString();
        helpWord = discordSettings.get("helpWord").toString();

        maxPrime = Integer.valueOf(discordSettings.get("maxPrime").toString());
        youtubeApiKey = settingsSettings.get("youtubeApiKey").toString();


        EventHandlerBeebot listenerozzobeby = new EventHandlerBeebot();
        
        jda = JDABuilder
                .createLight(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.ACTIVITY)
                .build();
                if(Thread.currentThread().getName().equals("beebot"))
                    jda.addEventListener(listenerozzobeby);
                
        botId = jda.getSelfUser().getId();
        bs.setSettings(new BotSettings(
                botId,
                PREFIX,
                color), botId);

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setHelpWord(helpWord);
        builder.setOwnerId(ownerID);
        builder.setActivity(activity);

        GuildSettings gs = new GuildSettings(null, botId, PREFIX);
        builder.setPrefixFunction(event -> {
            if (event.getChannelType() == ChannelType.PRIVATE)
                return "";
            if (event.isFromGuild()) {
                GuildData gd = gs.getServer(event.getGuild().getId());
                return gd == null ? PREFIX : gd.getPrefix();
            }
            return null;
        });

        sch = new SlashCommandsHandler(
            Thread.currentThread().getName(),
            youtubeApiKey,
            tierOneLink,
            tts,
            riotApi,
            sql,
            gs,
            maxPrime
        );

        if(!Thread.currentThread().getName().equals("beebot moderation")){
            // Audio
            builder.addCommand(new Connect());
            builder.addCommand(new DeleteSound(sql));
            builder.addCommand(new Disconnect());
            builder.addCommand(new DownloadSound(sql));
            builder.addCommand(new List());
            builder.addCommand(new ListUser());
            builder.addCommand(new PlayYoutube(youtubeApiKey, tierOneLink));
            builder.addCommand(new PlaySound(sql));
            builder.addCommand(new Upload(sql));
            builder.addCommand(new TTS(tts, sql));
            builder.addCommand(new Stop());
            builder.addCommand(new CustomizeSound());
            builder.addCommand(new SetVoice(sql));
        }

        if (!Thread.currentThread().getName().equals("beebot music")) {
            // Manage Guild
            builder.addCommand(new Anonym());
            builder.addCommand(new ChannelInfo());
            builder.addCommand(new Clear());
            builder.addCommand(new Msg());
            builder.addCommand(new ServerInfo());
            builder.addCommand(new MemberInfo());
            builder.addCommand(new EmojiInfo());
            builder.addCommand(new InviteBot());
            builder.addCommand(new ListGuild());
            builder.addCommand(new Leaderboard());

            // Manage Member
            builder.addCommand(new Ban());
            builder.addCommand(new Unban());
            builder.addCommand(new Kick());
            builder.addCommand(new Move(sql));
            builder.addCommand(new Mute());
            builder.addCommand(new UnMute());
            builder.addCommand(new Image());
            builder.addCommand(new Permissions());
            builder.addCommand(new ModifyNickname());
            builder.addCommand(new ListRoom(sql));

            // Advanced
            builder.addCommand(new SetWelcomeMessage(sql));
            builder.addCommand(new SetLeaveMessage(sql));
            builder.addCommand(new SetRoom(sql));

            // Dangerous
            builder.addCommand(new RandomMove());
        }


        if(!Thread.currentThread().getName().equals("beebot music") && !Thread.currentThread().getName().equals("beebot moderation")){
            builder.addCommand(new Champ());
            builder.addCommand(new Summoner());
            builder.addCommand(new FreeChamp());
            builder.addCommand(new GameRank());
            builder.addCommand(new SetSummoner(riotApi, sql));
            builder.addCommand(new LastMatches(riotApi, sql));

            // Math
            builder.addCommand(new Prime(maxPrime));
            builder.addCommand(new Calculator());
            builder.addCommand(new Dice());

            builder.addCommand(new ThreadCounter());
            builder.addCommand(new SetLevelUpMessage());
            builder.addCommand(new VandalizeServer());
            builder.addCommand(new Jelly());
            builder.addCommand(new ChatGPT());
            builder.addCommand(new Shutdown());
            builder.addCommand(new Restart());
            builder.addCommand(new Query());
        }


        builder.addCommand(new SetPrefix(sql, gs));

        // Misc
        builder.addCommand(new Ping());
        builder.addCommand(new BugsNotifier());
        builder.addCommand(new Ram());
        builder.addCommand(new Help(gs));
        builder.addCommand(new Aliases());
        builder.addCommand(new RawMessage());
        builder.addCommand(new PrefixList());
        builder.addCommand(new DisableSlash());
        builder.addCommand(new EnableSlash(sch));


        String name = Thread.currentThread().getName();
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onReady(ReadyEvent event) {
                if(name.equals("beebot canary"))
                    return;
                java.util.List<Guild> guilds = jda.getGuilds();
                Collection<CommandData> commandDataList = sch.getCommandData();
                for(Guild g : guilds){
                    if(hasSlash(g.getId(), botId))
                        g.updateCommands().addCommands(commandDataList).queue();
                    else
                        g.updateCommands().queue(); 
                }
                System.out.println("[" + name + "] INFO Slash commands loaded");
            }
        });

        CommandClient client = builder.build();
        jda.addEventListener(client);
        jda.addEventListener(new EventHandler(sql, sch));
        jda.addEventListener(new EventButtonHandler());

        
        synchronized (this){
            try {wait();} 
            catch (InterruptedException e) {
                System.out.println("[" + Thread.currentThread().getName() + "] INFO Bot has been shutdown or something went wrong.");
                jda.shutdown();
                return;
            }
        }
    }

    public static boolean hasSlash(String guildId, String botId){
        String query = "select has_slash from guild_settings where guild_id = '" + guildId + "' and bot_id = '" + botId + "';";
        String res = DatabaseHandler.getSql().getString(query, "has_slash");
        return (res == null) ? true : res.equals("1");
    }
}
