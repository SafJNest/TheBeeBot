package com.safjnest.SlashCommands.Settings.Welcome;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;

public class WelcomePreviewSlash extends SlashCommand{

    public WelcomePreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        ResultRow welcome = DatabaseHandler.getWelcome(guildId, botId);

        if(welcome.get("welcome_message") == null) {
            event.deferReply(true).addContent("This guild doesn't have a welcome message.").queue();
            return;
        }

        String welcomeMessage = welcome.get("welcome_message").replace("#user", event.getUser().getAsMention());
        welcomeMessage = welcomeMessage + "\nThis message would be sent to <#" + welcome.get("welcome_channel") + ">";

        if(welcome.get("welcome_role") != null){
            welcomeMessage += "\nRoles that would be given to the user:" + event.getGuild().getRoleById(welcome.get("welcome_role")).getName();
        }

        event.deferReply(false).addContent(welcomeMessage).queue();
    }
    
}
