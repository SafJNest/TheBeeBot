package com.safjnest.SlashCommands.Audio;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class DisconnectSlash extends SlashCommand {

    public DisconnectSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "member", "Member to disconnect", false));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        if(event.getOption("member") == null) {
            event.getGuild().getAudioManager().closeAudioConnection();
            return;
        }

        Member mentionedMember = event.getOption("member").getAsMember();
        if(mentionedMember == null) { 
            event.deferReply(true).addContent("Couldn't find the specified member, please mention or write the id of a member").queue();
        }// if you mention a user not in the guild or write a wrong id

        event.getGuild().kickVoiceMember(mentionedMember).queue();
    }
        
}