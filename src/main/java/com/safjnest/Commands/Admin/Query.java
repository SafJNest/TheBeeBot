package com.safjnest.Commands.Admin;

import java.util.ArrayList;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.DatabaseHandler;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.TableHandler;


public class Query extends Command{
    /**
     * Default constructor for the class.
     */
    public Query(){
        this.name = this.getClass().getSimpleName();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent e) {
        if(!PermissionHandler.isUntouchable(e.getAuthor().getId())){
            e.getAuthor().openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessage("figlio di troia non mi fai sql injectohnbjn ").queue());
            return;
        }

        String query = e.getArgs();
        if(query.equals("")){
            e.reply("Please specify a query to execute.");
            return;
        }

        ArrayList<ArrayList<String>> res = DatabaseHandler.getSql().getAllRows(query);
        String[][] data = new String[res.size()-1][res.get(0).size()];
        for(int i = 1; i < res.size(); i++)
            data[i-1] = res.get(i).toArray(new String[0]);
        String[] headers = res.get(0).toArray(new String[0]);
        
        TableHandler.replaceIdsWithNames(data, e.getJDA());

        String table = TableHandler.constructTable(data, headers);

        String[] splitTable = TableHandler.splitTable(table);

        for(int i = 0; i < splitTable.length; i++)
            e.reply("```" + splitTable[i] + "```");
    }
}