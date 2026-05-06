package site.valdigleis.whicom.frontend.utils;

public record Token(TokenType type, String lexeme, int line, int column) {
    @Override
    public String toString() {
        return "(" +  this.lexeme + ", " + this.type + ", line: " + this.line + ", column: " + this.column  + ")";
    }
}
