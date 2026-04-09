package site.valdigleis.whicom.utils;

import java.util.HashMap;
import java.util.Map;

public class TableSymbols {

    private Map<String, Symbol> table = new HashMap<>();

    public void add(String name, String type, Object value) {
        if(!this.table.containsKey(name)){
            this.table.put(name, new Symbol(name, type, value));
        }
    }

    public Symbol lookup(String name) {
        return this.table.get(name);
    }

}
