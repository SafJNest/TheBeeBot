package com.safjnest.Commands.ManageGuild;

import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.TableHandler;
import com.safjnest.Utilities.EXPSystem.ExpSystem;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

/**
 * @author <a href="https://github.com/NeuntronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.3
 */
public class Leaderboard extends Command {

    public Leaderboard() {
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
    }

    @Override
    protected void execute(CommandEvent event) {
        int limit = (SafJNest.intIsParsable(event.getArgs())) ? Integer.parseInt(event.getArgs()) : 10;

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
        for(int i = 0; i < splitTable.length; i++)
            event.reply("```" + splitTable[i] + "```");
    }
}