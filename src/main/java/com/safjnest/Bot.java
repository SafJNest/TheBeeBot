/**
 * Copyright (c) 22 Giugno anno 0, 2022, SafJNest and/or its affiliates. All rights reserved.
 * SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 */
package com.safjnest;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;

import com.safjnest.Utilities.*;
import com.safjnest.Utilities.Bot.BotSettings;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Controller.Connection;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.EventHandlers.CommandEventHandler;
import com.safjnest.Utilities.EventHandlers.EventButtonHandler;
import com.safjnest.Utilities.EventHandlers.EventHandler;
import com.safjnest.Utilities.EventHandlers.EventHandlerBeebot;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Commands.Misc.*;
import com.safjnest.Commands.Owner.*;
import com.safjnest.Commands.Owner.Shutdown;
import com.safjnest.Commands.Settings.*;
import com.safjnest.Commands.Math.*;
import com.safjnest.Commands.Audio.*;
import com.safjnest.Commands.League.*;
import com.safjnest.Commands.ManageGuild.*;
import com.safjnest.Commands.ManageMembers.*;

import com.safjnest.SlashCommands.Audio.*;
import com.safjnest.SlashCommands.Audio.Greet.GreetSlash;
import com.safjnest.SlashCommands.Audio.List.ListSlash;
import com.safjnest.SlashCommands.Audio.Play.PlaySlash;
import com.safjnest.SlashCommands.Audio.Soundboard.SoundboardSlash;
import com.safjnest.SlashCommands.League.*;
import com.safjnest.SlashCommands.ManageGuild.*;
import com.safjnest.SlashCommands.ManageMembers.*;
import com.safjnest.SlashCommands.ManageMembers.Blacklist.BlacklistSlash;
import com.safjnest.SlashCommands.ManageMembers.Move.MoveSlash;
import com.safjnest.SlashCommands.Math.*;
import com.safjnest.SlashCommands.Misc.*;
import com.safjnest.SlashCommands.Settings.*;
import com.safjnest.SlashCommands.Settings.Boost.BoostSlash;
import com.safjnest.SlashCommands.Settings.Leave.LeaveSlash;
import com.safjnest.SlashCommands.Settings.LevelUp.LevelUpSlash;
import com.safjnest.SlashCommands.Settings.Welcome.WelcomeSlash;

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
    private String[] coOwnersIDs;
    private String helpWord;

    private String token;
    private String youtubeApiKey;

    private int maxPrime;

    private TTSHandler tts;
    private R4J riotApi;
    private GuildSettings gs;

    public Bot(BotSettingsHandler bs, TTSHandler tts,  R4J riotApi) {
        this.tts = tts;
        this.riotApi = riotApi;
        this.bs = bs;
    }

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return new String[0];
        
        String[] arr = new String[array.size()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = (String) array.get(i);
        return arr;
    }

    /**
     * Where the magic happens.
     *
     */
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
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
        coOwnersIDs = toStringArray((JSONArray) discordSettings.get("coOwnersIDs"));
        helpWord = discordSettings.get("helpWord").toString();

        maxPrime = Integer.valueOf(discordSettings.get("maxPrime").toString());
        youtubeApiKey = settingsSettings.get("youtubeApiKey").toString();

        jda = JDABuilder
                .createLight(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MODERATION)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.ACTIVITY)
                .build();

        botId = jda.getSelfUser().getId();

        gs = new GuildSettings(null, botId, PREFIX);
        ExpSystem farm = new ExpSystem();
        

        bs.setSettings(new BotSettings(botId, PREFIX, color), botId);

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setHelpWord(helpWord);
        builder.setOwnerId(ownerID);
        builder.setCoOwnerIds(coOwnersIDs);
        builder.setActivity(activity);

        
        
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onReady(ReadyEvent event) {
                //gs.retrieveAllServers();
                /* 
                for(Guild g : event.getJDA().getGuilds()){
                    g.updateCommands().queue();
                }
                */
                System.out.println("[" + threadName + "] INFO no more guild cached correctly");
            }
        });
        
        builder.setPrefixFunction(event -> {
            if (event.getChannelType() == ChannelType.PRIVATE)
                return "";
            if (event.isFromGuild()) {
                GuildData gd = gs.getServer(event.getGuild().getId());
                return gd == null ? PREFIX : gd.getPrefix();
            }
            return null;
        });

        ArrayList<String> beebotsAll = new ArrayList<String>(Arrays.asList("beebot", "beebot 2", "beebot 3", "beebot canary"));

        ArrayList<Command> commandsList = new ArrayList<Command>();
        Collections.addAll(commandsList, new PrintCache(gs, farm), new Ping(), new Ram(), new Help(gs), new Prefix(gs));

        if(beebotsAll.contains(threadName))
            Collections.addAll(commandsList, new Summoner(), new InfoAugment(), new FreeChamp(), new Livegame(), 
                new LastMatches(riotApi), new Opgg(), new Calculator(), new Dice(), 
                new ThreadCounter(), new VandalizeServer(), new Jelly(), new Shutdown(), new Restart(), new Query());
        
        if(beebotsAll.contains(threadName) || threadName.equals("beebot moderation"))
            Collections.addAll(commandsList, new ChannelInfo(), new Clear(), new ServerInfo(), new MemberInfo(), new EmojiInfo(), 
                new InviteBot(), new ListGuild(), new Ban(), new Unban(), new Kick(), new Mute(), new UnMute(), new Image(), 
                new Permissions(), new ModifyNickname(), new RandomMove());

        if(beebotsAll.contains(threadName) || threadName.equals("beebot music"))
            Collections.addAll(commandsList, new Connect(), new Disconnect(), new List(), new ListUser(), new PlayYoutube(youtubeApiKey), 
            new PlaySound(), new TTS(tts), new Stop());
        
        if(threadName.equals("beebot") || threadName.equals("beebot canary"))
            Collections.addAll(commandsList, new Leaderboard(), new Test());
    
        builder.addCommands(commandsList.toArray(new Command[commandsList.size()]));

        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new PingSlash(), new BugsNotifierSlash(), new HelpSlash(gs), new PrefixSlash(gs));

        if(beebotsAll.contains(threadName))
            Collections.addAll(slashCommandsList, new SummonerSlash(), new InfoAugmentSlash(), new FreeChampSlash(), 
                new LivegameSlash(riotApi), new SetSummonerSlash(riotApi), new LastMatchesSlash(riotApi), 
                new PrimeSlash(maxPrime), new CalculatorSlash(), new DiceSlash(), new ChampionSlash(), new OpggSlash());
        
        if(beebotsAll.contains(threadName) || threadName.equals("beebot moderation"))
            Collections.addAll(slashCommandsList, new ChannelInfoSlash(), new ClearSlash(), new MsgSlash(), 
                new ServerInfoSlash(), new MemberInfoSlash(), new EmojiInfoSlash(), new InviteBotSlash(), new BanSlash(), new UnbanSlash(), 
                new KickSlash(), new MoveSlash(),new MuteSlash(), new UnMuteSlash(), new ImageSlash(), new PermissionsSlash(), 
                new ModifyNicknameSlash(), new WelcomeSlash(gs), new LeaveSlash(), new BoostSlash(), new BlacklistSlash(gs));

        if(beebotsAll.contains(threadName) || threadName.equals("beebot music"))
            Collections.addAll(slashCommandsList, new DeleteSoundSlash(), new DisconnectSlash(), new DownloadSoundSlash(), new ListSlash(), 
                new PlaySlash(youtubeApiKey), new UploadSlash(), new TTSSlash(tts), new StopSlash(), new SetVoiceSlash(), new CustomizeSoundSlash(), new SoundboardSlash(), new GreetSlash());


        if(threadName.equals("beebot"))
            Collections.addAll(slashCommandsList, new RewardsSlash(), new LeaderboardSlash(), new LevelUpSlash(gs));

        if(threadName.equals("beebot canary"))
            Collections.addAll(slashCommandsList, new TalkSlash(), new LeaderboardSlash(), new LevelUpSlash(gs), new RewardsSlash());


        builder.addSlashCommands(slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]));
        
        CommandClient client = builder.build();
        
        if(!threadName.equals("beebot canary"))
            client.setListener(new CommandEventHandler(gs));
        jda.addEventListener(client);
        jda.addEventListener(new EventHandler(gs, PREFIX));
        jda.addEventListener(new EventButtonHandler());;

        if(Thread.currentThread().getName().equals("beebot")){
            jda.addEventListener(new EventHandlerBeebot(gs, farm));
            Connection c = new Connection(jda, gs, bs);
            c.start();
        }
        
        synchronized (this){
            try {wait();} 
            catch (InterruptedException e) {
                System.out.println("[" + Thread.currentThread().getName() + "] INFO Bot has been shutdown or something went wrong.");
                jda.shutdown();
                return;
            }
        }
    }
}