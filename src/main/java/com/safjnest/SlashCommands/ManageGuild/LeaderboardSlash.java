package com.safjnest.SlashCommands.ManageGuild;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.TableHandler;

import java.util.Arrays;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.0
 */
public class LeaderboardSlash extends SlashCommand {

    public LeaderboardSlash(){
        this.name = this.getClass().getSimpleName().replace("Slash", "").toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.options = Arrays.asList(
            new OptionData(OptionType.INTEGER, "limit", "positions limit", false)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        int limit = (event.getOption("limit") != null) ? event.getOption("limit").getAsInt() : 10;

        QueryResult users = DatabaseHandler.getUsersByExp(event.getGuild().getId(), limit);

        if(users.isEmpty()) {
            event.reply("```No Results```");
            return;
        }

        String[][] databaseData = new String[users.size()-1][users.get(0).size()];
        for(int i = 1; i < users.size(); i++)
            databaseData[i-1] = users.get(i).toArray();
        int rows = databaseData.length;
        int columns = databaseData[0].length + 1;
        String[][] data = new String[rows][columns];
        String[] headers = {"#", "user", "level", "progress", "messages"};
        int lvl, exp;

        for(int i = 0; i < rows; i++) {
            data[i][0] = String.valueOf(i+1);

            data[i][1] = databaseData[i][0];

            lvl = Integer.parseInt(databaseData[i][2]);
            exp = Integer.parseInt(databaseData[i][3]);
            data[i][2] = String.valueOf(lvl);
            data[i][3] = ExpSystem.getLvlUpPercentage(lvl, exp) + "% (" + ExpSystem.getExpToLvlUp(lvl, exp) + "/" + ExpSystem.getExpToReachLvl(lvl) + ") ";
            data[i][4] = databaseData[i][1];
        }

        TableHandler.replaceIdsWithNames(data, event.getJDA());

        String table = TableHandler.constructTable(data, headers);

        String[] splitTable = TableHandler.splitTable(table);

        event.reply(event.getGuild().getName() + " leaderboard:");

        event.deferReply(false).addContent(event.getGuild().getName() + " leaderboard:").queue();
        for(int i = 0; i < splitTable.length; i++)
            event.getChannel().sendMessage("```" + splitTable[i] + "```").queue();
	}
}