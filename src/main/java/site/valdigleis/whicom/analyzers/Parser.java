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

import java.util.ArrayList;
import java.util.List;

import site.valdigleis.whicom.ast.Assign;
import site.valdigleis.whicom.ast.Binary;
import site.valdigleis.whicom.ast.Block;
import site.valdigleis.whicom.ast.Cmd;
import site.valdigleis.whicom.ast.Conditional;
import site.valdigleis.whicom.ast.Expr;
import site.valdigleis.whicom.ast.Literal;
import site.valdigleis.whicom.ast.Loop;
import site.valdigleis.whicom.ast.Skip;
import site.valdigleis.whicom.ast.Unary;
import site.valdigleis.whicom.ast.Variable;

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
            throw new RuntimeException(
                "Error at line " + this.peek().getLine() +
                ", column " + this.peek().getColumn() +
                ": expected " + type +
                " but found " + this.peek().getType()
            );
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    //
    // Os métodos abaixo eles fazem apenas a análise sintática
    //
    // -------------------------------------------------------------------------------------------------------------------------------------

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
            this.consume(Token.Type.SEMICOLON);
        } else if (this.peek().getType() == Token.Type.SKIP){
            this.consume(Token.Type.SKIP);
            this.consume(Token.Type.SEMICOLON);
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
            throw new RuntimeException("Commands cannot start with the tokien: " + this.peek().toString());
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
        } else if (this.peek().getType() == Token.Type.MINUS) {
            this.consume(Token.Type.MINUS);
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
            this.termLine();
        }
    }

    /** 
     * Método que consome um fator de uma expressão aritmética, quando o fator for válido (ou seja, quando o fator é um ID ou um valor numérico). Ou seja, implementa as regras:<br>
     * 
     * F  &Rightarrow;  ID | NUMBER | (E)<br>
     * 
     * onde NUMBER é um lexema de um número natural e ID é o lexema de um identificador válido.
     * 
     * @throws RuntimeException Lançada quando o fator não é válido!
     * */
    private void factor() {
        if(this.peek().getType() == Token.Type.ID) {
            this.consume(Token.Type.ID);
        } else if(this.peek().getType() == Token.Type.NUMBER) {
            this.consume(Token.Type.NUMBER);
        } else if (this.peek().getType() == Token.Type.LEFTP) {
            this.consume(Token.Type.LEFTP);
            this.arithmeticsExp();
             this.consume(Token.Type.RIGHTP);
        } else {
            throw new RuntimeException("Error in line: " + this.peek().getLine() + " column: " + this.peek().getColumn() + "caused by " + this.peek().getLexeme() + " cannot used an arithmetic expression");
        }
    }

    /**
     * Método que realiza a análise das expressões booleana (B), ou seja, este é a tradução direta da regra:<br>
     * 
     * B &Rightarrow; T'B'
     */
    private void booleanExp(){
        this.termBoolean();
        this.termOrBoolean();
    }

    /**
     * Método que realiza a análise dos sutermos booleanos (T') aninhados a uma expressão booleana que possivelmente apresenta conjunção, ou seja, este método é a tradução direta das regras gramaticais:<br> 
     * T' &Rightarrow; F'C | &lambda;
     */
    private void termBoolean(){
        this.booleanFactor();
        this.termAndBoolean();
    }

    /**
     * Método que realiza a análise dos sutermos conjuntivos (B') aninhados a uma expressão booleana, ou seja, este método é a tradução direta das regras gramaticais:<br> 
     * C' &Rightarrow; and F'C' | &lambda;
     */
    private void termAndBoolean(){
        if(this.peek().getType() == Token.Type.AND){
            this.consume(Token.Type.AND);
            this.booleanFactor();
            this.termAndBoolean();
        }
    }

    /**
     * Método que realiza a análise das subtermo booleano (B') com uma possível disjunção no corpo do termo, ou seja, este é a tradução direta da regra:<br>
     * 
     * B' &Rightarrow; or T'B'
     */
    private void termOrBoolean(){
        if(this.peek().getType() == Token.Type.OR){
            this.consume(Token.Type.OR);
            this.termBoolean();
            this.termOrBoolean();
        }
    }

    /** 
     * Método que consome um fator de uma expressão booleana, quando o fator for válido (ou seja, quando o fator for true, false ou uma comparação usando < ou =). Ou seja, implementa as regras:<br>
     * 
     * F' &Rightarrow; !F' | (B) | true R | false R | ID R | NUMBER D<br>
     * 
     * onde NUMBER é um lexema de um número natural e ID é o lexema de um identificador válido.
     * 
     * @throws RuntimeException Lançada quando o fator booleano não é válido!
     * */
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

    /**
     * Método que realiza o consumo dos operadores < e = nos fatores booleanos. Ou seja, implementa as regras:<br>
     * 
     * R  &Rightarrow; < R' | = R'
     */
    private void relations() {
        if (this.peek().getType() == Token.Type.LESS) {
            this.consume(Token.Type.LESS);
            this.relationFactor();
        } else if (this.peek().getType() == Token.Type.EQUAL) {
            this.consume(Token.Type.EQUAL);
            this.relationFactor();
        }
    }

    /**
     * Métrodo que realiza o consumo do segundo fator em uma comparação, ou seja,  implementa as regras:<br>
     * 
     * R' &Rightarrow; ID | NUMBER | true | false<br>
     * 
     * onde NUMBER é um lexema de um número natural e ID é o lexema de um identificador válido.
     */
    private void relationFactor(){
        Token.Type type = this.peek().getType();
        if (type == Token.Type.ID || type == Token.Type.NUMBER || 
            type == Token.Type.TRUE || type == Token.Type.FALSE) {
            this.consume(type);
        } else {
            throw new RuntimeException(
                "Error on line " + this.peek().getLine() +
                ", column " + this.peek().getColumn() +
                ": invalid lexeme " + this.peek().getLexeme()
            );
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
    //
    // Os métodos abaixo eles fazem a análise sintática e retorna uma AST (abstract sintax tree)
    //
    // -------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Método que executa a análise sintática e ao mesmo tempo realiza a construção da árvore sintática abstrata (AST - Abstract sintatic tree).
     * 
     * @return A AST construída
     */
    public Cmd parseAST() {
        Cmd program = this.astCommands();
        if (this.peek().getType() != Token.Type.EOF) {
            Token t = this.peek();
            throw new RuntimeException( "Error at line " + t.getLine() + ", column " + t.getColumn() + ": unexpected token " + t.getLexeme());
        }
        return program;
    }

    /**
     * Método que realiza a análise sintática dos comandos concatenados.
     * 
     * @return A AST construída usando os comandos concatenados
     * @throws RuntimeException Quando os blocos de comandos são vazios
     */
    private Cmd astCommands() {
        ArrayList<Cmd> cmds = new ArrayList<>();
        while (this.peek().getType() == Token.Type.ID || this.peek().getType() == Token.Type.SKIP || this.peek().getType() == Token.Type.IF || this.peek().getType() == Token.Type.WHILE) {
            cmds.add(this.astCommand());
        }
        if (cmds.isEmpty()) {
            throw new RuntimeException("Empty command block not allowed");
        }
        return cmds.size() == 1 ? cmds.get(0) : new Block(cmds);
    }

    /**
     * Método que realiza a análise dos comandos (C) da linguagem WHILE e cria a AST, ou seja, este é a tradução diretas da regras:<br>
     * 
     * C &Rightarrow; id:E;C | skip;C | if (B) { C } else { C } | if (B) { C } else { C } C |  While (B) { cmd } |  While (B) { cmd } C | &lambda;
     */
    private Cmd astCommand() {
        if(this.peek().getType() == Token.Type.ID){
            Token id = this.peek();
            this.consume(Token.Type.ID);
            this.consume(Token.Type.ASSIGN);
            Expr value = this.astArithmeticsExp();
            this.consume(Token.Type.SEMICOLON);
            return new Assign(id, value);
        } else if (this.peek().getType() == Token.Type.SKIP){
            this.consume(Token.Type.SKIP);
            this.consume(Token.Type.SEMICOLON);
            return new Skip();
        } else if (this.peek().getType() == Token.Type.IF) {
            this.consume(Token.Type.IF);
            this.consume(Token.Type.LEFTP);
            Expr condition = this.astBooleanExp();
            this.consume(Token.Type.RIGHTP);
            this.consume(Token.Type.THEN);
            this.consume(Token.Type.LEFTK);
            Cmd thenBranch = this.astCommands();
            this.consume(Token.Type.RIGHTK);
            this.consume(Token.Type.ELSE);
            this.consume(Token.Type.LEFTK);
            Cmd elseBranch = this.astCommands();
            this.consume(Token.Type.RIGHTK);
            return new Conditional(condition, thenBranch, elseBranch);
        } else if (this.peek().getType() == Token.Type.WHILE) {
            this.consume(Token.Type.WHILE);
            this.consume(Token.Type.LEFTP);
            Expr condition = this.astBooleanExp();
            this.consume(Token.Type.RIGHTP);
            this.consume(Token.Type.LEFTK);
            Cmd body = this.astCommands();
            this.consume(Token.Type.RIGHTK);
            return new Loop(condition, body);
        } else {
            throw new RuntimeException("Error at line " + peek().getLine() + ", column " + peek().getColumn() + ": invalid command starting with " + peek().getLexeme());
        }
    }

    /**
     * Método que analisa uma expressão aritmética e retorna uma Expressão em formato apropriado para a AST
     * 
     * @return A expressão limpa (sem símbolos sintático irrelevantes) para necessária para construir a AST
     */
    private Expr astArithmeticsExp() {
        Expr expr = this.astTerm();
        return this.astExpLine(expr);
    }

    /**
     * Método que analisa um termo em uma expressão aritmética e retorna uma Expressão em formato apropriado para a AST
     * 
     * @return A expressão limpa (sem símbolos sintático irrelevantes) para necessária para construir a AST
     */
    private Expr astTerm() {
        Expr term = this.astFactor();
        return this.astTermLine(term);
    }

    /**
     * Método que realiza a construção de uma expressão aditiva a partir de uma expressão à esquerda
     * 
     * @param left A expressão esquerda necessárias
     * @return A expressão limpa (sem símbolos sintático irrelevantes) para necessária para construir a AST
     */
    private Expr astExpLine(Expr left) {
        if (this.peek().getType() == Token.Type.PLUS ||  this.peek().getType() == Token.Type.MINUS) {
            Token op = this.peek();
            this.consume(op.getType());
            Expr right = this.astTerm();
            Expr newLeft = new Binary(left, op, right);
            return this.astExpLine(newLeft);
        }
        return left;
    }
    
    /**
     * Método que realiza a construção de uma expressão multiplicativa a partir de uma expressão à esquerda
     * 
     * @param left A expressão esquerda necessárias
     * @return A expressão limpa (sem símbolos sintático irrelevantes) para necessária para construir a AST
     */
    private Expr astTermLine(Expr left) {
        if (this.peek().getType() == Token.Type.PRODUCT) {
            Token op = this.peek();
            this.consume(Token.Type.PRODUCT);
            Expr right = this.astFactor();
            Expr newLeft = new Binary(left, op, right);
            return this.astTermLine(newLeft);
        }
        return left;
    }

    /**
     * Método que realiza a construção de um fator aritmético fundamental.
     * 
     * @return O fator limpo (apenas o fator puro, sem parênteses)
     * @throws RuntimeException quando o token é um fator inválido
     */
    private Expr astFactor() {
        Token t = this.peek();
        if (t.getType() == Token.Type.ID) {
            this.consume(Token.Type.ID);
            return new Variable(t);
        } else if (t.getType() == Token.Type.NUMBER) {
            this.consume(Token.Type.NUMBER);
            return new Literal(t);
        } else if (t.getType() == Token.Type.LEFTP) {
            this.consume(Token.Type.LEFTP);
            Expr expr = this.astArithmeticsExp();
            this.consume(Token.Type.RIGHTP);
            return expr;
        }
        throw new RuntimeException("Invalid factor: " + t);
    }

    /**
     * Método que analisa uma expressão booleana e retorna uma Expressão em formato apropriado, ou seja, formado limpo, para a AST
     * 
     * @return A expressão limpa (sem símbolos sintático irrelevantes) para necessária para construir a AST
     */
    private Expr astBooleanExp() {
        Expr left = this.astTermBoolean();
        return this.astTermOrBoolean(left);
    }

    /**
     * Método que analisa um termo disjuntivo em uma expressão booleana e retorna uma Expressão em formato apropriado para a AST
     * 
     * @param left A expressão base
     * @return A expressão limpa para construir a AST
     */
    private Expr astTermOrBoolean(Expr left) {
        if (this.peek().getType() == Token.Type.OR) {
            Token op = this.peek();
            this.consume(Token.Type.OR);
            Expr right = this.astTermBoolean();
            Expr newLeft = new Binary(left, op, right);
            return this.astTermOrBoolean(newLeft);
        }
        return left;
    }

    /**
     * Método que analisa um termo em uma expressão booleana e retorna uma Expressão booleana conjuntiva em formato apropriado para a AST
     * 
     * @return A expressão limpa para construir a AST
     */
    private Expr astTermBoolean() {
        Expr left = this.astBooleanFactor();
        return this.astTermAndBoolean(left);
    }

    /**
     * Método que analisa um termo conjuntivo em uma expressão booleana e retorna uma Expressão em formato apropriado para a AST
     * 
     * @param left A expressão base
     * @return A expressão limpa para construir a AST
     */
    private Expr astTermAndBoolean(Expr left) {
        if (this.peek().getType() == Token.Type.AND) {
            Token op = this.peek();
            this.consume(Token.Type.AND);
            Expr right = this.astBooleanFactor();
            Expr newLeft = new Binary(left, op, right);
            return this.astTermAndBoolean(newLeft);
        }
        return left;
    }

    /**
     * Método que realiza a construção de um fator booleano fundamental.
     * 
     * @return O fator limpo (apenas o fator puro, sem parênteses)
     * @throws RuntimeException quando o token é um fator inválido
     */
    private Expr astBooleanFactor() {
        if (this.peek().getType() == Token.Type.NOT) {
            Token op = this.peek();
            this.consume(Token.Type.NOT);
            Expr expr = this.astBooleanFactor();
            return new Unary(op, expr);
        } else if (this.peek().getType() == Token.Type.LEFTP) {
            this.consume(Token.Type.LEFTP);
            Expr expr = this.astBooleanExp();
            this.consume(Token.Type.RIGHTP);
            return expr;
        } else {
            Token token = this.peek();
            if (token.getType() == Token.Type.TRUE || token.getType() == Token.Type.FALSE) {
                this.consume(token.getType());
                return new Literal(token);
            }
            if (token.getType() == Token.Type.ID || token.getType() == Token.Type.NUMBER) {
                this.consume(token.getType());
                Expr left = (token.getType() == Token.Type.ID) ? new Variable(token) : new Literal(token);
                if (this.peek().getType() == Token.Type.LESS || this.peek().getType() == Token.Type.EQUAL) {
                    Token op = this.peek();
                    this.consume(op.getType());
                    Token rightToken = this.peek();
                    if (rightToken.getType() == Token.Type.ID || rightToken.getType() == Token.Type.NUMBER || rightToken.getType() == Token.Type.TRUE || rightToken.getType() == Token.Type.FALSE) { 
                        this.consume(rightToken.getType());
                        Expr right = (rightToken.getType() == Token.Type.ID) ? new Variable(rightToken) : new Literal(rightToken);
                        return new Binary(left, op, right);
                    } else {
                        throw new RuntimeException(
                            "Error at line " + rightToken.getLine() +
                            ", column " + rightToken.getColumn() +
                            ": invalid relation operand " + rightToken.getLexeme()
                        );
                    }
                } else {
                    throw new RuntimeException( "Error at line " + token.getLine() + ", column " + token.getColumn() + ": ID/NUMBER cannot appear without a relation in boolean expression");
                }
            }
            throw new RuntimeException("Unexpected token in boolean expression: " + token.getType());
        }
    }

}
