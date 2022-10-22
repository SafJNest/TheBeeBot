package com.safjnest.Commands.ManageGuild;

import java.awt.Color;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.DateHandler;
import com.safjnest.App;
import com.safjnest.Utilities.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class ChannelInfo extends Command {

    public ChannelInfo(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsHandler().getArray(this.name, "alias");
        this.help = new CommandsHandler().getString(this.name, "help");
        this.cooldown = new CommandsHandler().getCooldown(this.name);
        this.category = new Category(new CommandsHandler().getString(this.name, "category"));
        this.arguments = new CommandsHandler().getString(this.name, "arguments");
    }

	@Override
	protected void execute(CommandEvent event) {
        TextChannel c = null;
        VoiceChannel v = null;
        GuildChannel gc = null;
        if(event.getMessage().getMentions().getChannels().size() > 0) 
            gc = event.getMessage().getMentions().getChannels().get(0);
        else if(event.getArgs() != null) 
            gc = event.getGuild().getGuildChannelById(event.getArgs());
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**CHANNEL INFO**");
        eb.setColor(Color.decode(App.color));
        if(gc.getType().isAudio()){
            v = event.getGuild().getVoiceChannelById(event.getArgs());
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

            eb.addField("Created", "```" + DateHandler.formatDate(v.getTimeCreated())+ "```", false);
        }else{
            c = event.getGuild().getTextChannelById(gc.getId());
            eb.addField("Channel name", "```" + c.getName() + "```", true);   
            eb.addField("Channel ID", "```" + c.getId() + "```", true);   

            eb.addField("Channel Topic", "```" 
                       + ((c.getTopic()==null)
                            ?"None"
                            :c.getTopic()) 
                       + "```", false); 

            eb.addField("Type", "```" + c.getType()+ "```", true);   
            eb.addField("Category", "```" + c.getParentCategory().getName() + "```", true);   

            eb.addField("Channel created on", "```" + DateHandler.formatDate(c.getTimeCreated()) + "```", false);
        } 
        event.reply(eb.build());
	}
}