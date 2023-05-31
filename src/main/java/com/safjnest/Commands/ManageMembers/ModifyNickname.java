package com.safjnest.Commands.ManageMembers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class ModifyNickname extends Command {
    /**
     * Default constructor for the class.
     */
    public ModifyNickname() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        Member theGuy;
        String[] args = event.getArgs().split(" ", 3);
        
        if(args.length < 2)
            return;
        else if(event.getMessage().getMentions().getMembers().size() > 0)
            theGuy = event.getMessage().getMentions().getMembers().get(0);
        else
            theGuy = event.getGuild().getMemberById(args[0]);
        
        try {
            theGuy.modifyNickname(args[1]).queue(
            (e) -> event.reply("Changed nickname of " + theGuy.getAsMention() + " exectued"), 
            new ErrorHandler().handle(
                ErrorResponse.MISSING_PERMISSIONS,
                    (e) -> event.replyError("Sorry, " + e.getMessage()))
            );
        } catch (Exception e) {
            event.replyError("Sorry, " + e.getMessage());
        }
    }
}