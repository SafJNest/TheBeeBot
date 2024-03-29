package com.safjnest.Commands.Owner;

import java.nio.charset.StandardCharsets;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.CommandsLoader;
import com.safjnest.Utilities.TableHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;

import net.dv8tion.jda.api.utils.FileUpload;

/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.1
 */
public class Query extends Command{
    /**
     * Default constructor for the class.
     */
    public Query(){
        this.name = this.getClass().getSimpleName().toLowerCase();
        this.aliases = new CommandsLoader().getArray(this.name, "alias");
        this.help = new CommandsLoader().getString(this.name, "help");
        this.cooldown = new CommandsLoader().getCooldown(this.name);
        this.category = new Category(new CommandsLoader().getString(this.name, "category"));
        this.arguments = new CommandsLoader().getString(this.name, "arguments");
        this.ownerCommand = true;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String query = event.getArgs();

        query = query.replace("#guild", event.getGuild().getId());
        query = query.replace("#me", event.getAuthor().getId());
        query = query.replace("#channel", event.getChannel().getId());
        query = query.replace("#bot", event.getSelfMember().getId());

        QueryResult res = DatabaseHandler.safJQuery(query);

        if(res.isEmpty()) {
            event.reply("```No result```");
            return;
        }

        String[][] data = new String[res.size()][res.get(0).size()];
        for(int i = 0; i < res.size(); i++)
            data[i] = res.get(i).toArray();
        String[] headers = res.get(0).keySet().toArray(new String[0]);
        TableHandler.replaceIdsWithNames(data, event.getJDA());
        String table = TableHandler.constructTable(data, headers);

        event.getChannel().sendFiles(FileUpload.fromData(
            table.getBytes(StandardCharsets.UTF_8),
            "table.txt"
        )).queue();
    }
}