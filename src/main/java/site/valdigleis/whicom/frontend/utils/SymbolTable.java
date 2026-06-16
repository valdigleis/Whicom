package site.valdigleis.whicom.frontend.utils;

import java.util.HashMap;

public class SymbolTable {

    private final HashMap<String, Symbol> symbols = new HashMap<>();

    public void insert(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public Symbol lookup(String name) {
        return symbols.get(name);
    }

    public boolean contains(String name) {
        return symbols.containsKey(name);
    }

    public HashMap<String, Symbol> getSymbols() {
        return symbols;
    }
}
