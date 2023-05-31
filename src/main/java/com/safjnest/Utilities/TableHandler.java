package com.safjnest.Utilities;

import java.util.ArrayList;

import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.JDA;

public class TableHandler {

    public static void replaceIdsWithNames(String[][] data, JDA jda) {
        for(int i = 0; i < data.length; i++){
            for(int j = 0; j < data[i].length; j++){
                if(data[i][j] == null) data[i][j] = "";
                if(data[i][j].matches("\\d+")) {
                    if(jda.getUserById(data[i][j]) != null){
                        data[i][j] = jda.getUserById(data[i][j]).getName(); 
                        data[i][j] = EmojiParser.removeAllEmojis(data[i][j]);
                    }
                    else if(jda.getGuildById(data[i][j]) != null){
                        data[i][j] = jda.getGuildById(data[i][j]).getName();
                        data[i][j] = EmojiParser.removeAllEmojis(data[i][j]);
                    }
                }
            }
        }
    }

    public static String constructTable(String[][] data, String[] headers, int numColumns, int numRows ) {
        // determinazione della larghezza delle colonne
        int[] colWidths = new int[numColumns];
        for (int col = 0; col < numColumns; col++) {
            colWidths[col] = headers[col].length();
            for (int row = 0; row < numRows; row++) {
                if (data[row][col].length() > colWidths[col]) {
                    colWidths[col] = data[row][col].length();
                }
            }
        }
    
        // costruzione della tabella
        StringBuilder table = new StringBuilder();
        table.append("┌");
        for (int col = 0; col < numColumns; col++) {
            table.append("─".repeat(colWidths[col] + 2));
            if (col < numColumns - 1) {
                table.append("┬");
            }
        }
        table.append("┐\n");
    
        // stampa delle intestazioni delle colonne
        table.append("│ ");
        for (int col = 0; col < numColumns; col++) {
            table.append(String.format("%-" + colWidths[col] + "s", headers[col]));
            if (col < numColumns - 1) {
                table.append(" │ ");
            }
        }
        table.append(" │\n");
    
        // riga divisoria
        table.append("├");
        for (int col = 0; col < numColumns; col++) {
            table.append("─".repeat(colWidths[col] + 2));
            if (col < numColumns - 1) {
                table.append("┼");
            }
        }
        table.append("┤\n");
    
        // stampa dei dati
        for (int row = 0; row < numRows; row++) {
            table.append("│ ");
            for (int col = 0; col < numColumns; col++) {
                table.append(String.format("%-" + colWidths[col] + "s", data[row][col]));
                if (col < numColumns - 1) {
                    table.append(" │ ");
                }
            }
            table.append(" │\n");
        }
    
        // riga inferiore
        table.append("└");
        for (int col = 0; col < numColumns; col++) {
            table.append("─".repeat(colWidths[col] + 2));
            if (col < numColumns - 1) {
                table.append("┴");
            }
        }
        table.append("┘\n");
        return table.toString();
    }

    public static String constructTable(String[][] data, String[] headers) {
        return constructTable(data, headers, headers.length, data.length);
    }

    public static String[] splitTable(String table, int maxSplits) {
        String[] splittedTable = table.split("\n");
        ArrayList<String> splitty = new ArrayList<String>();
        
        String temp = "";
        for(int i = 0; i < splittedTable.length; i++){
            if(temp.length() + splittedTable[i].length() < 1950)
                temp += splittedTable[i] + "\n";
            else{
                splitty.add(temp);
                temp = splittedTable[i] + "\n";
            }
        }
        splitty.add(temp);

        int splitLength = Math.min(splitty.size(), maxSplits);
        String[] split = new String[splitLength];
        for(int i = 0; i < splitLength; i++)
            split[i] = splitty.get(i);
        return split;
    }

    public static String[] splitTable(String table) {
        String[] splittedTable = table.split("\n");
        ArrayList<String> splitty = new ArrayList<String>();
        
        String temp = "";
        for(int i = 0; i < splittedTable.length; i++){
            if(temp.length() + splittedTable[i].length() < 1950)
                temp += splittedTable[i] + "\n";
            else{
                splitty.add(temp);
                temp = splittedTable[i] + "\n";
            }
        }
        splitty.add(temp);

        String[] split = new String[splitty.size()];
        split = splitty.toArray(split);
        return split;
    }
}
