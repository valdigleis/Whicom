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

public class Lexer {

    private final String word;
    private int ref;
    private int line;
    private int column;

    public Lexer(String word) {
        this.word = word;
        this.ref = 0;
        this.line = 1;
        this.column = 1;
    }

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

    private char peek() {
        if (this.ref + 1 >= this.word.length()) {
            return '\0';
        }
        return this.word.charAt(ref + 1);
    }

    private void skipLineComment() {
        this.advance();
        this.advance();
        while (this.ref < word.length() && word.charAt(ref) != '\n') {
            this.advance();
        }
    }

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
