package com.safjnest.Utilities.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

import com.safjnest.Utilities.Guild.Alert.AlertType;

/**
 * Useless (now usefull) class but {@link <a href="https://github.com/Leon412">Leon412</a>} is one
 * of the biggest caterpies ever made
 */
public class DatabaseHandler {
    private static String hostName;
    private static String database;
    private static String user;
    private static String password;

    private static Connection c;

    /**
     * Constructor
     * 
     * @param hostName Hostname, as 'keria123.eu-west-1.compute.fakerAws.com'
     * @param database Name of the database to connect in
     * @param user Username
     * @param password Password
     */
    public DatabaseHandler(String hostName, String database, String user, String password){
        DatabaseHandler.hostName = hostName;
        DatabaseHandler.database = database;
        DatabaseHandler.user = user;
        DatabaseHandler.password = password;
        
        connectIfNot();
    }

    private static void connectIfNot() {
        try {
            if (c != null && !c.isClosed()) return;

            Class.forName("org.mariadb.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mariadb://" + hostName + "/" + database + "?autoReconnect=true", user, password);
            c.setAutoCommit(false);
            System.out.println("[SQL] INFO Connection to the extreme db successful!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("[SQL] ERROR Connection to the extreme db failed!");
        }
    }

    public static QueryResult safJQuery(String query) {
        connectIfNot();

        QueryResult result = new QueryResult();
        
        try (Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                ResultRow beeRow = new ResultRow();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnLabel(i);
                    String columnValue = rs.getString(i);
                    beeRow.put(columnName, columnValue);
                }
                result.add(beeRow);
            }
            c.commit();
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
            //return null;
        }

        return result; 
    }


    /**
     * Method used for returning a {@link com.safjnest.Utilities.SQL.QueryResult result} from a query using default statement
     * @param stmt
     * @param query
     * @throws SQLException
     */ 
    public static QueryResult safJQuery(Statement stmt, String query) throws SQLException {
        connectIfNot();

        QueryResult result = new QueryResult();

        ResultSet rs = stmt.executeQuery(query);

        ResultSetMetaData rsmd = rs.getMetaData();

        while (rs.next()) {
            ResultRow beeRow = new ResultRow();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i);
                String columnValue = rs.getString(i);
                beeRow.put(columnName, columnValue);
            }
            result.add(beeRow);
        }

        return result; 
    }


    /**
     * Method used for returning a single {@link com.safjnest.Utilities.SQL.ResultRow row} from a query using default statement
     * @param stmt
     * @param query
     * @throws SQLException
     */
    public static ResultRow fetchJRow(String query) {
        connectIfNot();

        ResultRow beeRow = new ResultRow();

        try (Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {
            
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnName = rsmd.getColumnName(i);
                    String columnValue = rs.getString(i);
                    beeRow.put(columnName, columnValue);
                }
            }
            c.commit();
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
            //return null;
        }

        return beeRow;
    }


    /**
     * Method used for returning a single {@link com.safjnest.Utilities.SQL.ResultRow row} from a query.
     * @param stmt
     * @param query
     * @throws SQLException
     */
    public static ResultRow fetchJRow(Statement stmt, String query) throws SQLException {
        connectIfNot();
        
        ResultRow beeRow = new ResultRow();

        ResultSet rs = stmt.executeQuery(query);
            
        ResultSetMetaData rsmd = rs.getMetaData();

        if (rs.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String columnName = rsmd.getColumnName(i);
                String columnValue = rs.getString(i);
                beeRow.put(columnName, columnValue);
            }
        }

        return beeRow;
    }


    /**
     * Run one or more queries using the deffault statement
     * @param queries
     */
    public static boolean runQuery(String... queries) {
        connectIfNot();

        try (Statement stmt = c.createStatement()) {
            for (String query : queries)
                stmt.execute(query);
            c.commit();
            return true;
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
            return false;
        }
    }


    /**
     * Run one or more queries with a specific statement
     * <p>
     * Only for insert, update and delete
     * @param stmt
     * @param queries
     * @throws SQLException
     */
    public static void runQuery(Statement stmt, String... queries) throws SQLException {
        connectIfNot();
        
        for (String query : queries)
            stmt.execute(query);
    }

    //-------------------------------------------------------------------------

    public static QueryResult getGuildsData(String filter){        
        String query = "SELECT guild_id, prefix, exp_enabled, threshold, blacklist_channel FROM guild WHERE " + filter + ";";
        return safJQuery(query);
    }


    public static QueryResult getlistGuildSounds(String guild_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE guild_id = '" + guild_id + "' ORDER BY name ASC");
    }

    public static QueryResult getlistGuildSounds(String guild_id, int limit) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE guild_id = '" + guild_id + "' ORDER BY name ASC LIMIT " + limit);
    }

    public static QueryResult getGuildRandomSound(String guild_id){
        return safJQuery("SELECT name, id FROM sound WHERE guild_id = '" + guild_id + "' ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getUserRandomSound(String user_id){
        return safJQuery("SELECT name, id FROM sound WHERE user_id = '" + user_id + "' ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getlistUserSounds(String user_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE user_id = '" + user_id + "' ORDER BY name ASC");
    }

    public static QueryResult getlistUserSounds(String user_id, String guild_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE user_id = '" + user_id + "' AND (guild_id = '" + guild_id + "'  OR public = 1) ORDER BY name ASC");
    }

    public static QueryResult getFocusedGuildSound(String guild_id, String like){
        return safJQuery("SELECT name, id FROM sound WHERE name LIKE '" + like + "%' AND guild_id = '" + guild_id + "' ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getFocusedUserSound(String user_id, String like){
        return safJQuery("SELECT name, id FROM sound WHERE name LIKE '" + like + "%' OR id LIKE '" + like + "%' AND user_id = '" + user_id + "' ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getFocusedListUserSounds(String user_id, String guild_id, String like) {
        return safJQuery("SELECT name, id FROM sound WHERE name LIKE '" + like + "%' OR id LIKE '" + like + "%' AND (user_id = '" + user_id + "' OR (guild_id = '" + guild_id + "' AND public = 1)) ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getSoundsById(String... sound_ids) {
        StringBuilder sb = new StringBuilder();
        for(String sound_id : sound_ids)
            sb.append(sound_id + ", ");
        sb.setLength(sb.length() - 2);

        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE id IN (" + sb.toString() + ");");
    }

    public static QueryResult getSoundsById(String id, String guild_id, String author_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public, time FROM sound WHERE id = '" + id + "' AND  (guild_id = '" + guild_id + "'  OR public = 1 OR user_id = '" + author_id + "')");
    }

    public static QueryResult getSoundsByName(String name, String guild_id, String author_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public, time FROM sound WHERE name = '" + name + "' AND  (guild_id = '" + guild_id + "'  OR public = 1 OR user_id = '" + author_id + "')");
    }

    public static QueryResult getDuplicateSoundsByName(String name, String guild_id, String author_id) {
        return safJQuery("SELECT id, guild_id, user_id FROM sound WHERE name = '" + name + "' AND  (guild_id = '" + guild_id + "' OR user_id = '" + author_id + "')");
    }

    public static ResultRow getAuthorSoundById(String id, String user_id) {
        return fetchJRow("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE id = '" + id + "' AND user_id = '" + user_id + "'");
    }

    public static ResultRow getAuthorSoundByName(String name, String user_id) {
        return fetchJRow("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE name = '" + name + "' AND user_id = '" + user_id + "'");
    }

    public static String insertSound(String name, String guild_id, String user_id, String extension, boolean isPublic) {
        String soundId = null;
        try (Statement stmt = c.createStatement()) {
            runQuery(stmt, "INSERT INTO sound(name, guild_id, user_id, extension, public, time) VALUES('" + name + "','" + guild_id + "','" + user_id + "','" + extension + "', " + ((isPublic == true) ? "1" : "0") + ", '" +  Timestamp.from(Instant.now()) + "'); ");
            soundId = fetchJRow(stmt, "SELECT LAST_INSERT_ID() AS id; ").get("id");
            c.commit();
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
        }
        return soundId;
    }

    public static boolean updateSound(String id, String name, boolean isPublic) {
        return runQuery("UPDATE sound SET name = '" + name + "', public = '" + (isPublic ? "1" : "0") + "' WHERE id = '" + id + "';");
    }

    public static boolean deleteSound(String id) {
        return runQuery("DELETE FROM sound WHERE id = " + id + ";");
    }

    public static boolean updateUserPlays(String sound_id, String user_id) {
        return runQuery("INSERT INTO play(user_id, sound_id, times) VALUES('" + user_id + "','" + sound_id + "', 1) ON DUPLICATE KEY UPDATE times = times + 1;");
    }

    public static ResultRow getPlays(String sound_id, String user_id) {
        return fetchJRow("SELECT"
            + "(SELECT SUM(times) FROM play WHERE sound_id = '" + sound_id + "') AS totalTimes,"
            + "(SELECT times FROM play WHERE sound_id = '" + sound_id + "' AND user_id = '" + user_id + "') AS timesByUser;");
    }

    public static String getSoundsUploadedByUserCount(String user_id) {
        return fetchJRow("select count(name) as count from sound where user_id = '" + user_id + "';").get("count");
    }

    public static String getSoundsUploadedByUserCount(String user_id, String guild_id) {
        return fetchJRow("select count(name) as count from sound where guild_id = '" + guild_id + "' AND user_id = '" + user_id + "';").get("count");
    }

    public static String getTotalPlays(String user_id) {
        return fetchJRow("select sum(times) as sum from play where user_id = '" + user_id + "';").get("sum");
    }

    public static boolean soundboardExists(String id, String guild_id) {
        return !fetchJRow("SELECT id from soundboard WHERE ID = '" + id + "' AND guild_id = '" + guild_id + "'").emptyValues();
    }

    public static int getSoundInSoundboardCount(String id) {
        return fetchJRow("SELECT count(sound_id) as cont FROM soundboard_sounds WHERE id = '" + id + "'").getAsInt("count");
    }

    public static QueryResult getSoundsFromSoundBoard(String id) {
        return safJQuery("select soundboard_sounds.sound_id, sound.extension, sound.name from soundboard_sounds join soundboard on soundboard.id = soundboard_sounds.id join sound on soundboard_sounds.sound_id = sound.id where soundboard.id = '" + id + "'");
    }

    public static ResultRow getSoundboardByID(String id) {
        return fetchJRow("select name from soundboard where id = '" + id + "'");
    }
    
    public static QueryResult getRandomSoundboard(String guild_id) {
        return safJQuery("SELECT name, id FROM soundboard WHERE guild_id = '" + guild_id + "' ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getFocusedSoundboard(String guild_id, String like){
        return safJQuery("SELECT name, id FROM soundboard WHERE name LIKE '" + like + "%' AND guild_id = '" + guild_id + "' ORDER BY RAND() LIMIT 25;");
    }

    public static QueryResult getFocusedSoundFromSounboard(String id, String like){
        return safJQuery("SELECT s.name, s.id FROM soundboard_sounds ss JOIN sound s ON ss.sound_id = s.id WHERE s.name LIKE '" + like + "%' AND ss.id = '" + id);
    }



    public static boolean insertSoundBoard(String name, String guild_id, String... sound_ids) {
        if(sound_ids.length == 0) throw new IllegalArgumentException("sound_ids must not be empty");

        StringBuilder sb = new StringBuilder();

        for (String sound_id : sound_ids)
            sb.append("(LAST_INSERT_ID(), " + sound_id + "), ");
        sb.setLength(sb.length() - 2);
        
        try (Statement stmt = c.createStatement()) {
            runQuery(stmt, "INSERT INTO soundboard (name, guild_id) VALUES ('" + name + "', '" + guild_id + "'); ");
            runQuery(stmt, "INSERT INTO soundboard_sounds (id, sound_id) VALUES " + sb.toString() + ";");
            c.commit();
            return true;
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public static boolean insertSoundsInSoundBoard(String id, String... sound_ids) {
        if(sound_ids.length == 0) throw new IllegalArgumentException("sound_ids must not be empty");

        StringBuilder sb = new StringBuilder();
        for(String sound_id : sound_ids) {
            sb.append("('" + id + "', '" + sound_id + "'), ");
        }
        sb.setLength(sb.length() - 2);

        return runQuery("INSERT INTO soundboard_sounds (id, sound_id) VALUES " + sb.toString() + "; ");
    }

    public static boolean deleteSoundboard(String id) {
        return runQuery("DELETE FROM soundboard WHERE id = '" + id + "'");
    }

    public static boolean deleteSoundFromSoundboard(String id, String sound_id) {
        return runQuery("DELETE FROM soundboard_sounds WHERE id = '" + id + "' AND sound_id = '" + sound_id + "'");
    }
 
    public static ResultRow getDefaultVoice(String guild_id) {
        return fetchJRow("SELECT name_tts, language_tts FROM guild WHERE guild_id = '" + guild_id + "';");
    }

    public static String getLOLAccountIdByUserId(String user_id){
        String query = "SELECT account_id FROM summoner WHERE user_id = '" + user_id + "';";
        return fetchJRow(query).get("account_id");
    }

    public static QueryResult getLOLAccountsByUserId(String user_id){
        String query = "SELECT account_id FROM summoner WHERE user_id = '" + user_id + "';";
        return safJQuery(query);
    }

    public static String getUserIdByLOLAccountId(String account_id) {
        return fetchJRow("SELECT user_id FROM summoner WHERE account_id = '" + account_id + "';").get("user_id");
    }

    

    public static boolean addLOLAccount(String user_id, String summoner_id, String account_id){
        String query = "INSERT INTO summoner(user_id, summoner_id, account_id) VALUES('" + user_id + "','" + summoner_id + "','" + account_id + "');";
        return runQuery(query);
    }

    public static boolean deleteLOLaccount(String user_id, String account_id){
        String query = "DELETE FROM summoner WHERE account_id = '" + account_id + "' and user_id = '" + user_id + "';";
        return runQuery(query);
    }

    public static String getLolProfilesCount(String user_id){
        String query = "SELECT count(user_id) as count FROM summoner WHERE user_id = '" + user_id + "';";
        return fetchJRow(query).get("count");
    }

    public static QueryResult getGuildData(){
        String query = "SELECT guild_id, PREFIX, exp_enabled, threshold, blacklist_channel, blacklist_enabled FROM guild;";
        return safJQuery(query);
    }
    
    public static ResultRow getGuildData(String guild_id) {
        String query = "SELECT guild_id, PREFIX, exp_enabled, threshold, blacklist_channel, blacklist_enabled FROM guild WHERE guild_id = '" + guild_id + "';";
        return fetchJRow(query);
    }

    public static boolean updateVoiceGuild(String guild_id, String language, String voice) {
        String query = "INSERT INTO guild (guild_id, language_tts, name_tts) VALUES ('" + guild_id + "', '" + language + "', '" + voice + "') ON DUPLICATE KEY UPDATE language_tts = '" + language + "', name_tts = '" + voice + "'";
        return runQuery(query);
    }

    public static boolean insertGuild(String guild_id, String prefix) {
        String query = "INSERT INTO guild (guild_id, bot_id, PREFIX, exp_enabled, threshold, blacklist_channel) VALUES ('" + guild_id + "', '" + prefix + "', '0', '0', null) ON DUPLICATE KEY UPDATE prefix = '" + prefix + "';";
        return runQuery(query);
    }


    public static QueryResult getUsersByExp(String guild_id, int limit) {
        if (limit == 0) {
            return safJQuery("SELECT user_id, messages, level, experience as exp from user WHERE guild_id = '" + guild_id + "' order by experience DESC;");
        }
        return safJQuery("SELECT user_id, messages, level, experience as exp from user WHERE guild_id = '" + guild_id + "' order by experience DESC limit " + limit + ";");
    }

    public static QueryResult getLolAccounts(String user_id) {
        return safJQuery("SELECT summoner_id FROM summoner WHERE user_id = '" + user_id + "';");
    }

    public static boolean toggleLevelUp(String guild_id, boolean toggle) {
        return runQuery("INSERT INTO guild(guild_id, exp_enabled) VALUES ('" + guild_id + "', '" + (toggle ? "1" : "0") + "') ON DUPLICATE KEY UPDATE exp_enabled = '" + (toggle ? "1" : "0") + "';");
    }

    public static boolean toggleBlacklist(String guild_id, boolean toggle) {
        return runQuery("UPDATE guild SET blacklist_enabled = '" + toggle + "' WHERE guild_id = '" + guild_id + "';");
    }


    public static boolean setPrefix(String guild_id, String prefix) {
        return runQuery("INSERT INTO guild(guild_id, prefix)" + "VALUES('" + guild_id + "','" + prefix +"') ON DUPLICATE KEY UPDATE prefix = '" + prefix + "';");
    }

    public static boolean updatePrefix(String guild_id, String prefix) {
        return runQuery("UPDATE guild SET prefix = '" + prefix + "' WHERE guild_id = '" + guild_id + "';");
    }

    public static boolean setGreet(String user_id, String guild_id, String sound_id) {
        return runQuery("INSERT INTO greeting (user_id, guild_id, sound_id) VALUES ('" + user_id + "', '" + guild_id + "', '" + sound_id + "') ON DUPLICATE KEY UPDATE sound_id = '" + sound_id + "';");
    }

    public static boolean deleteGreet(String user_id, String guild_id) {
        return runQuery("DELETE from greeting WHERE guild_id = '" + guild_id + "' AND user_id = '" + user_id + "';");
    }
    
    public static boolean setBlacklistChannel(String blacklist_channel, String guild_id) {
        return runQuery("UPDATE guild SET blacklist_channel = '" + blacklist_channel + "' WHERE guild_id = '" + guild_id +  "';");
    }

    public static boolean setBlacklistThreshold(String threshold, String guild_id) {
        return runQuery("UPDATE guild SET threshold = '" + threshold + "' WHERE guild_id = '" + guild_id +  "';");
    }

    public static boolean enableBlacklist(String guild_id, String threshold, String blacklist_channel) {
        return runQuery("INSERT INTO guild(guild_id, threshold, blacklist_channel, blacklist_enabled)" + "VALUES('" + guild_id + "','" + threshold +"', '" + blacklist_channel + "', 1) ON DUPLICATE KEY UPDATE threshold = '" + threshold + "', blacklist_channel = '" + blacklist_channel + "', blacklist_enabled = 1;");
    }

    public static boolean insertUserBlacklist(String user_id, String guild_id){
        return runQuery("INSERT INTO blacklist VALUES('" + user_id + "', '" + guild_id + "')");
    }

    public static int getBlacklistBan(String user_id){
        return fetchJRow("SELECT count(user_id) as times from blacklist WHERE user_id = '" + user_id + "'").getAsInt("times");
    }

    public static boolean deleteBlacklist(String guild_id, String user_id){
        return runQuery("DELETE FROM blacklist WHERE guild_id = '" + guild_id + "' AND user_id = '" + user_id + "'");
    }

    public static QueryResult getGuildByThreshold(int threshold, String guild_id){
        return safJQuery("SELECT guild_id, blacklist_channel, threshold FROM guild WHERE blacklist_enabled = 1 AND threshold <= '" + threshold + "' AND blacklist_channel IS NOT NULL AND guild_id != '" + guild_id + "'");
    }

    public static boolean insertCommand(String guild_id, String author_id, String command, String args){
        return runQuery("INSERT INTO command(name, time, user_id, guild_id, args) VALUES ('" + command + "', '" + new Timestamp(System.currentTimeMillis()) + "', '" + author_id + "', '"+ guild_id +"', '"+ fixSQL(args) +"');");
    }

    public static int getBannedTimes(String user_id){
        return fetchJRow("SELECT count(user_id) as times from blacklist WHERE user_id = '" + user_id + "'").getAsInt("times");
    }
    
    public static int getBannedTimesInGuild(String guild_id){
        return fetchJRow("SELECT count(user_id) as times from blacklist WHERE guild_id = '" + guild_id + "'").getAsInt("times");
    }

    public static ResultRow getGreet(String user_id, String guild_id) {
        return fetchJRow("SELECT sound.id, sound.extension from greeting join sound on greeting.sound_id = sound.id WHERE greeting.user_id = '" + user_id + "' AND (greeting.guild_id = '" + guild_id + "' OR greeting.guild_id = '0') ORDER BY CASE WHEN greeting.guild_id = '0' THEN 1 ELSE 0 END LIMIT 1;");
    }


    public static boolean setAlertMessage(String ID, String message) {
        try (PreparedStatement pstmt = c.prepareStatement("UPDATE alert SET message = ? WHERE ID = ?")) {
            pstmt.setString(1, message);
            pstmt.setString(2, ID);
            int affectedRows = pstmt.executeUpdate();
            c.commit();
            return affectedRows > 0;
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public static boolean setAlertChannel(String ID, String channel) {
        return runQuery("UPDATE alert SET channel = '" + channel + "' WHERE ID = '" + ID + "';");
    }

    public static boolean setAlertEnabled(String ID, boolean toggle) {
        return runQuery("UPDATE alert SET enabled = '" + (toggle ? 1 : 0) + "' WHERE ID = '" + ID + "';");
    }

    public static QueryResult getAlerts(String guild_id) {
        return safJQuery("SELECT id, message, channel, enabled, type FROM alert WHERE guild_id = '" + guild_id + "';");
    }

    public static QueryResult getAlertsRoles(String guild_id) {
        return safJQuery("SELECT r.id as row_id, a.id as alert_id, r.role_id as role_id  FROM alert_role as r JOIN alert as a ON r.alert_id = a.id WHERE a.guild_id = '" + guild_id + "';");
    }

    public static int createAlert(String guild_id, String message, String channelId, AlertType type) {
        int id = 0;
        try (Statement stmt = c.createStatement()) {
            runQuery(stmt, "INSERT INTO alert(guild_id, message, channel, enabled, type) VALUES('" + guild_id + "','" + message + "','" + channelId + "', 1, '" + type.ordinal() + "');");
            id = fetchJRow(stmt, "SELECT LAST_INSERT_ID() AS id; ").getAsInt("id");
            c.commit();
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public static boolean createRewardData(String alertID, int level, boolean temporary) {
        return runQuery("INSERT INTO alert_reward(alert_id, level, temporary) VALUES('" + alertID + "', '" + level + "', '" + (temporary ? 1 : 0) + "');");
    }

    public static QueryResult getRewardData(String guild_id) {
        return safJQuery("SELECT r.id as id, a.id as alert_id, r.level as level, r.temporary as temporary FROM alert as a JOIN alert_reward as r ON a.id = r.alert_id WHERE a.guild_id = '" + guild_id + "';");
    }

    public static boolean deleteAlert(String valueOf) {
        return runQuery("DELETE FROM alert WHERE id = '" + valueOf + "';");
    }

    public static boolean deleteAlertRoles(String valueOf) {
        return runQuery("DELETE FROM alert_role WHERE alert_id = '" + valueOf + "';");
    }

    public static HashMap<Integer, String> createRolesAlert(String valueOf, String[] roles) {
        
        String values = "";
        for(String role : roles) {
            if(role != null) {
                values += "('" + valueOf + "', '" + role + "'), ";
            }
        }

        if (values.isEmpty()) {
            return null;
        }

        values = values.substring(0, values.length() - 2);

        if (deleteAlertRoles(valueOf) && runQuery("INSERT INTO alert_role(alert_id, role_id) VALUES " + values + ";")) {
            HashMap<Integer, String> roleMap = new HashMap<>();
            QueryResult result = safJQuery("SELECT id, role_id FROM alert_role WHERE alert_id = '" + valueOf + "';");
            for(ResultRow row : result) {
                roleMap.put(row.getAsInt("id"), row.get("role_id"));
            }
            return roleMap;
        }

        return null;
                
    }


    public static int insertChannelData(long guild_id, long channel_id) {
        int id = 0;
        try (Statement stmt = c.createStatement()) {
            runQuery(stmt, "INSERT INTO channel(guild_id, channel_id) VALUES('" + guild_id + "','" + channel_id + "');");
            id = fetchJRow(stmt, "SELECT LAST_INSERT_ID() AS id; ").getAsInt("id");
            c.commit();
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public static QueryResult getChannelData(String guild_id) {
        return safJQuery("SELECT id, channel_id, exp_enabled, exp_modifier, stats_enabled FROM channel WHERE guild_id = '" + guild_id + "';");
    }

    public static boolean setChannelExpModifier(int ID, double exp_modifier) {
        return runQuery("UPDATE channel SET exp_modifier = '" + exp_modifier + "' WHERE id = '" + ID + "';");
    }

    public static boolean setChannelExpEnabled(int ID, boolean toggle) {
        return runQuery("UPDATE channel SET exp_enabled = '" + (toggle ? 1 : 0) + "' WHERE id = '" + ID + "';");
    }

    public static boolean deleteChannelData(int ID) {
        return runQuery("DELETE FROM channel WHERE id = '" + ID + "';");
    }


    public static int insertUserData(long guild_id, long user_id) {
        int id = 0;
        try (Statement stmt = c.createStatement()) {
            runQuery(stmt, "INSERT INTO user(guild_id, user_id) VALUES('" + guild_id + "','" + user_id + "');");
            id = fetchJRow(stmt, "SELECT LAST_INSERT_ID() AS id; ").getAsInt("id");
            c.commit();
        } catch (SQLException ex) {
            try {
                if(c != null) c.rollback();
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public static ResultRow getUserData(String guild_id, long user_id) {
        return fetchJRow("SELECT id, experience, level, messages, update_time FROM user WHERE user_id = '"+ user_id +"' AND guild_id = '" + guild_id + "';");
    }

    public static boolean updateUserDataExperience(int ID, int experience, int level, int messages) {
        return runQuery("UPDATE user SET experience = '" + experience + "', level = '" + level + "', messages = '" + messages + "' WHERE id = '" + ID + "';");
    }

    public static boolean updateUserDataUpdateTime(int ID, int updateTime) {
        return runQuery("UPDATE user SET update_time = '" + updateTime + "' WHERE id = '" + ID + "';");
    }

    public static ResultRow getUserExp(String id, String id2) {
        return fetchJRow("SELECT experience, level, messages FROM user WHERE user_id = '" + id + "' AND guild_id = '" + id2 + "'");
    }





    /** 
     * What the actual fuck sunyx?
     * eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee la cannuccia
     * weru9fgt9uehrgferwfghreyuio
    */
    public static String getCannuccia() {
        return ":cannuccia:";
    }

    /**
     * @deprecated
     * deprecated this shit and use querySafe
     * @param s
     * @return
     */
    public static String fixSQL(String s){
        s = s.replace("\"", "\\\"");
        s = s.replace("\'", "\\\'");
        return s;
    }

    public static Connection getConnection(){
        return c;
    }

}