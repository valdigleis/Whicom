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

    public Lexer(String word) {
        this.word = word;
        this.ref = 0;
    }

    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (this.ref < this.word.length() && Character.isLetterOrDigit(this.word.charAt(this.ref))) {
            sb.append(this.word.charAt(this.ref));
            this.ref++;
        }
        String text = sb.toString();
        switch (text) {
            case "if":   return new Token(text, Token.Type.IF);
            case "then": return new Token(text, Token.Type.THEN);
            case "else": return new Token(text, Token.Type.ELSE);
            case "skip": return new Token(text, Token.Type.SKIP);
            case "true": return new Token(text, Token.Type.TRUE);
            case "false": return new Token(text, Token.Type.FALSE);
            case "and": return new Token(text, Token.Type.AND);
            case "or": return new Token(text, Token.Type.OR);
            default:     return new Token(text, Token.Type.ID);
        }
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        while (this.ref < this.word.length() && Character.isDigit(this.word.charAt(this.ref))) {
            sb.append(this.word.charAt(this.ref));
            this.ref++;
        }
        return new Token(sb.toString(), Token.Type.NUMBER);
    }

    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (this.ref < this.word.length()) {
            char current = this.word.charAt(this.ref);
            if (Character.isWhitespace(current)) {
                this.ref++;
                continue;
            }
            if (Character.isLetter(current)) {
                tokens.add(readIdentifier());
            }else if (Character.isDigit(current)) {
                tokens.add(readNumber());
            } else if (current == ':') {
                tokens.add(new Token(":", Token.Type.ASSIGN));
                this.ref++;
            } else if (current == '=') {
                tokens.add(new Token("=", Token.Type.EQUAL));
                this.ref++;
            } else if (current == '<') {
                tokens.add(new Token("<", Token.Type.LESS));
                this.ref++;
            } else if (current == '+') {
                tokens.add(new Token("+", Token.Type.PLUS));
                this.ref++;
            } else if (current == '-') {
                tokens.add(new Token("-", Token.Type.MINUS));
                this.ref++;
            } else if (current == '*') {
                tokens.add(new Token("*", Token.Type.PRODUCT));
                this.ref++;
            } else if (current == ';') {
                tokens.add(new Token(";", Token.Type.PUNCTUATION));
                this.ref++;
            } else if (current == '!') {
                tokens.add(new Token("!", Token.Type.NOT));
                this.ref++;
            } else if (current == '(') {
                tokens.add(new Token("(", Token.Type.LEFTP));
                this.ref++;
            } else if (current == ')') {
                tokens.add(new Token(")", Token.Type.RIGHTP));
                this.ref++;
            } else if (current == '{') {
                tokens.add(new Token("{", Token.Type.LEFTK));
                this.ref++;
            } else if (current == '}') {
                tokens.add(new Token("}", Token.Type.RIGHTK));
                this.ref++;
            } else {
                throw new RuntimeException("Unexpected character: " + current);
            }
        }
        tokens.add(new Token("", Token.Type.EOF));
        return tokens;
    }
}
