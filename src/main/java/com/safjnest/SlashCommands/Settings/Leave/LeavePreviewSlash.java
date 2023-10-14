package com.safjnest.SlashCommands.Settings.Leave;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.ResultRow;

public class LeavePreviewSlash extends SlashCommand{

    public LeavePreviewSlash(String father){
        this.name = this.getClass().getSimpleName().replace("Slash", "").replace(father, "").toLowerCase();
        this.help = new CommandsLoader().getString(name, "help", father.toLowerCase());
        this.cooldown = new CommandsLoader().getCooldown(this.name, father.toLowerCase());
        this.category = new Category(new CommandsLoader().getString(father.toLowerCase(), "category"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String guildId = event.getGuild().getId();
        String botId = event.getJDA().getSelfUser().getId();

        ResultRow leave = DatabaseHandler.getLeave(guildId, botId);

        if(leave.get("leave_message") == null) {
            event.deferReply(true).addContent("This guild doesn't have a leave message.").queue();
            return;
        }

        String leaveMessage = leave.get("leave_message").replace("#user", event.getUser().getAsMention());
        leaveMessage = leaveMessage + "\nThis message would be sent to <#" + leave.get("leave_channel") + ">";

        event.deferReply(false).addContent(leaveMessage).queue();
    }
}