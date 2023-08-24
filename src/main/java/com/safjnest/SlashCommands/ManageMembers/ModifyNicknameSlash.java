package com.safjnest.SlashCommands.ManageMembers;

import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class ModifyNicknameSlash extends SlashCommand {
    /**
     * Default constructor for the class.
     */
    public ModifyNicknameSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.USER, "user", "User to change the nickname", true),
            new OptionData(OptionType.STRING, "nick","New Nickname", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        Member theGuy = event.getOption("user").getAsMember();
        
        try {
            theGuy.modifyNickname(event.getOption("nick").getAsString()).queue(
            (e) -> event.deferReply(false).addContent("Changed nickname of " + theGuy.getAsMention() + " exectued").queue(), 
            new ErrorHandler().handle(
                ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> event.deferReply(true).addContent("Sorry, " + e.getMessage()).queue())
            );
        } catch (Exception e) {
            event.deferReply(true).addContent("Sorry, " + e.getMessage()).queue();
        }
    }
}