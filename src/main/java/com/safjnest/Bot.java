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

import com.safjnest.Utilities.EventHandlers.CommandEventHandler;
import com.safjnest.Utilities.EventHandlers.EventButtonHandler;
import com.safjnest.Utilities.EventHandlers.EventHandler;
import com.safjnest.Utilities.EventHandlers.EventHandlerBeebot;
import com.safjnest.Utilities.Guild.GuildData;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Commands.Misc.*;
import com.safjnest.Commands.Owner.*;
import com.safjnest.Commands.Owner.Shutdown;
import com.safjnest.Commands.Queue.JumpTo;
import com.safjnest.Commands.Queue.Pause;
import com.safjnest.Commands.Queue.PlayYoutube;
import com.safjnest.Commands.Queue.PlayYoutubeForce;
import com.safjnest.Commands.Queue.Previous;
import com.safjnest.Commands.Queue.Queue;
import com.safjnest.Commands.Queue.Resume;
import com.safjnest.Commands.Queue.Skip;
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
import com.safjnest.SlashCommands.Queue.DownloadSoundSlash;
import com.safjnest.SlashCommands.Queue.JumpToSlash;
import com.safjnest.SlashCommands.Queue.PauseSlash;
import com.safjnest.SlashCommands.Queue.PreviousSlash;
import com.safjnest.SlashCommands.Queue.QueueSlash;
import com.safjnest.SlashCommands.Queue.ResumeSlash;
import com.safjnest.SlashCommands.Queue.SkipSlash;
import com.safjnest.SlashCommands.Settings.*;
import com.safjnest.SlashCommands.Settings.Boost.BoostSlash;
import com.safjnest.SlashCommands.Settings.Leave.LeaveSlash;
import com.safjnest.SlashCommands.Settings.LevelUp.LevelUpSlash;
import com.safjnest.SlashCommands.Settings.Reward.RewardSlash;
import com.safjnest.SlashCommands.Settings.Welcome.WelcomeSlash;

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
 * @version 4.0
 */
public class Bot extends ListenerAdapter {

    private static JDA jda;
    private static String PREFIX;
    private static String BOT_ID;
    private static String color;
    
    private Activity activity;
    private String ownerID;
    private String[] coOwnersIDs;
    private String helpWord;

    private String token;
    private String weatherApiKey;
    private String nasaApiKey;

    private int maxPrime;

    private static GuildSettings gs;

    /**
     * Where the magic happens.
     *
     */
    public void il_risveglio_della_bestia() {
        // fastest way to compile
        // ctrl c ctrl v
        // assembly:assembly -DdescriptorId=jar-with-dependencies

        JSONParser parser = new JSONParser();
        JSONObject settings = null, discordSettings = null, settingsSettings = null;

        String name = App.isExtremeTesting() ? "beebot canary" : "beebot";
        try (Reader reader = new FileReader("rsc" + File.separator + "settings.json")) {
            settings = (JSONObject) parser.parse(reader);
            discordSettings = (JSONObject) settings.get(name);
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
        weatherApiKey = settingsSettings.get("weatherApiKey").toString();
        nasaApiKey = settingsSettings.get("nasaApiKey").toString();

        jda = JDABuilder
            .createLight(token, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MODERATION)
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .setChunkingFilter(ChunkingFilter.ALL)
            .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.ACTIVITY)
            .build();

        BOT_ID = jda.getSelfUser().getId();

        gs = new GuildSettings();
        
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setHelpWord(helpWord);
        builder.setOwnerId(ownerID);
        builder.setCoOwnerIds(coOwnersIDs);
        builder.setActivity(activity);
        //builder.forceGuildOnly("608967318789160970");
        
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onReady(ReadyEvent event) {
                
                // for(Guild g : event.getJDA().getGuilds()){
                //     g.updateCommands().queue();
                // }
                
                RiotHandler.loadEmoji(event.getJDA());
                System.out.println("[INFO] custom emoji cached correctly");
                System.out.println("[INFO] no more guild cached correctly");
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

        if (App.isExtremeTesting()) {
            builder.setPrefixFunction(event -> {
                return PREFIX;
            });
        }

        ArrayList<Command> commandsList = new ArrayList<Command>();
        Collections.addAll(commandsList, new PrintCache(gs), new Ping(), new Ram(), new Help(gs), new Prefix(gs));

        Collections.addAll(commandsList, new Summoner(), new Augment(), new FreeChamp(), new Livegame(), 
            new LastMatches(), new Opgg(), new Calculator(), new Dice(), 
            new VandalizeServer(), new Jelly(), new Shutdown(), new Restart(), new Query());
  
        
        Collections.addAll(commandsList, new ChannelInfo(), new Clear(), new ServerInfo(), new MemberInfo(), new EmojiInfo(), 
            new InviteBot(), new ListGuild(), new Ban(), new Unban(), new Kick(), new Mute(), new UnMute(), new Image(), 
            new Permissions(), new ModifyNickname(), new RandomMove());

        
        Collections.addAll(commandsList, new Connect(), new Disconnect(), new List(), new ListUser(), 
            new PlayYoutube(), new PlaySound(), new TTS(), new Stop(), new Pause(), new Resume(), new Queue(), new Skip(), new Previous(), new PlayYoutubeForce(),
            new JumpTo()
        );
        
        
        Collections.addAll(commandsList, new Leaderboard(), new Test(gs));
    
        builder.addCommands(commandsList.toArray(new Command[commandsList.size()]));

        ArrayList<SlashCommand> slashCommandsList = new ArrayList<SlashCommand>();
        Collections.addAll(slashCommandsList, new PingSlash(), new BugSlash(), new HelpSlash(gs), new PrefixSlash(gs));

        
        Collections.addAll(slashCommandsList, new SummonerSlash(), new AugmentSlash(), new FreeChampSlash(), 
            new LivegameSlash(), new LastMatchesSlash(), 
            new PrimeSlash(maxPrime), new CalculatorSlash(), new DiceSlash(), new ChampionSlash(), new OpggSlash(), 
            new WeatherSlash(weatherApiKey), new APODSlash(nasaApiKey), new SpecialCharSlash()
        );
        
        
        Collections.addAll(slashCommandsList, new ChannelInfoSlash(), new ClearSlash(), new MsgSlash(), 
            new ServerInfoSlash(), new MemberInfoSlash(), new EmojiInfoSlash(), new InviteBotSlash(), new BanSlash(), 
            new UnbanSlash(), new KickSlash(), new MoveSlash(),new MuteSlash(), new UnMuteSlash(), new ImageSlash(), 
            new PermissionsSlash(), new ModifyNicknameSlash(), new WelcomeSlash(gs), new LeaveSlash(), new BoostSlash(), 
            new BlacklistSlash(gs)
        );

        
        Collections.addAll(slashCommandsList, new DeleteSoundSlash(), new DisconnectSlash(), new DownloadSoundSlash(), 
            new ListSlash(), new PlaySlash(), new UploadSlash(), new TTSSlash(), new StopSlash(), 
            new SetVoiceSlash(), new CustomizeSoundSlash(), new SoundboardSlash(), new GreetSlash(), new PauseSlash(), new ResumeSlash(),
            new QueueSlash(), new SkipSlash(), new PreviousSlash(), new JumpToSlash()
        );


        
        Collections.addAll(slashCommandsList, new RewardSlash(), new LeaderboardSlash(), new LevelUpSlash(gs));




        builder.addSlashCommands(slashCommandsList.toArray(new SlashCommand[slashCommandsList.size()]));
        
        CommandClient client = builder.build();
        
        if(!App.isExtremeTesting()) {
            client.setListener(new CommandEventHandler(gs));
        }

        jda.addEventListener(client);
        jda.addEventListener(new EventHandler(gs, PREFIX));
        jda.addEventListener(new EventButtonHandler());;

        if(!App.isExtremeTesting()){
            jda.addEventListener(new EventHandlerBeebot(gs));
            //Connection c = new Connection(jda, gs, bs);
            //c.start();
        }
        
    }


    public void distruzione_demoniaca(){
        jda.shutdown();
    }




    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return new String[0];
        
        String[] arr = new String[array.size()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = (String) array.get(i);
        return arr;
    }

    public static JDA getJDA() {
        return jda;
    }

    public static String getPrefix() {
        return PREFIX;
    }

    public static String getBotId() {
        return BOT_ID;
    }

    public static String getColor() {
        return color;
    }

    public static GuildSettings getGuildSettings() {
        return gs;
    }

    public static GuildData getGuildData(String id) {
        return gs.getServer(id);
    }
}