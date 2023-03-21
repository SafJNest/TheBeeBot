package com.safjnest.Commands.Dangerous;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
/**
 * 
 * @author <a href="https://github.com/Leon412">Leon412</a> 
 * @since 1.3
 */
public class Jelly extends Command {
    /**
     * Default constructor for the class.
     */
    public Jelly() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Member theGuy;
        String[] args = event.getArgs().split(" ", 2);
        
        if(args[0].equals(""))
            theGuy = event.getMember();
        else if(event.getMessage().getMentions().getMembers().size() > 0)
            theGuy = event.getMessage().getMentions().getMembers().get(0);
        else
            theGuy = event.getGuild().getMemberById(args[0]);
        
        if(((theGuy.getNickname() == null) ? theGuy.getUser().getName() : theGuy.getNickname()).toLowerCase().endsWith("wx"))
            event.reply(theGuy.getAsMention() + " fa già parte del team di jelly");
        else{
            try {
                theGuy.modifyNickname(((theGuy.getNickname() == null) ? theGuy.getUser().getName() : theGuy.getNickname()) + "WX").queue(
                (e) -> event.reply(theGuy.getAsMention() + " has been jellified blblblblblblblbllblblblblbllbblblbllbblblblblblbllbblblblbblblblbbllblblblblblblblblblblbblbllbblbllblbbllblblblblblbblblbllllblblbblblblblblblbllblblblblbllbblblbllbblblblblblbllbblblblbblblblbbllblblblblblblblblblblbblbllbblbllblbbllblblblblblbblblbllllblblbblblblblblblbllblblblblbllbblblbllbblblblblblbllbblblblbblblblbbllblblblblblblblblblblbblbllbblbllblbbllblblblblblbblblbllllblblb"), 
                new ErrorHandler().handle(
                    ErrorResponse.MISSING_PERMISSIONS,
                        (e) -> event.replyError("Sorry, " + e.getMessage()))
                );
            } catch (Exception e) {
                event.replyError("Sorry, " + e.getMessage());
            }
        }
    }
}