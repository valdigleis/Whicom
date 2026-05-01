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
 * FITNESS FOR A PARTICULAR PURthis.refE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package site.valdigleis.whicom.analyzers;

import java.util.ArrayList;

/**
 * Implementação de um analisador léxico para uma variação da linguagem WHILE.
 * 
 * @version 1.0
 * @author Valdigleis (valdigleis@dimap.ufrn.br)
 * @see <a href="https://dl.acm.org/doi/epdf/10.1145/363235.363259" targt="_blank">Hoare Paper</a>
 */
public class Lexer {

    private final String word;
    private int ref;
    private int line;
    private int column;

    /**
     * Método que cria uma instância de um Lexer (Analisador léxico)
     * 
     * @param word O código que será analisado pelo Lexer
     */
    public Lexer(String word) {
        this.word = word;
        this.ref = 0;
        this.line = 1;
        this.column = 1;
    }

    /**
     * Método responsável por realizar a leitura de uma palavra e retorna a classe de token de tal palavra
     * 
     * @return O token que representa a palavra
     */
    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        int lineRef = this.line;
        int columnRef = this.column;
        while (this.ref < this.word.length() && Character.isLetterOrDigit(this.word.charAt(this.ref))) {
            sb.append(this.word.charAt(this.ref));
            this.advance();
        }
        String text = sb.toString();
        switch (text) {
            case "while" : 
                return new Token(text, Token.Type.WHILE, lineRef, columnRef);
            case "if":
                return new Token(text, Token.Type.IF, lineRef, columnRef);
            case "then":
                return new Token(text, Token.Type.THEN, lineRef, columnRef);
            case "else":
                return new Token(text, Token.Type.ELSE, lineRef, columnRef);
            case "skip":
                return new Token(text, Token.Type.SKIP, lineRef, columnRef);
            case "true":
                return new Token(text, Token.Type.TRUE, lineRef, columnRef);
            case "false":
                return new Token(text, Token.Type.FALSE, lineRef, columnRef);
            case "and":
                return new Token(text, Token.Type.AND, lineRef, columnRef);
            case "or":
                return new Token(text, Token.Type.OR, lineRef, columnRef);
            default:
                return new Token(text, Token.Type.ID, lineRef, columnRef);
        }
    }

    /**
     * Método responsável por realizar a leitura de um número e retorna a classe de token número
     * 
     * @return O token número que representa o número lido
     */
    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        int lineRef = this.line;
        int columnRef = this.column;
        while (this.ref < this.word.length() && Character.isDigit(this.word.charAt(this.ref))) {
            sb.append(this.word.charAt(this.ref));
            lineRef = this.line;
            columnRef = this.column;
            this.advance();
        }
        return new Token(sb.toString(), Token.Type.NUMBER, lineRef, columnRef);
    }

    /**
     * Método que constrói a lista de tokens a partir do código que criou o Lexer.
     * 
     * @return A lista de token
     * @throws RuntimeException Quando uma sequência de caracteres inválida é encontrada
     */
    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (this.ref < this.word.length()) {
            char current = this.word.charAt(this.ref);    
            int lineRef = this.line;
            int columnRef = this.column;
            if (Character.isWhitespace(current)) {
                this.advance();
                continue;
            } 
            // -------------------------------------------------------------------
            // Detecta os comentários mais não insere ambos na lista de tokens
            if (current == '/' && this.peek() == '/') {
                this.skipLineComment();
                continue;
            }
            if (current == '/' && peek() == '*') {
                this.skipBlockComment();
                continue;
            }
            // -------------------------------------------------------------------
            if (Character.isLetter(current)) {
                tokens.add(readIdentifier());
            }else if (Character.isDigit(current)) {
                tokens.add(readNumber());
            } else if (current == ':') {
                tokens.add(new Token(":", Token.Type.ASSIGN, lineRef, columnRef));
                this.advance();
            } else if (current == '=') {
                tokens.add(new Token("=", Token.Type.EQUAL, lineRef, columnRef));
                this.advance();
            } else if (current == '<') {
                tokens.add(new Token("<", Token.Type.LESS, lineRef, columnRef));
                this.advance();
            } else if (current == '+') {
                tokens.add(new Token("+", Token.Type.PLUS, lineRef, columnRef));
                this.advance();
            } else if (current == '-') {
                tokens.add(new Token("-", Token.Type.MINUS, lineRef, columnRef));
                this.advance();
            } else if (current == '*') {
                tokens.add(new Token("*", Token.Type.PRODUCT, lineRef, columnRef));
                this.advance();
            } else if (current == ';') {
                tokens.add(new Token(";", Token.Type.SEMICOLON, lineRef, columnRef));
                this.advance();
            } else if (current == '!') {
                tokens.add(new Token("!", Token.Type.NOT, lineRef, columnRef));
                this.advance();
            } else if (current == '(') {
                tokens.add(new Token("(", Token.Type.LEFTP, lineRef, columnRef));
                this.advance();
            } else if (current == ')') {
                tokens.add(new Token(")", Token.Type.RIGHTP, lineRef, columnRef));
                this.advance();
            } else if (current == '{') {
                tokens.add(new Token("{", Token.Type.LEFTK, lineRef, columnRef));
                this.advance();
            } else if (current == '}') {
                tokens.add(new Token("}", Token.Type.RIGHTK, lineRef, columnRef));
                this.advance();
            } else {
                throw new RuntimeException("Unexpected character: " + current);
            }
        }
        tokens.add(new Token("", Token.Type.EOF, this.line, this.column));
        return tokens;
    }

    /**
     * Método responsável por manter os valores de localização (linha, coluna) das palavras no código atualizadas.
     */
    private void advance() {
        if (ref >= this.word.length()) {
            return;
        }
        char c = word.charAt(ref);
        this.ref++;
        if (c == '\n') {
            this.line++;
            this.column = 1;
        } else {
            this.column++;
        }
    }

    /**
     * Método que retorna (sem apagar) o caractere que está sendo usado (mapeado pelo <em>lookahead</em>) para forma palavras ou números no analisador lexico
     * 
     * @return O caractere apontado no lookahead do analisador léxico
     */
    private char peek() {
        if (this.ref + 1 >= this.word.length()) {
            return '\0';
        }
        return this.word.charAt(this.ref + 1);
    }

    /**
     * Método responsável por analisar os comentários simples (de linha única)
     */
    private void skipLineComment() {
        this.advance();
        this.advance();
        while (this.ref < word.length() && word.charAt(ref) != '\n') {
            this.advance();
        }
    }

    /**
     * Método responsável por analisar os comentários múltiplos (de múltiplas de linhas)
     */
    private void skipBlockComment() {
        this.advance();
        this.advance();
        while (this.ref < word.length()) {
            if (word.charAt(ref) == '*' && peek() == '/') {
                advance(); // '*'
                advance(); // '/'
                return;
            }
            this.advance();
        }
        throw new RuntimeException("Comment from an unclosed block");
    }

}
