package com.safjnest.Commands.Misc;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.OpenAIHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;


/**
 * Gets a list of aliases for a command.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 1.1.02
 */
public class ChatGPT extends Command {
    
    /**
     * Default constructor for the class.
     */
    public ChatGPT() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

    /**
     * This method is called every time a member executes the command.
     */
    @Override
    protected void execute(CommandEvent event) {
        event.reply(OpenAIHandler.getAiService().createCompletion(OpenAIHandler.getCompletionRequest(event.getArgs())).getChoices().get(0).getText());
    }

}
