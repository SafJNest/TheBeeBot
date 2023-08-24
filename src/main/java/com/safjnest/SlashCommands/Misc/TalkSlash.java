package com.safjnest.SlashCommands.Misc;

import java.util.Arrays;

import org.nikki.omegle.Omegle;
import org.nikki.omegle.core.OmegleException;
import org.nikki.omegle.core.OmegleMode;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.event.OmegleEventAdaptor;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.CommandsLoader;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TalkSlash extends SlashCommand {

    public TalkSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.STRING, "lang", "language (english by default)", false)
                    .addChoice("Deutsch", "de")
                    .addChoice("English", "en")
                    .addChoice("Espagnol", "es")
                    .addChoice("Francois", "fr")
                    .addChoice("Italiano", "it")
                    .addChoice("Norsk", "no")
                    .addChoice("Suomi", "fi")
                    .addChoice("Svenska", "sv"),
            new OptionData(OptionType.STRING, "interests", "common interests separated by a comma (anime, manga)", false),
            new OptionData(OptionType.BOOLEAN, "party-mode", "if party mode is true everyone in the discord channel can talk (false by default)", false));
    }

    public void terminator2LaRivolta(SlashCommandEvent event, MessageChannel channel) {
        for(Object keria : event.getJDA().getRegisteredListeners()){
            if(keria.getClass().getSimpleName().equals("OmegleListener") && ((OmegleListener) keria).getChannel() == channel) {
                event.getJDA().removeEventListener((OmegleListener) keria);
                return;
            }
        }
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        MessageChannel channel = event.getChannel();

        String[] interests = null;
        String interestsString = "";

        String lang = "en";

        boolean party = false;

        for(Object keria : event.getJDA().getRegisteredListeners()){
            if(keria.getClass().getSimpleName().equals("OmegleListener") && ((OmegleListener) keria).getChannel() == channel) {
                event.deferReply(true).addContent("This channel is busy").queue();
                return;
            }
        }

        if(event.getOption("interests") != null){
            interests = event.getOption("interests").getAsString().split(",");
            interestsString += "[";
            for(String interest : interests){
                interestsString += "\"" + interest.trim() + "\"" + ", ";
            }
            interestsString = interestsString.substring(0, interestsString.length() - 2) ;
            interestsString += "]";
        }

        if(event.getOption("lang") != null){
            lang = event.getOption("lang").getAsString();
        }

        if(event.getOption("party-mode") != null) {
            party = event.getOption("party-mode").getAsBoolean();
        }

        Omegle omegle = new Omegle();
		try {
			System.out.println("Opening omegle session... (type quit to quit)");

			OmegleSession session = omegle.openSessionSafJ(OmegleMode.NORMAL, lang, interestsString,  new OmegleEventAdaptor() {
				@Override
				public void chatWaiting(OmegleSession session) {
                    channel.sendMessage("Waiting for chat...").queue();
				}

				@Override
				public void chatConnected(OmegleSession session) {
                    channel.sendMessage("You are now talking to a random stranger!").queue();
				}

				@Override
				public void chatMessage(OmegleSession session, String message) {
                    channel.sendMessage("Stranger: " + message).queue();
				}

				@Override
				public void messageSent(OmegleSession session, String string) {
					//System.out.println("You: " + string);
				}

				@Override
				public void strangerDisconnected(OmegleSession session) {
					System.out.println("Stranger disconnected");
                    channel.sendMessage("Stranger disconnected, goodbye and go fuck yourself!").queue();
                    terminator2LaRivolta(event, channel);
                    omegle.removeSession(session);
				}

                @Override
				public void chatDisconnected(OmegleSession session) {
					System.out.println("user disconnected");
                    channel.sendMessage("You disconnected, goodbye and go fuck yourself!").queue();
                    terminator2LaRivolta(event, channel);
                    omegle.removeSession(session);
				}   

				@Override
				public void omegleError(OmegleSession session, String string) {
					System.out.println("ERROR! " + string);
                    channel.sendMessage("ERROR! " + string).queue();
                    terminator2LaRivolta(event, channel);
                    omegle.removeSession(session);
				}
			});

            event.deferReply(false).addContent("Opening session...").queue();
            OmegleListener omegleListener = new OmegleListener(event, channel, session, party);
            event.getJDA().addEventListener(omegleListener);

		} catch (OmegleException e) {
			e.printStackTrace();
		}
    }
}

class OmegleListener extends ListenerAdapter {
    private MessageChannel channel;
    private User author;
    private OmegleSession session;
    private boolean party;

    public OmegleListener(SlashCommandEvent event, MessageChannel channel, OmegleSession session, boolean party){
        this.channel = channel;
        this.session = session;
        this.author = event.getUser();
        this.party = party;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(!e.getChannel().equals(channel) || e.getAuthor().isBot() || (!party && e.getAuthor() != author)){
            return;
        }
        try {
            if(e.getMessage().getContentRaw().equals("quit"))
                session.disconnect();
            if(party)
                session.send(" <" + e.getMember().getNickname() + "> " + e.getMessage().getContentRaw(), true);
            else
                session.send(e.getMessage().getContentRaw(), true);
        } catch (OmegleException e1) {
            e1.printStackTrace();
        }
    }
}