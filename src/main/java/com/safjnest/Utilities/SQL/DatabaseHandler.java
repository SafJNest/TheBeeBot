package com.safjnest.Utilities.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Useless (now usefull) class but {@link <a href="https://github.com/Leon412">Leon412</a>} is one
 * of the biggest caterpies ever made
 */
public class DatabaseHandler {
    
    /** Object that opens the connection between database and beeby */
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
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mariadb://" + hostName + "/" + database + "?autoReconnect=true", user, password);
            c.setAutoCommit(false);
            System.out.println("[SQL] INFO Connection to the extreme db successful!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("[SQL] INFO Connection to the extreme db ANNODAM!");
        }
    }

    public static QueryResult safJQuery(String query) {
        QueryResult result = new QueryResult();

        try (Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

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
        for (String query : queries)
            stmt.execute(query);
    }



    public static QueryResult getGuildsData(String filter){        
        String query = "SELECT guild_id, prefix, exp_enabled, threshold, blacklist_channel FROM guild_settings WHERE " + filter + ";";
        return safJQuery(query);
    }

    public static QueryResult getRoomsData(String filter){        
        String query = "SELECT guild_id, room_id, room_name, has_exp, exp_value, has_command_stats FROM rooms_settings WHERE " + filter + ";";
        return safJQuery(query);
    }

    public static QueryResult getlistGuildSounds(String guild_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE guild_id = '" + guild_id + "' ORDER BY name ASC");
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
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE id = '" + id + "' AND  (guild_id = '" + guild_id + "'  OR public = 1 OR user_id = '" + author_id + "')");
    }

    public static QueryResult getSoundsByName(String name, String guild_id, String author_id) {
        return safJQuery("SELECT id, name, guild_id, user_id, extension, public FROM sound WHERE name = '" + name + "' AND  (guild_id = '" + guild_id + "'  OR public = 1 OR user_id = '" + author_id + "')");
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
            runQuery(stmt, "INSERT INTO sound(name, guild_id, user_id, extension, public) VALUES('" + name + "','" + guild_id + "','" + user_id + "','" + extension + "', " + ((isPublic == true) ? "1" : "0") + "); ");
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

    public static boolean soundboardExists(String name, String guild_id) {
        return !fetchJRow("SELECT id from soundboard WHERE name = '" + name + "' AND guild_id = '" + guild_id + "'").emptyValues();
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
 
    public static ResultRow getDefaultVoice(String guild_id, String bot_id) {
        return fetchJRow("SELECT name_tts, language_tts FROM guild_settings WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static String getLOLAccountIdByUserId(String user_id){
        String query = "SELECT account_id FROM lol_user WHERE user_id = '" + user_id + "';";
        return fetchJRow(query).get("account_id");
    }

    public static QueryResult getLOLAccountsByUserId(String user_id){
        String query = "SELECT account_id FROM lol_user WHERE user_id = '" + user_id + "';";
        return safJQuery(query);
    }

    public static String getUserIdByLOLAccountId(String account_id) {
        return fetchJRow("SELECT user_id FROM lol_user WHERE account_id = '" + account_id + "';").get("user_id");
    }

    

    public static boolean addLOLAccount(String user_id, String summoner_id, String account_id){
        String query = "INSERT INTO lol_user(user_id, summoner_id, account_id) VALUES('" + user_id + "','" + summoner_id + "','" + account_id + "');";
        return runQuery(query);
    }

    public static boolean deleteLOLaccount(String user_id, String account_id){
        String query = "DELETE FROM lol_user WHERE account_id = '" + account_id + "' and user_id = '" + user_id + "';";
        return runQuery(query);
    }

    public static String getLolProfilesCount(String user_id){
        String query = "SELECT count(user_id) as count FROM lol_user WHERE user_id = '" + user_id + "';";
        return fetchJRow(query).get("count");
    }

    public static QueryResult getGuildData(String bot_id){
        String query = "SELECT guild_id, PREFIX, exp_enabled, threshold, blacklist_channel, blacklist_enabled FROM guild_settings WHERE bot_id = '" + bot_id + "';";
        return safJQuery(query);
    }
    
    public static ResultRow getGuildData(String guild_id, String bot_id) {
        String query = "SELECT guild_id, PREFIX, exp_enabled, threshold, blacklist_channel, blacklist_enabled FROM guild_settings WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';";
        return fetchJRow(query);
    }

    public static boolean insertGuild(String guild_id, String bot_id, String prefix) {
        String query = "INSERT INTO guild_settings (guild_id, bot_id, PREFIX, exp_enabled, threshold, blacklist_channel) VALUES ('" + guild_id + "', '" + bot_id + "', '" + prefix + "', '0', '0', null); ON DUPLICATE KEY UPDATE prefix = '" + prefix + "';";
        return runQuery(query);
    }

    public static boolean updateVoiceGuild(String guild_id, String bot_id, String language, String voice) {
        String query = "INSERT INTO guild_settings (guild_id, bot_id, language_tts, name_tts) VALUES ('" + guild_id + "', '" + bot_id + "', '" + language + "', '" + voice + "') ON DUPLICATE KEY UPDATE language_tts = '" + language + "', name_tts = '" + voice + "'";
        return runQuery(query);
    }

    public static QueryResult getRoomsSettings(String guild_id) {
        String query = "SELECT room_id, room_name, has_exp, exp_value, has_command_stats FROM rooms_settings WHERE guild_id ='" + guild_id + "';";
        return safJQuery(query);
    }

    public static QueryResult getRoomsSettingsWithExpModifier(String guild_id) {
        String query = "SELECT room_id, room_name, has_exp, exp_value, has_command_stats FROM rooms_settings WHERE guild_id ='" + guild_id + "' AnND has_exp = 1 AND exp_value > 1;";
        return safJQuery(query);
    }

    public static QueryResult getRoomsSettingsWithoutExp(String guild_id) {
        String query = "SELECT room_id, has_exp, exp_value from rooms_settings WHERE guild_id = '"+ guild_id +"' AND has_exp = 0;";
        return safJQuery(query);
    }

    public static ResultRow getExp(String guild_id, String user_id) {
        String query = "SELECT exp, level, messages FROM exp_table WHERE user_id = '" + user_id + "' AND guild_id = '" + guild_id + "';";
        return fetchJRow(query);
    }

    public static boolean addExpData(String guild_id, String user_id) {
        String query = "INSERT INTO exp_table (user_id, guild_id, exp, level, messages) VALUES ('" + user_id + "','" + guild_id + "',0,1,0);";
        return runQuery(query);
    }

    public static boolean updateExp(String guild_id, String user_id, int exp, int level, int messages){
        String  query = "UPDATE exp_table SET exp = " + exp + ", level = " + level + ", messages = " + messages + " WHERE user_id = '" + user_id + "' AND guild_id = '" + guild_id + "';";
        return runQuery(query);
    }

    public static boolean updateExp(String guild_id, String user_id, int exp, int messages){
        String  query = "UPDATE exp_table SET exp = " + exp + ", messages = " + messages + " WHERE user_id = '" + user_id + "' AND guild_id = '" + guild_id + "';";
        return runQuery(query);
    }

    public static QueryResult getUsersByExp(String guild_id, int limit) {
        return safJQuery("SELECT user_id, messages, level, exp from exp_table WHERE guild_id = '" + guild_id + "' order by exp DESC limit " + limit + ";");
    }

    public static QueryResult getLolAccounts(String user_id) {
        return safJQuery("SELECT summoner_id FROM lol_user WHERE user_id = '" + user_id + "';");
    }

    public static ResultRow getUserExp(String user_id, String guild_id) {
        return fetchJRow("select exp, level, messages from exp_table where user_id ='" + user_id + "' and guild_id = '" + guild_id + "';");
    }

    public static ResultRow getAlert(String guild_id, String bot_id) {
        return fetchJRow("SELECT * FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'");
    }

    public static boolean hasWelcome(String guild_id, String bot_id) {
        return fetchJRow("SELECT welcome_message FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'").get("welcome_message") != null;
    }

    public static ResultRow getWelcome(String guild_id, String bot_id) {
        return fetchJRow("SELECT welcome_message, welcome_channel, welcome_role, welcome_enabled FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean setWelcome(String guild_id, String bot_id, String welcome_channel, String welcome_message, String welcome_role) {
        if(welcome_role == null) {
            return runQuery("INSERT INTO alert(guild_id, bot_id, welcome_channel, welcome_message, welcome_enabled)"
                + "VALUES('" + guild_id + "','" + bot_id + "','" + welcome_channel + "','" + welcome_message + "','" + "1" + "') "
                + "ON DUPLICATE KEY UPDATE welcome_channel = '" + welcome_channel + "', welcome_message = '" + welcome_message + "', welcome_enabled = '" + "1" + "';");
        }
        return runQuery("INSERT INTO alert(guild_id, bot_id, welcome_channel, welcome_message, welcome_role, welcome_enabled)"
            + "VALUES('" + guild_id + "','" + bot_id + "','" + welcome_channel + "','" + welcome_message + "','" + "1" + "') "
            + "ON DUPLICATE KEY UPDATE welcome_channel = '" + welcome_channel + "', welcome_message = '" + welcome_message + "', welcome_role = '" + welcome_role + "', welcome_enabled = '" + "1" + "';");
    }

    public static boolean deleteWelcome(String guild_id, String bot_id) {
        return runQuery("UPDATE alert SET welcome_message = NULL, welcome_channel = NULL, welcome_role = NULL, welcome_enabled = 0 WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'"); 
    }

    public static boolean updateWelcomeChannel(String guild_id, String bot_id, String welcome_channel) {
        return runQuery("UPDATE alert SET welcome_channel = '" + welcome_channel + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean updateWelcomeRole(String guild_id, String bot_id, String welcome_role) {
        return runQuery("UPDATE alert SET welcome_role = '" + welcome_role + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean updateWelcomeMessage(String guild_id, String bot_id, String welcome_message) {
        return runQuery("UPDATE alert SET welcome_message = '" + welcome_message + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean toggleWelcome(String guild_id, String bot_id, boolean toggle) {
        return runQuery("UPDATE alert SET welcome_enabled = '" + (toggle ? "1" : "0") + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean hasLeave(String guild_id, String bot_id) {
        return fetchJRow("SELECT leave_message FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'").get("leave_message") != null;
    }

    public static ResultRow getLeave(String guild_id, String bot_id) {
        return fetchJRow("SELECT leave_message, leave_channel, leave_enabled FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean setLeave(String guild_id, String bot_id, String leave_channel, String leave_message) {
        return runQuery("INSERT INTO alert(guild_id, bot_id, leave_channel, leave_message, leave_enabled)"
            + "VALUES('" + guild_id + "','" + bot_id + "','" + leave_channel + "','" + leave_message + "','" + "1" + "') "
            + "ON DUPLICATE KEY UPDATE leave_channel = '" + leave_channel + "', leave_message = '" + leave_message + "', leave_enabled = '" + "1" + "';");
    }

    public static boolean deleteLeave(String guild_id, String bot_id) {
        return runQuery("UPDATE alert SET leave_message = NULL, leave_channel = NULL, leave_enabled = 0 WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'"); 
    }

    public static boolean updateLeaveChannel(String guild_id, String bot_id, String leave_message) {
        return runQuery("UPDATE alert SET leave_message = '" + leave_message + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean updateLeaveMessage(String guild_id, String bot_id, String leave_message) {
        return runQuery("UPDATE alert SET leave_message = '" + leave_message + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean toggleLeave(String guild_id, String bot_id, boolean toggle) {
        return runQuery("UPDATE alert SET leave_enabled = '" + (toggle ? "1" : "0") + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean hasBoost(String guild_id, String bot_id) {
        return fetchJRow("SELECT boost_message FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'").get("boost_message") != null;
    }

    public static ResultRow getBoost(String guild_id, String bot_id) {
        return fetchJRow("SELECT boost_message, boost_channel, boost_enabled FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean setBoost(String guild_id, String bot_id, String boost_channel, String boost_message) {
        return runQuery("INSERT INTO alert(guild_id, bot_id, boost_channel, boost_message, boost_enabled)"
            + "VALUES('" + guild_id + "','" + bot_id + "','" + boost_channel + "','" + boost_message + "','" + "1" + "') "
            + "ON DUPLICATE KEY UPDATE boost_channel = '" + boost_channel + "', boost_message = '" + boost_message + "', boost_enabled = '" + "1" + "';");
    }

    public static boolean deleteBoost(String guild_id, String bot_id) {
        return runQuery("UPDATE alert SET boost_message = NULL, boost_channel = NULL, boost_enabled = 0 WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "'"); 
    }

    public static boolean updateBoostChannel(String guild_id, String bot_id, String boost_message) {
        return runQuery("UPDATE alert SET boost_message = '" + boost_message + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean updateBoostMessage(String guild_id, String bot_id, String boost_message) {
        return runQuery("UPDATE alert SET boost_message = '" + boost_message + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean toggleBoost(String guild_id, String bot_id, boolean toggle) {
        return runQuery("UPDATE alert SET boost_enabled = '" + (toggle ? "1" : "0") + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static ResultRow getLevelUp(String guild_id, String bot_id) {
        return fetchJRow("SELECT levelup_message, levelup_enabled FROM alert WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean toggleLevelUp(String guild_id, String bot_id, boolean toggle) {
        return runQuery("INSERT INTO guild_settings(guild_id, bot_id, exp_enabled) VALUES ('" + guild_id + "', '" + bot_id + "', '" + (toggle ? "1" : "0") + "') ON DUPLICATE KEY UPDATE exp_enabled = '" + (toggle ? "1" : "0") + "';");
    }
    
    public static boolean updateLevelupMessage(String guild_id, String bot_id, String levelup_message) {
        return runQuery("INSERT INTO alert (guild_id, bot_id, levelup_message) VALUES ('" + guild_id + "', '" + bot_id + "', '" + levelup_message + "') ON DUPLICATE KEY UPDATE levelup_message = '" + levelup_message + "';");
    }

    public static ResultRow getRoomSettings(String guild_id, String room_id) {
        return fetchJRow("SELECT room_name, has_exp, exp_value, has_command_stats FROM alert WHERE guild_id = '" + guild_id + "' AND room_id = '" + room_id + "';");
    }

    public static boolean updateExpValue(String guild_id, String room_id, Double exp_value) {
        return runQuery("INSERT INTO rooms_settings (guild_id, room_id, exp_value)" 
            + " VALUES ('" + guild_id + "', '" + room_id + "', '" + exp_value + "')" 
            + " ON DUPLICATE KEY UPDATE exp_value = " + exp_value + ";");
    }

    public static boolean toggleLevelUpChannel(String guild_id, String room_id, boolean toggle) {
        return runQuery("INSERT INTO rooms_settings (guild_id, room_id, has_exp) "
            + "VALUES ('" + guild_id + "', '" + room_id + "', '" + (toggle ? "1": "0") + "') "
            + "ON DUPLICATE KEY UPDATE has_exp = " + (toggle ? "1": "0") +";");
    }

    public static boolean toggleBlacklist(String guild_id, String bot_id, boolean toggle) {
        return runQuery("UPDATE guild_settings SET blacklist_enabled = '" + toggle + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean deleteRoom(String guild_id, String room_id) {
        return runQuery("DELETE FROM rooms_settings WHERE guild_id = '" + guild_id + "' AND room_id = '" + room_id + "';");
    }


    public static boolean isExpEnabled(String guildId, String botId) {
        return runQuery("SELECT exp_enabled FROM guild_settings WHERE guild_id = '" + guildId + "' AND bot_id = '" + botId + "';");
    }


    public static boolean setPrefix(String guild_id, String bot_id, String prefix) {
        return runQuery("INSERT INTO guild_settings(guild_id, bot_id, prefix)" + "VALUES('" + guild_id + "','" + bot_id + "','" + prefix +"') ON DUPLICATE KEY UPDATE prefix = '" + prefix + "';");
    }

    public static boolean updatePrefix(String guild_id, String bot_id, String prefix) {
        return runQuery("UPDATE guild_settings SET prefix = '" + prefix + "' WHERE guild_id = '" + guild_id + "' AND bot_id = '" + bot_id + "';");
    }

    public static boolean setGreet(String user_id, String guild_id, String bot_id, String sound_id) {
        return runQuery("INSERT INTO greeting (user_id, guild_id, bot_id, sound_id) VALUES ('" + user_id + "', '" + guild_id + "', '" + bot_id + "', '" + sound_id + "') ON DUPLICATE KEY UPDATE sound_id = '" + sound_id + "';");
    }

    public static boolean deleteGreet(String user_id, String guild_id, String bot_id) {
        return runQuery("DELETE from greeting WHERE guild_id = '" + guild_id + "' AND user_id = '" + user_id + "' AND bot_id = '" + bot_id + "';");
    }
    

    public static boolean insertRewards(String guild_id, String role, String level, String message){
        return runQuery("INSERT INTO rewards_table (guild_id, role_id, level, message_text) VALUES ('" + guild_id + "', '" + role + "', '" + level + "', '" + message + "');");
    }
    
    public static QueryResult getRewards(String guild_id) {
        return safJQuery("SELECT role_id, level, message_text FROM rewards_table WHERE guild_id = '" + guild_id + "' ORDER BY level DESC;");
    }

    public static ResultRow getReward(String guild_id, int level) {
        return fetchJRow("SELECT role_id, message_text FROM rewards_table WHERE guild_id = '" + guild_id + "' and level = '" + level + "';");
    }

    public static boolean deleteReward(String role_id){
        return runQuery("DELETE FROM rewards_table WHERE role_id = '" + role_id + "';");
    }

    public static boolean setBlacklistChannel(String blacklist_channel, String guild_id, String bot_id) {
        return runQuery("UPDATE guild_settings SET blacklist_channel = '" + blacklist_channel + "' WHERE guild_id = '" + guild_id +  "' AND bot_id = '" + bot_id +  "';");
    }

    public static boolean setBlacklistThreshold(String threshold, String guild_id, String bot_id) {
        return runQuery("UPDATE guild_settings SET threshold = '" + threshold + "' WHERE guild_id = '" + guild_id +  "' AND bot_id = '" + bot_id +  "';");
    }

    public static boolean enableBlacklist(String guild_id, String bot_id, String threshold, String blacklist_channel) {
        return runQuery("INSERT INTO guild_settings(guild_id, bot_id, threshold, blacklist_channel, blacklist_enabled)" + "VALUES('" + guild_id + "','" + bot_id + "','" + threshold +"', '" + blacklist_channel + "', 1) ON DUPLICATE KEY UPDATE threshold = '" + threshold + "', blacklist_channel = '" + blacklist_channel + "', blacklist_enabled = 1;");
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

    public static QueryResult getGuildByThreshold(int threshold, String bot_id, String guild_id){
        return safJQuery("SELECT guild_id, blacklist_channel, threshold FROM guild_settings WHERE blacklist_enabled == 1 AND threshold <= '" + threshold + "' AND blacklist_channel IS NOT NULL AND guild_id != '" + guild_id + "' AND bot_id = '" + bot_id + "'");
    }

    public static boolean insertCommand(String guild_id, String bot_id, String author_id, String command, String args){
        return runQuery("INSERT INTO command_analytic(name, time, user_id, guild_id, bot_id, args) VALUES ('" + command + "', '" + new Timestamp(System.currentTimeMillis()) + "', '" + author_id + "', '"+ guild_id +"','"+ bot_id +"', '"+ args +"');");
    }

    public static int getBannedTimes(String user_id){
        return fetchJRow("SELECT count(user_id) as times from blacklist WHERE user_id = '" + user_id + "'").getAsInt("times");
    }
    
    public static int getBannedTimesInGuild(String guild_id){
        return fetchJRow("SELECT count(user_id) as times from blacklist WHERE guild_id = '" + guild_id + "'").getAsInt("times");
    }

    public static ResultRow getGreet(String user_id, String guild_id, String bot_id) {
        return fetchJRow("SELECT sound.id, sound.extension from greeting join sound on greeting.sound_id = sound.id WHERE greeting.user_id = '" + user_id + "' AND (greeting.guild_id = '" + guild_id + "' OR greeting.guild_id = '0') AND greeting.bot_id = '" + bot_id + "' ORDER BY CASE WHEN greeting.guild_id = '0' THEN 1 ELSE 0 END LIMIT 1;");
    }






    /** 
     * What the actual fuck sunyx?
     * eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee la cannuccia
     * weru9fgt9uehrgferwfghreyuio
    */
    public static String getCannuccia() {
        return ":cannuccia:";
    }
}