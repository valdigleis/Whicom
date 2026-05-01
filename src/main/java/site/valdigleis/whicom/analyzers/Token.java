/**
 * MIT License
 * 
 * Copyright (c) 2026 Valdigleis S Costa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the right
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package site.valdigleis.whicom.analyzers;

import java.util.Objects;

public class Token {
    
    public enum Type { ID, NUMBER, ASSIGN, PLUS, MINUS, PRODUCT, LESS, EQUAL, SEMICOLON, IF, THEN, ELSE, WHILE, SKIP, TRUE, FALSE, NOT, AND, OR, LEFTP, RIGHTP, LEFTK, RIGHTK, EOF }
    private final Type type;
    private final String lexeme;
    private final int line;
    private final int column;

    /**
     * Método que cria um token válido
     * 
     * @param lexeme O valor do token
     * @param type O tipo do token
     * @param line A linha em que o token aparece no código fonte
     * @param column A coluna em que o token aparece no código fonte
     */
    public Token(String lexeme, Type type, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    /**
     * Método que retorna o valor do lexema no token
     * 
     * @return lexeme
     */
    public String getLexeme() {
        return lexeme;
    }

    /**
     * Método que retorna o tipo do token
     * 
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Método que retorna o número da linha em que o token aparece no código fonte
     * 
     * @return linha
     */
    public int getLine() {
        return line;
    }

    /**
     * Método que retorna o número da coluna em que o token aparece no código fonte
     * 
     * @return coluna
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "<'" + lexeme + "', " + type  + ", line: " + this.line + ", column: " + this.column + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Token other = (Token) obj;
        return this.type == other.type && this.lexeme.equals(other.getLexeme()) && this.line == other.getLine() && this.column == other.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.lexeme, this.line, this.column);
    }
}
