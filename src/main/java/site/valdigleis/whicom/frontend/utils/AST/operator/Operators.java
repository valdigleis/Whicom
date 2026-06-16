package site.valdigleis.whicom.frontend.utils.AST.operator;

public class Operators {

    private Operators() {}

    public enum Unary {
        NOT
    }

    public enum Binary {
        PLUS,
        MINUS,
        TIMES,
        AND,
        OR
    }

    public enum Relational {
        EQUAL,
        LESS_THAN
    }
}
