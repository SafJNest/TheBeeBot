package com.safjnest.SlashCommands.ManageGuild;

import java.awt.Color;
import java.util.ArrayList;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.Bot.BotSettingsHandler;
import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class RewardsSlash extends SlashCommand {

    public RewardsSlash() {
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        
        //event.replyEmbeds(RewardsSlash.createEmbed(event)).addComponents(ActionRow.of(buttons)).queue();
        //event.replyModal(modal).queue();
        event.deferReply(false).and(createEmbed(event.getHook(), event.getGuild())).queue();
	}

    public static WebhookMessageEditAction<Message> createEmbed(InteractionHook hook,  Guild g){
        Button add = Button.success("rewards-add", "+");
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(add);
        //create an embed
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Rewards");
        eb.setThumbnail(g.getIconUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(hook.getJDA().getSelfUser().getId()).color));
        eb.setDescription("Rack up those levels and score awesome rewards! Leveling up in this server unlocks a treasure trove of cool perks and surprises. Keep grinding!\n**This is just for admin: click on + to add a reward or click on a reward to delete it.**");
        String query = "SELECT role_id, level FROM rewards_table WHERE guild_id = '" + g.getId() + "';";
        ArrayList<ArrayList<String>> rewards = DatabaseHandler.getSql().getAllRows(query, 2);
        if(rewards.size() == 0) {
            eb.addField("No rewards", "There are no rewards set up for this server. You can set up rewards with the /addreward command.", false);
        }else{
            for(ArrayList<String> reward : rewards) {
                buttons.add(Button.primary("rewards-role-" + reward.get(0), reward.get(1)));
                eb.addField(g.getRoleById(reward.get(0)).getName(), "Level: " + reward.get(1), false);
            }
        }
        eb.setFooter("If you delete a role the reward will be deleted in our database too.");
        return hook.editOriginalEmbeds(eb.build()).setActionRow(buttons);
        //return event.replyEmbeds(eb.build()).addComponents(ActionRow.of(buttons));
    }

        public static MessageEditAction createEmbed(Message message,  Guild g){
        Button add = Button.success("rewards-add", "+");
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(add);
        //create an embed
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Rewards");
        eb.setThumbnail(g.getIconUrl());
        eb.setColor(Color.decode(BotSettingsHandler.map.get(message.getJDA().getSelfUser().getId()).color));
        eb.setDescription("Rack up those levels and score awesome rewards! Leveling up in this server unlocks a treasure trove of cool perks and surprises. Keep grinding!\n**This is just for admin: click on + to add a reward or click on a reward to delete it.**");
        String query = "SELECT role_id, level FROM rewards_table WHERE guild_id = '" + g.getId() + "';";
        ArrayList<ArrayList<String>> rewards = DatabaseHandler.getSql().getAllRows(query, 2);
        if(rewards.size() == 0) {
            eb.addField("No rewards", "There are no rewards set up for this server. You can set up rewards with the /addreward command.", false);
        }else{
            for(ArrayList<String> reward : rewards) {
                buttons.add(Button.primary("rewards-role-" + reward.get(0), reward.get(1)));
                eb.addField(g.getRoleById(reward.get(0)).getName(), "Level: " + reward.get(1), false);
            }
        }
        eb.setFooter("If you delete a role the reward will be deleted in our database too.");  
        return message.editMessageEmbeds(eb.build()).setActionRow(buttons);
    }
}