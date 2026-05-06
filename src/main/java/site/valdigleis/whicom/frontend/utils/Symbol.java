package site.valdigleis.whicom.frontend.utils;

public class Symbol {
    private final String name;     // Nome do identificador (ex: "x")
    private final SymbolKind kind; // Categoria (ex: "variável", "constante") aqui será só variáveis
    private String type;     // Tipo (ex: "int", "bool")
    private Object value;           // Valor na variável x
    //private int scopeLevel;       // Nível de escopo (0 = global, 1 = local...) Não usado por hora

    public Symbol(String name, SymbolKind kind, String type, String value) {
        this.name = name;
        this.kind = kind;
        this.type = type;
        this.value = value;
    }

    public String getName() { 
        return this.name; 
    }
    
    public SymbolKind getKind() { 
        return this.kind; 
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() { 
        return type; 
    }

    public void setValue(Object value) { 
        this.value = value;
    }

    public Object getValue() { 
        return this.value; 
    }


    @Override
    public String toString() {
        return "Symbol[name='%s', category='%s', type='%s', value=%s]"
                .formatted(this.name, this.kind, this.type, this.value);
    }
}
