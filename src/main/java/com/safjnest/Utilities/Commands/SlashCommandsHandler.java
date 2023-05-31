package com.safjnest.Utilities.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.safjnest.SlashCommands.Admin.ListGuildSlash;
import com.safjnest.SlashCommands.Admin.PingSlash;
import com.safjnest.SlashCommands.Audio.*;
import com.safjnest.SlashCommands.LOL.*;
import com.safjnest.SlashCommands.ManageGuild.*;
import com.safjnest.SlashCommands.ManageMembers.*;
import com.safjnest.SlashCommands.Math.*;
import com.safjnest.SlashCommands.Misc.*;
import com.safjnest.SlashCommands.Settings.SetLeaveMessageSlash;
import com.safjnest.SlashCommands.Settings.SetLevelUpMessageSlash;
import com.safjnest.SlashCommands.Settings.SetPrefixSlash;
import com.safjnest.SlashCommands.Settings.SetSummonerSlash;
import com.safjnest.SlashCommands.Settings.SetVoiceSlash;
import com.safjnest.SlashCommands.Settings.SetWelcomeMessageSlash;
import com.safjnest.Utilities.SQL;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.tts.TTSHandler;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import no.stelar7.api.r4j.impl.R4J;

public class SlashCommandsHandler {

    private Map<String, SlashCommand> slashCommands = new HashMap<>();

    public SlashCommandsHandler(String threadName, String youtubeApiKey, HashMap<String,String> tierOneLink, TTSHandler tts, R4J riotApi, SQL sql, GuildSettings gs, int maxPrime) {
        if(!threadName.equals("beebot moderation")){
            slashCommands.put(new ConnectSlash().getName(), new ConnectSlash());
            slashCommands.put(new DeleteSoundSlash().getName(), new DeleteSoundSlash());
            slashCommands.put(new DisconnectSlash().getName(), new DisconnectSlash());
            slashCommands.put(new DownloadSoundSlash().getName(), new DownloadSoundSlash());
            slashCommands.put(new ListSlash().getName(), new ListSlash());
            slashCommands.put(new ListUserSlash().getName(), new ListUserSlash());
            slashCommands.put(new PlayYoutubeSlash(youtubeApiKey, tierOneLink).getName(), new PlayYoutubeSlash(youtubeApiKey, tierOneLink));
            slashCommands.put(new PlaySoundSlash().getName(), new PlaySoundSlash());
            slashCommands.put(new UploadSlash().getName(), new UploadSlash());
            slashCommands.put(new TTSSlash(tts).getName(), new TTSSlash(tts));
            slashCommands.put(new StopSlash().getName(), new StopSlash());
            slashCommands.put(new SetVoiceSlash().getName(), new SetVoiceSlash());
        }
        
        if(!threadName.equals("beebot music")){
            slashCommands.put(new ChannelInfoSlash().getName(), new ChannelInfoSlash());
            slashCommands.put(new ClearSlash().getName(), new ClearSlash());
            slashCommands.put(new ServerInfoSlash().getName(), new ServerInfoSlash());
            slashCommands.put(new MemberInfoSlash().getName(), new MemberInfoSlash());
            slashCommands.put(new EmojiInfoSlash().getName(), new EmojiInfoSlash());
            slashCommands.put(new SetWelcomeMessageSlash(sql).getName(), new SetWelcomeMessageSlash(sql));
            slashCommands.put(new SetLeaveMessageSlash(sql).getName(), new SetLeaveMessageSlash(sql));
            slashCommands.put(new SetLevelUpMessageSlash().getName(), new SetLevelUpMessageSlash());
            slashCommands.put(new LeaderboardSlash().getName(), new LeaderboardSlash());
            
            slashCommands.put(new BanSlash().getName(), new BanSlash());
            slashCommands.put(new UnbanSlash().getName(), new UnbanSlash());
            slashCommands.put(new KickSlash().getName(), new KickSlash());
            slashCommands.put(new MoveSlash().getName(), new MoveSlash());
            slashCommands.put(new MoveChannelSlash().getName(), new MoveChannelSlash());
            slashCommands.put(new MuteSlash().getName(), new MuteSlash());
            slashCommands.put(new UnMuteSlash().getName(), new UnMuteSlash());
            slashCommands.put(new ImageSlash().getName(), new ImageSlash());
            slashCommands.put(new PermissionsSlash().getName(), new PermissionsSlash());
            slashCommands.put(new ModifyNicknameSlash().getName(), new ModifyNicknameSlash());
        }

        if(!threadName.equals("beebot music") && !threadName.equals("beebot moderation")){
            slashCommands.put(new SummonerSlash().getName(), new SummonerSlash());
            slashCommands.put(new FreeChampSlash().getName(), new FreeChampSlash());
            slashCommands.put(new GameRankSlash(riotApi, sql).getName(), new GameRankSlash(riotApi, sql));
            slashCommands.put(new LastMatchesSlash(riotApi, sql).getName(), new LastMatchesSlash(riotApi, sql));
            slashCommands.put(new RuneSlash().getName(), new RuneSlash());
            slashCommands.put(new SetSummonerSlash(riotApi, sql).getName(), new SetSummonerSlash(riotApi, sql));
            slashCommands.put(new PrimeSlash(maxPrime).getName(), new PrimeSlash(maxPrime));
            slashCommands.put(new DiceSlash().getName(), new DiceSlash());
            slashCommands.put(new CalculatorSlash().getName(), new CalculatorSlash());
        }

        slashCommands.put(new SetPrefixSlash(sql, gs).getName(), new SetPrefixSlash(sql, gs));
        slashCommands.put(new PingSlash().getName(), new PingSlash());
        slashCommands.put(new ListGuildSlash().getName(), new ListGuildSlash());
        slashCommands.put(new BugsNotifierSlash().getName(), new BugsNotifierSlash());
        slashCommands.put(new HelpSlash(gs, slashCommands).getName(), new HelpSlash(gs, slashCommands));
        slashCommands.put(new MsgSlash().getName(), new MsgSlash());
        slashCommands.put(new InviteBotSlash().getName(), new InviteBotSlash());
        slashCommands.put(new AnonymSlash().getName(), new AnonymSlash());
    }

    /**
     * Useless method but
     * {@link <a href="https://github.com/NeutronSun">NeutronSun</a>} is one
     * of the biggest bellsprout ever made
     */
    public void doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed() {
        return;
    }
    
    public SlashCommand getCommand(String name) {
        return slashCommands.get(name);
    }

    public Map<String, SlashCommand> getCommands() {
        return slashCommands;
    }

    public Collection<CommandData> getCommandData() {
        Collection<CommandData> commandDataList = new ArrayList<>();
        for (SlashCommand command : slashCommands.values()) {
            commandDataList.add(command.buildCommandData());
        }
        return commandDataList;
    }
    
}
