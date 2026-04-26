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
     * @param type O tipo de token para ser consumido
     * 
     * @throws RuntimeException Lançada quando o token apontato pelo lookahead não é do mesmo tipo do token que deve ser consumido
     */
    private void consume(Token.Type type) {
        if (this.peek().getType() == type) {
            this.lookahead++;
        } else {
            throw new RuntimeException("Error: Expected " + type + " but it came " + peek().getType());
        }
    }

    /**
     * Método que "starta" o processo de análise sintática
     */
    public void parse() {
        while (this.peek().getType() != Token.Type.EOF) {
            this.commands();
        }
    }

    /**
     * Método que realiza a análise dos comandos (C) da linguagem WHILE, ou seja, este é a tradução diretas da regras:<br>
     * 
     * C &Rightarrow; id:E;C | skip;C | if (B) { C } else { C } | if (B) { C } else { C } C |  While (B) { cmd } |  While (B) { cmd } C | &lambda;
     */
    private void commands() {
        if(this.peek().getType() == Token.Type.ID){
            this.consume(Token.Type.ID);
            this.consume(Token.Type.ASSIGN);
            this.arithmeticsExp();
            this.consume(Token.Type.PUNCTUATION);
        } else if (this.peek().getType() == Token.Type.SKIP){
            this.consume(Token.Type.SKIP);
            this.consume(Token.Type.PUNCTUATION);
        } else if (this.peek().getType() == Token.Type.IF) {
            this.consume(Token.Type.IF);
            this.consume(Token.Type.LEFTP);
            this.booleanExp();
            this.consume(Token.Type.RIGHTP);
            this.consume(Token.Type.THEN);
            this.consume(Token.Type.LEFTK);
            this.commands();
            this.consume(Token.Type.RIGHTK);
            this.consume(Token.Type.ELSE);
            this.consume(Token.Type.LEFTK);
            this.commands();
            this.consume(Token.Type.RIGHTK);
        } else if (this.peek().getType() == Token.Type.WHILE) {
            this.consume(Token.Type.WHILE);
            this.consume(Token.Type.LEFTP);
            this.booleanExp();
            this.consume(Token.Type.RIGHTP);
            this.consume(Token.Type.LEFTK);
            this.commands();
            this.consume(Token.Type.RIGHTK);
        } else {
            throw new RuntimeException("Commands cannot start with the tokien " + this.peek().toString());
        }
        // Trata as sequências de comandos
        Token.Type next = this.peek().getType();
        if (next == Token.Type.ID || next == Token.Type.SKIP || next == Token.Type.IF || next == Token.Type.WHILE) {
            this.commands();
        }
    }

    /**
     * Método que realiza a análise das expressões aritméticas (E), ou seja, este é a tradução direta da regra:<br>
     * 
     * E &Rightarrow; TE'
     */
    private void arithmeticsExp() {
        this.term();
        this.expLine();
    }

    /**
     * Método que realiza a análise dos sutermos aditivos (E') aninhados a uma expressão aritmética, ou seja, este método é a tradução direta das regras gramaticais:<br> 
     * E' &Rightarrow; +TE' | &lambda;
     */
    private void expLine() {
        if(this.peek().getType() == Token.Type.PLUS) {
            this.consume(Token.Type.PLUS);
            this.term();
            this.expLine();
        }
    }

    /**
     * Método que realiza a análise subtermo (T), ou seja, este método é a tradução direta da regra gramatical:<br> 
     * T &Rightarrow; GT'
     */
    private void term() {
        this.factor();
        this.termLine();
    }

    /**
     * Método que realiza a análise dos sutermos multiplicativos aninhados a um termo T', ou seja, este método é a tradução direta das regras gramaticais:<br> 
     * T' &Rightarrow; *GT' | &lambda;
     */
    private void termLine() {
        if(this.peek().getType() == Token.Type.PRODUCT) {
            this.consume(Token.Type.PRODUCT);
            this.factor();
            this.expLine();
        }
    }

    /** 
     * Método que consome um fator de uma expressão aritmética, quando o fator for válido (ou seja, quando o fator é um ID ou um valor numérico).
     * 
     * @throws RuntimeException Lançada quando o fator não é válido!
     * */
    private void factor() {
        if(this.peek().getType() == Token.Type.ID) {
            this.consume(Token.Type.ID);
        } else if(this.peek().getType() == Token.Type.NUMBER) {
            this.consume(Token.Type.NUMBER);
        } else {
            throw new RuntimeException(this.peek().getType() + "Not is valid factor!");
        }
    }

    private void booleanExp(){
        this.termBoolean();
        this.termOrBoolean();
    }

    private void termBoolean(){
        this.booleanFactor();
        this.termAndBoolean();
    }

    private void termAndBoolean(){
        if(this.peek().getType() == Token.Type.AND){
            this.consume(Token.Type.AND);
            this.booleanFactor();
            this.termAndBoolean();
        }
    }

    private void termOrBoolean(){
        if(this.peek().getType() == Token.Type.OR){
            this.consume(Token.Type.OR);
            this.termBoolean();
            this.termOrBoolean();
        }
    }

    private void booleanFactor(){
        if(this.peek().getType() == Token.Type.NOT){
            this.consume(Token.Type.NOT);
            this.booleanFactor();
        } else if (this.peek().getType() == Token.Type.LEFTP){
            this.consume(Token.Type.LEFTP);
            this.booleanExp();
            this.consume(Token.Type.RIGHTP);
        } else {
            if (this.peek().getType() == Token.Type.TRUE){
                this.consume(Token.Type.TRUE);
            } else if (this.peek().getType() == Token.Type.FALSE) {
                this.consume(Token.Type.FALSE);
            } else if (this.peek().getType() == Token.Type.ID){
                this.consume(Token.Type.ID);
            } else if (this.peek().getType() == Token.Type.NUMBER){
                this.consume(Token.Type.NUMBER);
            } else {
                throw new RuntimeException("Unexpected token in boolean expression: " + peek().getType());
            }
            this.relations();
        }
         
    }

    private void relations() {
        if (this.peek().getType() == Token.Type.LESS) {
            this.consume(Token.Type.LESS);
            this.relationFactor();
        } else if (this.peek().getType() == Token.Type.EQUAL) {
            this.consume(Token.Type.EQUAL);
            this.relationFactor();
        }
    }

    private void relationFactor(){
        Token.Type type = this.peek().getType();
        if (type == Token.Type.ID || type == Token.Type.NUMBER || 
            type == Token.Type.TRUE || type == Token.Type.FALSE) {
            this.consume(type);
        } else {
            throw new RuntimeException("Expected value after relational operator, but found " + type);
        }
    }

}
