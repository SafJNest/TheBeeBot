package com.safjnest.SlashCommands.ManageGuild;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.TableHandler;

import java.util.ArrayList;
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
            new OptionData(OptionType.INTEGER, "limit", "row limit", false)
        );
    }

	@Override
	protected void execute(SlashCommandEvent event) {
        int limit = (event.getOption("limit") != null) ? event.getOption("limit").getAsInt() : 10;

        String query = "SELECT user_id, messages, level, exp from exp_table WHERE guild_id = '" + event.getGuild().getId() + "' order by exp DESC limit " + limit + ";";
        ArrayList<ArrayList<String>> res = DatabaseHandler.getSql().getAllRows(query);
        String[][] databaseData = new String[res.size()-1][res.get(0).size()];
        for(int i = 1; i < res.size(); i++)
            databaseData[i-1] = res.get(i).toArray(new String[0]);
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
            data[i][3] = Math.round((float)ExpSystem.expToLvlUp(lvl, exp)/(float)(ExpSystem.totalExpToLvlUp(lvl + 1) - ExpSystem.totalExpToLvlUp(lvl))*100) + "% (" + ExpSystem.expToLvlUp(lvl, exp) + "/" + (ExpSystem.totalExpToLvlUp(lvl + 1) - ExpSystem.totalExpToLvlUp(lvl)) + ") ";

            data[i][4] = databaseData[i][1];
        }

        TableHandler.replaceIdsWithNames(data, event.getJDA());

        String table = TableHandler.constructTable(data, headers);

        String[] splitTable = TableHandler.splitTable(table);
        event.deferReply(false).addContent("Here the full table:").queue();
         for(int i = 0; i < splitTable.length; i++)
            event.getChannel().sendMessage("```" + splitTable[i] + "```").queue();
	}
}