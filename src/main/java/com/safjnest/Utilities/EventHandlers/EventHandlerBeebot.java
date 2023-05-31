package com.safjnest.Utilities.EventHandlers;

import java.util.ArrayList;

import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * This class handles a few events that are only used by Beebot. These events:
 * <ul>
 * <li>On Message Received (for gaining exp)</li>
 * </ul>
 * 
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * 
 * @since 2.1
 */
public class EventHandlerBeebot extends ListenerAdapter {
    /**
     * The ExpSystem object that handles the exp system.
     */
    private ExpSystem farm;

    /**
     * Constructor for the TheListenerBeebot class.
     */
    public EventHandlerBeebot() {
        farm = new ExpSystem();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot())
            return;

        int lvl = farm.receiveMessage(e.getAuthor().getId(), e.getGuild().getId());
        if (lvl != -1) {
            User newGuy = e.getAuthor();
            String query = "SELECT message_text FROM levelup_message WHERE discord_id = '" + e.getGuild().getId() + "';";
            ArrayList<String> arr = DatabaseHandler.getSql().getSpecifiedRow(query, 0);
            if (arr == null){
                e.getChannel().asTextChannel().sendMessage("Congratulations, you are now level: " + lvl).queue();
                return;
            }
            String message = arr.get(0);
            message = message.replace("#user", newGuy.getAsMention());
            message = message.replace("#level", String.valueOf(lvl));
            e.getChannel().asTextChannel().sendMessage(message).queue();
        }

    }

}