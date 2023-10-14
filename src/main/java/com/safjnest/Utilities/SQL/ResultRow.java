package com.safjnest.Utilities.SQL;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ResultRow {
    Map<String, String> row;

    public ResultRow(Map<String, String> row){
        this.row = row;
    }


    /**
     * Default construct to return an empty ResultRow if the query returns nothing.
     * <p>
     * If the {@link com.safjnest.Utilities.SQL.ResultRow query} is null, the method {@code row.get(String)} will throw a NullPointerException.
     */
    public ResultRow(){
        this.row = new HashMap<String, String>();
    }

    public String get(String columnName){
        try {
            return row.get(columnName);
        } catch (Exception e) {
            return null;
        }
    }

    public void put(String key, String value) {
        this.row.put(key, value);
    }

    public int getAsInt(String columnName){
        try {
            return Integer.parseInt(row.get(columnName));
        } catch (Exception e) {
            return 0;
        }
    }

    public long getAsLong(String columnName){
        try {
            return Long.parseLong(row.get(columnName));
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean getAsBoolean(String columnName){
        return "1".equals(row.get(columnName)) || "true".equalsIgnoreCase(row.get(columnName));
    }

    public double getAsDouble(String columnName) {
        try {
            return Double.parseDouble(row.get(columnName));
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public int size() {
        return row.size();
    }

    public boolean isEmpty(){
        return row.isEmpty();
    }

    public boolean emptyValues(){
        for(String value : row.values()){
            if(value != null && !value.isEmpty())
                return false;
        }
        return true;
    }

    public String[] toArray() {
        String[] array = new String[row.size()];
        int i = 0;
        for(String col : row.values()) {
            array[i] = col;
            i++;
        }
        return array;
    }

    public Set<String> keySet(){
        return row.keySet();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> col : row.entrySet())
            sb.append(col.getKey()).append(": ").append(col.getValue());
        return sb.toString();
    }
}
