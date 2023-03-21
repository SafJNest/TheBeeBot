package com.safjnest.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
     * Run a query and return a string, so just an element in a row.
     * @param query query to be run 
     * @param nameCol name of the row to get element
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
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Run a query and return an array of strings, so an entire row.
     * @param query query to be run 
     * @param nameCol name of the row 
     * @return
     * An {@code ArrayList<String>} if the query is run correctly, otherwise {@code null}
     */
     public ArrayList<String> getListString(String query, String nameCol){
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
    * Keria CHI?
    *
    *
     */
    public ArrayList<ArrayList<String>> getTuple(String query, int nCol){
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
    /**
    * Guma CHI?
    * 
    *
    */
    public ArrayList<String> getRealTuple(String query, int nRow){
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
}           