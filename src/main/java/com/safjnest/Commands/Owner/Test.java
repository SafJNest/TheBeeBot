package com.safjnest.Commands.Owner;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.safjnest.Utilities.PermissionHandler;
import com.safjnest.Utilities.SafJNest;
import com.safjnest.Utilities.Guild.BlacklistData;
import com.safjnest.Utilities.Guild.ChannelData;
import com.safjnest.Utilities.Guild.GuildSettings;
import com.safjnest.Utilities.Guild.Alert.AlertData;
import com.safjnest.Utilities.Guild.Alert.AlertKey;
import com.safjnest.Utilities.Guild.Alert.AlertType;
import com.safjnest.Utilities.LOL.RiotHandler;
import com.safjnest.Utilities.SQL.DatabaseHandler;
import com.safjnest.Utilities.SQL.QueryResult;
import com.safjnest.Utilities.SQL.ResultRow;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import no.stelar7.api.r4j.pojo.lol.staticdata.item.Item;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @author <a href="https://github.com/Leon412">Leon412</a>
 * 
 * @since 1.3
 */
public class Test extends Command{

    private GuildSettings gs;

    public Test(GuildSettings gs){
        this.name = "test";
        this.aliases = new String[]{"wip"};
        this.help = "";
        this.category = new Category("Owner");
        this.arguments = "faker";
        this.ownerCommand = true;
        this.hidden = true;
        this.gs = gs;
    }

    @Override
    protected void execute(CommandEvent e) {
        String[] args = e.getArgs().split(" ", 2);
        String[] bots = {"938487470339801169", "983315338886279229", "939876818465488926", "1098906798016184422", "1074276395640954942"};
        QueryResult res;
        if(args.length == 0 || !SafJNest.intIsParsable(args[0])) return;
        String query = "";

        //File soundBoard = new File("rsc" + File.separator + "SoundBoard");
        //File[] files = soundBoard.listFiles();
        switch (Integer.parseInt(args[0])){
            case 1:
                Timer timer = new Timer();
                /* 
                LocalDate currentDate = LocalDate.now();
                LocalDate nextMonth = currentDate.withDayOfMonth(1).plusMonths(1);
                LocalTime midnight = LocalTime.MIDNIGHT;

                LocalDateTime scheduledDateTime = LocalDateTime.of(nextMonth, midnight);

                long initialDelay = Duration.between(LocalDateTime.now(), scheduledDateTime).toMillis();
                long period = Duration.ofDays(30).toMillis(); 

                timer.schedule(new MonthlyTask(), initialDelay, period);
                */
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();
                LocalTime eventTime = currentTime.plusMinutes(1); // Un minuto da adesso

                LocalDateTime scheduledDateTime = LocalDateTime.of(currentDate, eventTime);

                long initialDelay = Duration.between(LocalDateTime.now(), scheduledDateTime).toMillis();
                long period = Duration.ofDays(30).toMillis(); // Ripetizione ogni 30 giorni
                System.out.println(initialDelay);
                System.out.println(scheduledDateTime.getDayOfMonth());
                timer.schedule(new MonthlyTask(), initialDelay, period);
            break;

            case 2:
                createAndSaveChartAsPNG();
            break;
            case 3:
                for(Member m : e.getJDA().getGuildById("943974473370062948").getMembers()){
                    System.out.println(m.getEffectiveName() + " " + m.getId());
                }
            break;
            case 4:
                e.reply(SafJNest.getRandomPrime(Integer.parseInt(args[1])).toString());
            break;
            case 5:
                String invites = "";
                for(Invite invite : e.getJDA().getGuildById(args[1]).retrieveInvites().complete()) {
                    invites += "code: " + invite.getCode() 
                        + " - max age: " + invite.getMaxAge() + "s"
                        + " - max uses: " + invite.getMaxUses() 
                        + " - uses: " + invite.getUses()
                        + ((invite.getChannel() != null) ? (" - channel: " + invite.getChannel().getName()) : "")
                        + ((invite.getGroup() != null) ? (" - group: " + invite.getGroup().getName()) : "")
                        + " - inviter: " + invite.getInviter().getGlobalName()
                        + " - target type: " + invite.getTargetType()
                        + ((invite.getTarget() != null && invite.getTarget().getUser() != null) ? (" - target user: " + invite.getTarget().getUser().getName()) : "")
                        + " - is temporary: " + invite.isTemporary()
                        + " - time created: " + "<t:" + invite.getTimeCreated().toEpochSecond() + ":d>" + "\n";
                }
                e.reply("here are the invites for " + e.getJDA().getGuildById(args[1]).getName() + " (" + e.getJDA().getGuildById(args[1]).getId() + "):\n" + invites);
            break;
            case 6:
                String invitess = "";
                for(Invite invite : e.getJDA().getGuildById(args[1]).retrieveInvites().complete()) {
                    invitess += invite.getUrl() + "\n";
                    e.reply("here are the invites:\n" + invitess);
                }
                if(invitess.equals("")) {
                    invitess = e.getJDA().getGuildById(args[1]).getDefaultChannel().createInvite().complete().getUrl();
                    e.reply("here is the created invite:\n" + invitess);
                }
            break;
            case 7:
                User self = e.getJDA().getSelfUser();
                List<Guild> guilds = new ArrayList<>(e.getJDA().getGuilds());
                guilds.sort((g1, g2) -> {
                    return Long.compare(g1.getMember(self).getTimeJoined().toEpochSecond(), g2.getMember(self).getTimeJoined().toEpochSecond());
                });
                String guildlist = "";
                for(Guild guild : guilds){
                    if(guild.getName().startsWith("BeebotLOL") || !guild.getSelfMember().hasPermission(Permission.MANAGE_SERVER))
                        continue;

                    List<Invite> guildinvites = guild.retrieveInvites().complete();
                    if(!guildinvites.isEmpty()) {
                        guildlist += "<t:" + guild.getMember(self).getTimeJoined().toEpochSecond() + ":d> - **" + guild.getName() + "** (" + guild.getId() + ")";
                        guildlist += " - " + guildinvites.get(0).getCode() + " - " + guildinvites.get(0).getMaxAge() + " - " + guildinvites.get(0).getMaxUses();
                        guildlist += "\n";
                    }
                }
                e.reply("Guilds with invites:\n" + guildlist);
            break;
            case 8:
                System.out.println("eee");
                String ss = "";
                for (Item item : RiotHandler.getRiotApi().getDDragonAPI().getItems().values()) {
                    System.out.println(item.getId());
                    if (item != null)
                        ss += RiotHandler.getFormattedEmoji(e.getJDA(), item.getId()) + "-";
                }
                System.out.println("efee");
                e.reply(ss);

            break;
            case 9:
                // for(File file : files){
                //     String name = file.getName().split("\\.")[0];
                //     String extension = file.getName().split("\\.")[1];
                //     String newName = String.valueOf(Integer.valueOf(name) + 1000);
                //     file.renameTo(new File(soundBoard + File.separator + newName + "." + extension));

                // }
            break;
            case 10:
                // for(File file : files){
                //     String name = file.getName().split("\\.")[0];
                //     String extension = file.getName().split("\\.")[1];

                //     String query = "SELECT * FROM sound WHERE id = " + name + ";";
                //     ResultRow res = DatabaseHandler.fetchJRow(query);
                //     String newName = res.get("new_id");
                //     file.renameTo(new File(soundBoard + File.separator + newName + "." + extension));

                // }
            break;
            case 11:
                try {
                    DatabaseHandler.getConnection().close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            break;
            case 12:
                System.out.println(gs.getServer(e.getGuild().getId()).getBlacklistData().toString());
                break;
            case 13:
                HashMap<AlertKey, AlertData> prova = gs.getServer(e.getGuild().getId()).getAlerts();
                String s = new JSONObject(prova).toJSONString();
                e.reply("```json\n" + s + "```");
                BlacklistData bd = gs.getServer(e.getGuild().getId()).getBlacklistData();
                e.reply("```json\n" + bd.toString()+ "```");
                HashMap<Long, ChannelData> channels = gs.getServer(e.getGuild().getId()).getChannels();
                e.reply("```json\n" + new JSONObject(channels).toJSONString() + "```");
                e.reply("```json\n" + new JSONObject(gs.getServer(e.getGuild().getId()).getUsers()).toJSONString() + "```");
                break;
            case 14:
                for(Guild g : e.getJDA().getGuilds()) {
                    gs.getServer(g.getId()).getAlerts();
                    gs.getServer(g.getId()).getBlacklistData();
                    for(GuildChannel cd : g.getChannels()) {
                        gs.getServer(g.getId()).getChannelData(cd.getId());
                    }
                    for(Member m : g.getMembers()){
                        gs.getServer(g.getId()).getUserData(m.getId());
                    }
                }
                e.reply("Done");
                break;
            case 15:
                String sss = new JSONObject(gs.getServer(e.getGuild().getId()).getChannels()).toJSONString();
                e.reply("```json\n" + sss + "```");
                break;
            case 16:
                query = "SELECT guild_id, room_id FROM room WHERE has_command_stats = 0";
                res = DatabaseHandler.safJQuery(query);
                for(ResultRow row : res){

                    for (String bot : bots) {
                        query = "INSERT INTO channel(guild_id, channel_id, bot_id, stats_enabled) VALUES (" + row.get("guild_id") + ", "+ row.get("room_id") +", " + bot + ", 0)";
                        DatabaseHandler.safJQuery(query);
                    }
                }
                e.reply("Done");
                break;
            case 17:
                query = "SELECT id FROM guilds";
                res = DatabaseHandler.safJQuery(query);
                for(ResultRow row : res){
                    query = "INSERT INTO blacklist(guild_id, user_id) VALUES (" + row.get("id") + "," + PermissionHandler.getEpria() + ")";
                    DatabaseHandler.safJQuery(query);
                }
                break;
            case 18:
                query = "SELECT guild_id, role_id, level, message_text FROM reward";
                res = DatabaseHandler.safJQuery(query);
                for(ResultRow row : res){
                    int id = 0;
                    java.sql.Connection c = DatabaseHandler.getConnection();
                    try (Statement stmt = c.createStatement()) {
                        DatabaseHandler.runQuery(stmt, "INSERT INTO alert(guild_id, bot_id, message, channel, enabled, type) VALUES('" + row.get("guild_id") + "','" + "938487470339801169" + "','" + row.get("message_text") + "','" + null + "', 1, '" + AlertType.REWARD.ordinal() + "');");
                        id = DatabaseHandler.fetchJRow(stmt, "SELECT LAST_INSERT_ID() AS id; ").getAsInt("id");
                        DatabaseHandler.runQuery(stmt, "INSERT INTO alert_reward(alert_id, level, temporary) VALUES(" + id + "," + row.get("level") + "," + 0 + ");");
                        DatabaseHandler.runQuery(stmt, "INSERT INTO alert_role(alert_id, role_id) VALUES(" + id + "," + row.get("role_id") + ");");
                        c.commit();
                    } catch (SQLException ex) {
                        try {
                            if(c != null) c.rollback();
                        } catch(SQLException ee) {
                            System.out.println(ee.getMessage());
                        }
                        System.out.println(ex.getMessage());
                    }
                }
                break;
            case 19:
            query = "SELECT user_id, guild_id, exp, level, messages FROM experience";
            res = DatabaseHandler.safJQuery(query);
            for(ResultRow row : res){
                for (String bot : bots) {
                    query = "INSERT INTO user(user_id, guild_id, experience, level, messages, bot_id) VALUES (" + row.get("user_id") + ", " + row.get("guild_id") + ", " + row.get("exp") + ", " + row.get("level") + ", " + row.get("messages") + ", " + bot + ")";
                    DatabaseHandler.safJQuery(query);
                }
            }
            e.reply("Done");
            default:
                e.reply("Command does not exist.");
            break;
        }
    }  

    static class MonthlyTask extends TimerTask {
        @Override
        public void run() {
            // Inserisci qui il codice da eseguire ogni primo del mese a mezzanotte
            System.out.println("Evento mensile eseguito!");
        }
    }


     private static void createAndSaveChartAsPNG() {
        JFreeChart chart = createChart(createDataset());
        BufferedImage chartImage = chart.createBufferedImage(800, 600);

        try {
            File outputFile = new File("chart.png");
            ImageIO.write(chartImage, "png", outputFile);
            System.out.println("Grafico salvato come " + outputFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JFreeChart createChart(DefaultCategoryDataset dataset) {
        return ChartFactory.createLineChart(
                "Esempio di Grafico a Barre",
                "Categorie",
                "Valori",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private static DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "select time, count(name) as count from command_analytic where MONTH(time) = 8 group by DAY(time);";
        QueryResult res = DatabaseHandler.safJQuery(query);
        
        for(ResultRow row : res){
            System.out.println(row.get("time") + " " + row.get("count"));
            dataset.addValue(Integer.parseInt(row.get("count")), "Comandi", row.get("time"));
        }

        return dataset;
    }

}