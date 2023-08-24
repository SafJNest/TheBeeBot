package com.safjnest.SlashCommands.ManageMembers;

import java.awt.Color;
import java.util.Arrays;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class UnbanSlash extends SlashCommand{

    public UnbanSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "user", "User ID to unbun, null to get list of banned users", false));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        try {
            if(event.getOption("user") == null){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("List of banned users");
                for (net.dv8tion.jda.api.entities.Guild.Ban ban : event.getGuild().retrieveBanList().complete())
                    eb.appendDescription(ban.getUser().getAsMention() + " - ");
                    eb.setColor(Color.decode(
                        BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
                    ));
                eb.setAuthor(event.getJDA().getSelfUser().getName(), "https://github.com/SafJNest",event.getJDA().getSelfUser().getAvatarUrl());
                eb.setFooter("*This is not SoundFx, this is much worse cit. steve jobs (probably)", null);
                event.deferReply(true).addEmbeds(eb.build()).queue();
            }

            else{
                final User surelyTheGuy = event.getJDA().retrieveUserById(event.getOption("user").getAsString()).complete();
                if (!event.getGuild().getMember(event.getJDA().getSelfUser()).hasPermission(Permission.BAN_MEMBERS))
                    event.deferReply(true).addContent(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to unban").queue();

                else if (PermissionHandler.hasPermission(event.getMember(), Permission.BAN_MEMBERS)) {
                    event.getGuild().unban(surelyTheGuy).queue(
                                                            (e) -> event.deferReply(false).addContent("Unbanned " + surelyTheGuy.getAsMention()).queue(), 
                                                            new ErrorHandler().handle(
                                                                ErrorResponse.MISSING_PERMISSIONS,
                                                                    (e) -> event.deferReply(true).addContent("sorry, " + e.getMessage()).queue())
                    );
                }
                else 
                    event.deferReply(true).addContent(event.getJDA().getSelfUser().getAsMention() + " you dont have permission to unban").queue();
            }
        } catch (Exception e) {
            event.deferReply(true).addContent("sorry, " + e.getMessage()).queue();
        }
    }
}