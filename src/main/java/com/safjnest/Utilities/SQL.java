package com.safjnest.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.postgresql.util.PSQLException;

/**
 * Contains all the method to comunicate with SafJNest Database.
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 * @since 2.0
 */
public class SQL {
    /** Object that opens the connection between database and beeby */
    Connection c;

    /**
     * Constructor
     * 
     * @param hostName Hostname, as 'keria123.eu-west-1.compute.fakerAws.com'
     * @param database Name of the database to connect in
     * @param user Username
     * @param password Password
     */
    public SQL(String hostName, String database, String user, String password){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager
                .getConnection("jdbc:mysql://"+hostName+"/"+ database + "?autoReconnect=true",user,password);
            System.out.println("[SQL] INFO Connection to the extreme db successful!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.out.println("[SQL] INFO Connection to the extreme db ANNODAM!");;
        }
    }

    /**
     * Run a query that not return anything. INSERT, CREATE, DELETE, DROP, UPDATE
     * @param query
     * @return
     * True if the query has been run correctly, otherwise false.
     */
    public boolean runQuery(String query){
        Statement stmt;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(query);
            stmt.close();
            return true;
        }
        catch (PSQLException e) {e.printStackTrace(); return true;}
        catch (SQLException e1) {e1.printStackTrace(); return false;}
    }

    /**
     * Run a query and return a string, so just one element in a row in a specific column.
     * @param query query to be run 
     * @param nameCol name of the column to get element
     * @return
     * A {@code String} if the query is run correctly, otherwise {@code null}
     */
    public String getString(String query, String nameCol){
        Statement stmt;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            String info = rs.getString(nameCol);
            rs.next();
            rs.close();
            stmt.close();
            return info;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * Run a query and return an array of strings, so every rows with specified a column.
     * @param query query to be run 
     * @param nameCol name of the column 
     * @return
     * An {@code ArrayList<String>} if the query is run correctly, otherwise {@code null}
     */
     public ArrayList<String> getAllRowsSpecifiedColumn(String query, String nameCol){
        Statement stmt;
        ArrayList<String> arr = new ArrayList<>();
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                arr.add(rs.getString(nameCol));
            }
            rs.close();
            stmt.close();
            return arr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   /**
     * Run a query and return an array of array of strings, so every row with a specified column.
     * <p>For example, if the row has 5 column and you ask for three, this method will return every row with the first
     * three column/element.</p>
     * <table border="1">
    <tr>
        <td> Name </td> <td> Guild_id</td> <td> bot_id </td>
    </tr>
    <tr>
        <td> server_1 </td> <td> 123 </td> <td> 123 </td>
    </tr>
    <tr>
        <td> server_2 </td> <td> 345</td> <td> 123 </td>
    </tr>
    <tr>
        <td> server_3 </td> <td> 678</td> <td> 345 </td>
    </tr>
    </table>
    Given this line:
    <pre>
    {@code
    String query = SELECT * from table;
    Arraylist<Arraylist<String>> arr = SQL.getTuple(query, 2);
    }    
    </pre>
    The output would be {@code server_1, 123 - server_2, 345...}
     * @param query query to be run 
     * @param nCol number of the column to get
     * @return
     * An {@code ArrayList<ArrayList<String>>} if the query is run correctly, otherwise {@code null}
     */
    public ArrayList<ArrayList<String>> getAllRows(String query, int nCol){
        Statement stmt;
        ArrayList<ArrayList<String>> arr = new ArrayList<>();
        
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                arr.add(new ArrayList<>());
                for(int i = 1; i <= nCol; i++){
                    arr.get(arr.size()-1).add(rs.getString(i));
                }
            }
            rs.close();
            stmt.close();
            return arr;
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<ArrayList<String>> getAllRows(String query){
        Statement stmt;
        ArrayList<ArrayList<String>> arr = new ArrayList<>();
        
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            arr.add(new ArrayList<>());
            for(int i = 1; i <= rsmd.getColumnCount(); i++)
                arr.get(arr.size()-1).add(rsmd.getColumnName(i));
            while(rs.next()){
                arr.add(new ArrayList<>());
                for(int i = 0; i < rsmd.getColumnCount(); i++)
                    arr.get(arr.size()-1).add(rs.getString(i + 1));
            }
            rs.close();
            stmt.close();
            return arr;
        } catch (SQLException e) {
            return null;
        }
    }
    

    /**
     * Run a query and return an array of strings, so every column with a specified row.
     * <p>For example, if the query returns 5 row, this method will return a specified row with
     * all the elements</p>
     * <table border="1">
    <tr>
        <td> Name </td> <td> Guild_id</td> <td> bot_id </td>
    </tr>
    <tr>
        <td> server_1 </td> <td> 123 </td> <td> 123 </td>
    </tr>
    <tr>
        <td> server_2 </td> <td> 345</td> <td> 123 </td>
    </tr>
    <tr>
        <td> server_3 </td> <td> 678</td> <td> 345 </td>
    </tr>
    </table>
    Given this line:
    <pre>
    {@code
    String query = SELECT * from table;
    Arraylist<String> arr = SQL.getSpecifiedRow(query, 1);
    }    
    </pre>
    The output would be {@code server_2, 345, 123}
     * @param query query to be run 
     * @param nRow number of the row to get
     * @return
     * An {@code ArrayList<String>} if the query is run correctly, otherwise {@code null}
     */
    public ArrayList<String> getSpecifiedRow(String query, int nRow){
        Statement stmt;
        ArrayList<ArrayList<String>> arr = new ArrayList<>();
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()){
                arr.add(new ArrayList<>());
                try {
                    for(int i = 1; 1 > 0; i++) 
                        arr.get(arr.size()-1).add(rs.getString(i));
                } catch (Exception e) {}
            }
            rs.close();
            stmt.close();
            return arr.get(nRow);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Run a query and return an array of strings, so every column with a specified row.
     * <p>For example, if the query returns 5 row, this method will return a specified row with
     * all the elements</p>
     * <table border="1">
    <tr>
        <td> Name </td> <td> Guild_id</td> <td> bot_id </td>
    </tr>
    <tr>
        <td> server_1 </td> <td> 123 </td> <td> 123 </td>
    </tr>
    <tr>
        <td> server_2 </td> <td> 345</td> <td> 123 </td>
    </tr>
    <tr>
        <td> server_3 </td> <td> 678</td> <td> 345 </td>
    </tr>
    </table>
    Given this line:
    <pre>
    {@code
    String query = SELECT * from table;
    Arraylist<String> arr = SQL.getSpecifiedRow(query, 1);
    }    
    </pre>
    The output would be {@code server_2, 345, 123}
     * @param query query to be run 
     * @param nRow number of the row to get
     * @return
     * An {@code ArrayList<String>} if the query is run correctly, otherwise {@code null}
     */
    public ArrayList<ArrayList<String>> getSpecifiedRowBetween(String query, int start, int end){
        Statement stmt;
        ArrayList<ArrayList<String>> arr = new ArrayList<>();
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            int contStart = 0;
            int contEnd = 0;
            while(rs.next()){
                if(contStart == start){
                    if(contEnd == end){
                        break;
                    }
                    arr.add(new ArrayList<>());
                    contEnd++;
                    try {
                        for(int i = 1; 1 > 0; i++) 
                            arr.get(arr.size()-1).add(rs.getString(i));
                    } catch (Exception e) {}
                }else{
                    contStart++;
                }
            }
            rs.close();
            stmt.close();
            return arr;
        } catch (Exception e) {
            return null;
        }
    }
}           