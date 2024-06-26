package com.safjnest.Commands.ManageGuild;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Bot;
import com.safjnest.Utilities.CommandsLoader;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.sticker.Sticker;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class EmojiInfo extends Command {

    public EmojiInfo(){
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        String args = event.getArgs();
        String idEmoji = null;
        CustomEmoji em = null;
        Sticker sticker = null;
        boolean isSticker = false;

        try {
            idEmoji = args.substring(args.lastIndexOf(":")+1, args.length()-1);
            em = event.getGuild().getEmojiById(idEmoji);
        } catch (Exception e) {
            try {
                sticker = event.getGuild().getStickersByName(event.getArgs(), true).get(0);
                isSticker = true;
            } catch (Exception e1) {
                event.reply("Couldn't find the Emote/Sticker. Remember to insert ':' at the start/end of the name.");
                return;
            }
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Color.decode(Bot.getColor()));

        if(isSticker) {
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
        else {
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
        event.reply(eb.build());
	}
}