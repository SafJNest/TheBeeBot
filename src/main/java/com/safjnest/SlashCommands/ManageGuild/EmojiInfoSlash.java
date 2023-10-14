package com.safjnest.SlashCommands.ManageGuild;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.Bot.BotSettingsHandler;

import java.awt.Color;
import java.util.Arrays;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class EmojiInfoSlash extends SlashCommand {

    public EmojiInfoSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "name", "Emoji/Sticker to get information on", true));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        CustomEmoji em = null;
        Sticker sticker = null;
        boolean isSticker = false;
        try {
            if(!event.getOption("name").getAsString().startsWith("<"))
                throw new Exception();
            String id = "";
            id = event.getOption("name").getAsString().substring(event.getOption("name").getAsString().lastIndexOf(":")+1, event.getOption("name").getAsString().length()-1);
            em = event.getGuild().getEmojiById(id);
        } catch (Exception e) {
            try {
                sticker = event.getGuild().getStickersByName(event.getOption("name").getAsString(), true).get(0);
                isSticker = true;
            } catch (Exception e1) {
                event.reply("Couldn't find the Emoji/Sticker. Remembert to write the emoji in the correct format :emojiname:.");
            }
        }
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));

        if(isSticker){
            eb.setTitle(":laughing: "+"**STICKER INFO**"+" :laughing:");
            eb.setThumbnail(sticker.getIconUrl());
            eb.addField("**Name**", "```" + sticker.getName() + "```", true);   
            eb.addField("**Emoji ID**", "```" + sticker.getId() + "```", true);
            eb.addField("**APNG?**",
            (sticker.getFormatType().name().equals("APNG"))
                ?"```✅ Yes```"
                :"```❌ No - "+sticker.getFormatType().name()+"```"
            , true);
            eb.addField("**Emoji URL**", sticker.getIconUrl(), false);   
            eb.addField("Emoji created on", 
                          "<t:" + sticker.getTimeCreated().toEpochSecond() + ":f> | "
                        + "<t:" + sticker.getTimeCreated().toEpochSecond() + ":R>",
                        false);
        }
        else{
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
            eb.addField("Emoji created on", 
                          "<t:" + em.getTimeCreated().toEpochSecond() + ":f> | "
                        + "<t:" + em.getTimeCreated().toEpochSecond() + ":R>",
                        false);
        }
        event.deferReply(false).addEmbeds(eb.build()).queue();
    }
}