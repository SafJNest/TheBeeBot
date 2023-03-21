package com.safjnest.SlashCommands.ManageGuild;

import java.awt.Color;
import java.time.ZoneId;
import java.util.Arrays;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class ChannelInfoSlash extends SlashCommand {

    public ChannelInfoSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.CHANNEL, "channel", "Channel to get the information about", false));
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        TextChannel c = null;
        VoiceChannel v = null;
        String id = String.valueOf(event.getOption("channel").getAsChannel().getId());
        GuildChannel gc = event.getGuild().getGuildChannelById(id);
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**CHANNEL INFO**");
        eb.setColor(Color.decode(
            BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color
        ));
        if(gc.getType().isAudio()){
            v = event.getGuild().getVoiceChannelById(id);
            eb.addField("Channel name", "```" + v.getName() + "```", true);   
            eb.addField("Channel ID", "```" + v.getId() + "```", true);   

            eb.addField("BitRate ", "```" + v.getBitrate()/1000 + "kbps```", false);   
            eb.addField("Number limit", "```" 
                        + v.getMembers().size() 
                        +"/"
                        + ((v.getUserLimit()==0)
                            ?"∞"
                            :v.getUserLimit()) 
                        + "```", false);
            
            eb.addField("Type", "```" + v.getType()+ "```", true);   
            eb.addField("Category", "```" + v.getParentCategory().getName() + "```", true);   

            eb.addField("Created", "```" + v.getTimeCreated().atZoneSimilarLocal(ZoneId.of("Europe/Rome")) + " | " + DateHandler.formatDate(v.getTimeCreated())+ "```", false);
        }else{
            c = event.getGuild().getTextChannelById(gc.getId());
            eb.addField("Channel name", "```" + c.getName() + "```", true);   
            eb.addField("Channel ID", "```" + c.getId() + "```", true);   

            eb.addField("Channel Topic", "```" 
                       + ((c.getTopic()==null)
                            ?"None"
                            :c.getTopic()) 
                       + "```", false); 

            eb.addField("Type", "```" + c.getType() + "```", true);
            eb.addField("Category", "```" + c.getParentCategory().getName() + "```", true);

            eb.addField("Channel created on", "```" + DateHandler.formatDate(c.getTimeCreated()) + "```", false);
        } 
        event.deferReply(false).addEmbeds(eb.build()).queue();
	}
}