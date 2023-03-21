package com.safjnest.SlashCommands.ManageGuild;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.CommandsHandler;
import java.awt.Color;
import java.util.Arrays;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class EmojiInfoSlash extends SlashCommand {

    public EmojiInfoSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "idemoji", "ID Emoji", true));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        CustomEmoji em = null;
        try {
            em = event.getGuild().getEmojiById(event.getOption("idemoji").getAsString());
        } catch (Exception e) {
            event.deferReply(true).addContent("Emote not found").queue();
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        eb.setTitle(":laughing: "+"**EMOJI INFO**"+" :laughing:");
        eb.setThumbnail(em.getImageUrl());
        eb.addField("**Name**", "```" + em.getName() + "```", true);   
        eb.addField("**Emoji ID**", "```" + em.getId() + "```", true); 
        eb.addField("**GIF?**",
        (em.isAnimated())
            ?"```✅ Yes```"
            :"```❌ No```"
        , true);
        eb.addField("**Emoji URL**", em.getImageUrl(), false);   
        eb.addField("Emoji created on (dd/mm/yyyy)", "```" + DateHandler.formatDate(em.getTimeCreated()) + "```", false);
        event.deferReply(false).addEmbeds(eb.build()).queue();
	}
}