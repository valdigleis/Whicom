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

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int lookahead = 0;

    public Parser(List<Token> tokens) { 
        this.tokens = tokens; 
    }

    private Token peek() { 
        return tokens.get(this.lookahead); 
    }

    /**
     * 
     * Consume tokens
     * 
     * @param type
     */
    private void consume(Token.Type type) {
        if (this.peek().getType() == type) {
            this.lookahead++;
        } else {
            throw new RuntimeException("Error: Expected " + type + " but it came " + peek().getType());
        }
    }

    public void parse() {
        while (this.peek().getType() != Token.Type.EOF) {
            this.commands();
        }
    }

    private void commands() {
        if(this.peek().getType() == Token.Type.ID){
            this.consume(Token.Type.ID);
            this.consume(Token.Type.ASSIGN);
            this.arithmeticsExp();
        } else if (this.peek().getType() == Token.Type.SKIP){
            this.consume(Token.Type.SKIP); 
        } else if (this.peek().getType() == Token.Type.IF) {
            this.consume(Token.Type.IF);
            this.booleanExp();
            this.consume(Token.Type.THEN);
            this.commands();
            this.consume(Token.Type.ELSE);
            this.commands();
        } else if (this.peek().getType() == Token.Type.PUNCTUATION) {
            this.consume(Token.Type.PUNCTUATION);
            if (this.peek().getType() != Token.Type.EOF && peek().getType() != Token.Type.ELSE) {
                this.commands();
            }
        }
    }

    private void arithmeticsExp() {
        this.term();
        while (this.peek().getType() == Token.Type.PLUS || this.peek().getType() == Token.Type.MINUS) {
            this.consume(peek().getType());
            this.term();
        }
    }

    private void term() {
        this.factor();
        while (this.peek().getType() == Token.Type.PRODUCT) {
            this.consume(Token.Type.PRODUCT);
            this.factor();
        }
    }

    private void factor() {
        if (this.peek().getType() == Token.Type.ID) { 
            this.consume(Token.Type.ID);
        } else if (peek().getType() == Token.Type.NUMBER) {
            this.consume(Token.Type.NUMBER);
        } else {
            throw new RuntimeException("Invalid factor");
        }
    }

    private void booleanExp() {
        if (this.peek().getType() == Token.Type.TRUE) { 
            this.consume(Token.Type.TRUE);
        } else if (this.peek().getType() == Token.Type.FALSE) { 
            this.consume(Token.Type.FALSE);
        } else if (this.peek().getType() == Token.Type.NOT) { 
            this.consume(Token.Type.NOT);
            this.booleanExp();
        } else {
            this.arithmeticsExp();
            if (peek().getType() == Token.Type.EQUAL) {
                this.consume(Token.Type.EQUAL);
                this.arithmeticsExp();
            } else if (peek().getType() == Token.Type.LESS) {
                this.consume(Token.Type.LESS);
                this.arithmeticsExp();
            } else if (peek().getType() == Token.Type.LESS) {
                this.consume(Token.Type.LA);
                this.arithmeticsExp();
            }
        }

        if (this.peek().getType() == Token.Type.AND || this.peek().getType() == Token.Type.OR) {
            this.consume(peek().getType());
            this.booleanExp();
        }
    }


}
