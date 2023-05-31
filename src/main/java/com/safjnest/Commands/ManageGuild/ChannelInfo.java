package com.safjnest.Commands.ManageGuild;

import java.awt.Color;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.safjnest.Utilities.Commands.CommandsHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
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
        GuildChannel gc = null;
        if(event.getMessage().getMentions().getChannels().size() > 0) 
            gc = event.getMessage().getMentions().getChannels().get(0);
        else if(event.getArgs() == "")
            gc = event.getGuildChannel();
        else if(SafJNest.longIsParsable(event.getArgs()))
            gc = event.getGuild().getGuildChannelById(event.getArgs());
        if(gc == null) {
            event.reply("Invalid channel id");
            return;
        }
        
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**CHANNEL INFO**");
        eb.setColor(Color.decode(BotSettingsHandler.map.get(event.getJDA().getSelfUser().getId()).color));

        eb.addField("Channel name", "```" + gc.getName() + "```", true);   
        eb.addField("Channel ID", "```" + gc.getId() + "```", true); 

        switch (gc.getType().toString()) {
            case "TEXT":
                TextChannel c = event.getGuild().getTextChannelById(gc.getId());

                eb.addField("Channel Topic", "```" 
                            + ((c.getTopic() == null)
                                ? "None"
                                : c.getTopic()) 
                            + "```", false); 

                eb.addField("Type", "```" + c.getType() + "```", true);

                eb.addField("Category", "```" 
                            + ((c.getParentCategory() != null)
                                ? c.getParentCategory().getName()
                                : "not under a category") 
                            + "```", true);
                break;

            case "VOICE":
                VoiceChannel v = event.getGuild().getVoiceChannelById(gc.getId()); 

                eb.addBlankField(true);

                eb.addField("BitRate ", "```" + v.getBitrate() / 1000 + "kbps```", true);  

                eb.addField("Number limit", "```" 
                            + v.getMembers().size() + "/"
                            + ((v.getUserLimit() == 0)
                                ? "∞"
                                : v.getUserLimit()) 
                            + "```", true);

                if(v.getMembers().size() != 0){
                    List<String> users = PermissionHandler.getMaxFieldableUserNames(v.getMembers(), 1024);
                    eb.addField("Members [" + v.getMembers().size() + "] " + "(Printed " + users.size() + ")", "```"
                            + (users.size() == 0
                                ? "empty"
                                : users.toString().substring(1, users.toString().length() - 1))
                            + "```", false);
                }
                else
                    eb.addField("Members", "```Channel is now empty```", false);
                
                eb.addField("Type", "```" + v.getType()+ "```", true);  

                eb.addField("Category", "```" + 
                            ((v.getParentCategory() != null)
                                ? v.getParentCategory().getName()
                                : "Its not under a category") 
                            + "```", true);  
                break;

            case "CATEGORY":
                net.dv8tion.jda.api.entities.channel.concrete.Category ct = event.getGuild().getCategoryById(gc.getId());

                eb.addField("Contains", "```" + ct.getChannels().size() + " channels" + "```", true);

                eb.addField("Type", "```" + ct.getType() + "```", true);
                break;

            case "STAGE":
                StageChannel sg = event.getGuild().getStageChannelById(gc.getId()); 

                eb.addField("BitRate ", "```" + sg.getBitrate()/1000 + "kbps```", false);  

                eb.addField("Number limit", "```" 
                            + sg.getMembers().size() + "/"
                            + ((sg.getUserLimit()==0)
                                ?"∞"
                                :sg.getUserLimit()) 
                            + "```", false);

                if(sg.getStageInstance() != null){
                    eb.addField("Is live", "```In live```", true);
                    if(sg.getStageInstance().getSpeakers()!=null){
                        List<String>users = PermissionHandler.getMaxFieldableUserNames(sg.getStageInstance().getSpeakers(), 1024);
                        eb.addField("Members ["
                        + sg.getStageInstance().getSpeakers().size() + "] "
                        + "(Printed " + users.size() + ")", "```"
                        + (users.size() == 0
                            ? "empty"
                            : users.toString().substring(1, users.toString().length() - 1))
                        + "```", false);
                    }
    
                    if(sg.getStageInstance().getTopic() != null)
                        eb.addField("Topic", "```" + sg.getStageInstance().getTopic() + "```", true);
                }
                else
                    eb.addField("Is live", "```Is not in live```", true);
            
                eb.addField("Type", "```" + sg.getType() + "```", true);

                eb.addField("Category", "```" + 
                            ((sg.getParentCategory() != null)
                                ? sg.getParentCategory().getName()
                                : "Its not under a category") 
                            + "```", true);
                break;

            case "NEWS":
                NewsChannel nw = event.getGuild().getNewsChannelById(gc.getId());

                eb.addField("Channel Topic", "```" 
                            + ((nw.getTopic() == null)
                                ? "None"
                                : nw.getTopic()) 
                            + "```", false); 

                eb.addField("Type", "```" + nw.getType() + "```", true);

                eb.addField("Category", "```" + 
                            ((nw.getParentCategory() != null)
                                ? nw.getParentCategory().getName()
                                : "Its not under a category") 
                            + "```", true);
                break;

            case "FORUM":
                ForumChannel fr = event.getGuild().getForumChannelById(gc.getId());

                eb.addField("Channel Topic", "```" 
                            + ((fr.getTopic()==null)
                                ?"None"
                                :fr.getTopic()) 
                            + "```", false); 

                eb.addField("Type", "```" + fr.getType() + "```", true);

                eb.addField("Category", "```" + 
                            ((fr.getParentCategory() != null)
                                ? fr.getParentCategory().getName()
                                : "Its not under a category") 
                            + "```", true);
                break;
            
            default:
                event.reply("Unknown channel type");
                break;
        }

        eb.addField("Channel created on", 
                      "<t:" + gc.getTimeCreated().toEpochSecond() + ":f> | "
                    + "<t:" + gc.getTimeCreated().toEpochSecond() + ":R>",
                     false);

        event.reply(eb.build());
	}
}