package com.safjnest.Utilities.SQL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryResult implements Iterable<ResultRow>{

    private List<ResultRow> result;

    public QueryResult(){
        this.result = new ArrayList<>();
    }

    @Override
    public Iterator<ResultRow> iterator() {
        return result.iterator();
    }

    public void add(ResultRow row){
        result.add(row);
    }

    public ResultRow get(int index){
        return result.get(index);
    }

    public int size(){
        return result.size();
    }

    public boolean isEmpty(){
        return result.isEmpty();
    }
    
}
