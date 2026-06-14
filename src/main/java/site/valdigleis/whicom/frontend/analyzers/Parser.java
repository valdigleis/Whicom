package site.valdigleis.whicom.frontend.analyzers;

import java.util.List;
import java.util.Map;

import site.valdigleis.whicom.frontend.utils.Symbol;
import site.valdigleis.whicom.frontend.utils.Token;
import site.valdigleis.whicom.frontend.utils.TokenType;

public class Parser {
    
    private final List<Token> tokens;
    private final Map<String, Symbol> symbolTable;
    private int lookahead = 0;

    public Parser(List<Token> tokens, Map<String, Symbol> symbolTable) {
        this.tokens = tokens;
        this.symbolTable = symbolTable;
    }

    /**
     * Método que retorna o token apontado pelo <em>lookahead</em> na lista de tokens.
     * 
     * @return
     */
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
    private void consume(TokenType type) {
        if (this.peek().type() == type) {
          this.lookahead++;
        } else {
          throw new RuntimeException(
            "Error at line " + this.peek().line() +
            ", column " + this.peek().column() +
            ": expected " + type +
            " but found " + this.peek().type()
          );
        }
    }

    // ----------------------------------------------------------------------------
    // Abaixo estão os métodos que efetuam o parse, porém, não constroem a AST
    // ----------------------------------------------------------------------------
    

    /**
     * Método que realiza a análise sintática.
     * 
     * @throws RuntimeException no caso de ainda existirem arquivos após o token de EOF
     */
    public void parse() {
        this.S();
        if (this.peek().type() != TokenType.EOF) {
            throw new RuntimeException("Error: Extra Tokens after WHILE program tree " + this.peek().line());
        }
    }

    /**
     * Método que implementa a produção do símbolo S, ou seja, implementa a produção S &rightarrow; CS'.
     */
    public void S() {
        this.C();
        this.S_line();
    }

    /**
     * Método que implementa as produções do símbolo S', ou seja, implementa a produção S' &rightarrow; CS' &mid; &lambda;.
     */
    private void S_line() {
        // S' -> C S' | lambda
        if (this.peek().type() == TokenType.ID || this.peek().type() == TokenType.IF || this.peek().type() == TokenType.WHILE || this.peek().type() == TokenType.SKIP) {
            this.C();
            S_line();
        }
    }

    /**
     * Método que implementa as produções do símbolo C, ou seja, implementa as produções:<br> C &rightarrow; I := K; &mid; if( B ) then { S } else { S } &mid; while ( B ) { S } &mid; skip;.
     */
    private void C() {
        if (this.peek().type() == TokenType.ID) {
            String tableRef = this.peek().lexeme();
            this.consume(TokenType.ID);
            this.consume(TokenType.ASSIGN);
            this.K(tableRef);
            this.consume(TokenType.SEMI);
        } else if (this.peek().type() == TokenType.IF) {
            this.consume(TokenType.IF);
            this.consume(TokenType.LPAREN);
            this.B();
            this.consume(TokenType.RPAREN);
            this.consume(TokenType.THEN);
            this.consume(TokenType.LBRACE);
            this.S();
            this.consume(TokenType.RBRACE);
            this.consume(TokenType.ELSE);
            this.consume(TokenType.LBRACE);
            this.S();
            this.consume(TokenType.RBRACE);
        } else if (this.peek().type() == TokenType.WHILE) {
            this.consume(TokenType.WHILE);
            this.consume(TokenType.LPAREN);
            this.B();
            this.consume(TokenType.RPAREN);
            this.consume(TokenType.DO);
            this.consume(TokenType.LBRACE);
            this.S();
            this.consume(TokenType.RBRACE);
        } else if (this.peek().type() == TokenType.SKIP) {
            this.consume(TokenType.SKIP);
            this.consume(TokenType.SEMI);
        } else {
            throw new RuntimeException("Error: Invalid command in line: " + this.peek().line() + " column: " + this.peek().column());
            
        }
    }

    private void K(String tableRef) {
        if(this.peek().type() == TokenType.TRUE) {
            this.consume(TokenType.TRUE);
            this.symbolTable.get(tableRef).setType("bool");
        } else if (this.peek().type() == TokenType.FALSE) {
            this.consume(TokenType.FALSE);
            this.symbolTable.get(tableRef).setType("bool");
        } else if (this.peek().type() == TokenType.NUM || this.peek().type() == TokenType.ID || this.peek().type() == TokenType.LPAREN) {
            this.symbolTable.get(tableRef).setType("int");
            this.E();
        } else {
            throw new RuntimeException("Error: Invalid token in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    private void E() { 
        this.T(); 
        this.E_line(); 
    }

    private void E_line() {
        if (this.peek().type() == TokenType.PLUS) {
            this.consume(TokenType.PLUS);
            this.T(); 
            this.E_line(); 
        } else if (this.peek().type() == TokenType.MINUS) {
            this.consume(TokenType.MINUS);
            this.T(); 
            this.E_line(); 
        } 
    }

    private void T() {
        this.F();
        this.T_line();
    }

    private void T_line() {
        if (this.peek().type() == TokenType.TIMES) {
            this.consume(TokenType.TIMES);
            this.F(); 
            this.T_line(); 
        }
    }

    private void F() {
        if (this.peek().type() == TokenType.LPAREN) {
            this.consume(TokenType.LPAREN);
            this.E();
            this.consume(TokenType.RPAREN);
        } else if (this.peek().type() == TokenType.ID) {
            this.I();
        } else if (this.peek().type() == TokenType.NUM) {
            this.N();
        }
    }

    private void B() {
        this.A();
        this.O();
    }

    private void A() {
        this.B_n();
        this.A_line();
    }

    private void A_line() {
        if (this.peek().type() == TokenType.AND) {
            this.consume(TokenType.AND);
            this.B_n();
            this.A_line();
        }
    }

    private void O() {
        if (this.peek().type() == TokenType.OR) {
            this.consume(TokenType.OR);
            this.A();
            this.O();
        }
    }

    private void B_n() {
        if (this.peek().type() == TokenType.NOT) {
            this.consume(TokenType.NOT);
            this.B_n();
        } else {
            this.V();
        }
    }

    private void V() {
        if (this.peek().type() == TokenType.TRUE) {
            this.consume(TokenType.TRUE);
        } else if (this.peek().type() == TokenType.FALSE) {
            this.consume(TokenType.FALSE);
        } else if (this.peek().type() == TokenType.LPAREN) {
            this.consume(TokenType.LPAREN);
            this.B();
            this.consume(TokenType.RPAREN);
        } else {
            this.E();
            this.R();
            this.E();
        }
    }

    private void R() {
        if (this.peek().type() == TokenType.EQ) {
            this.consume(TokenType.EQ);
        } else if (this.peek().type() == TokenType.LT) {
            this.consume(TokenType.LT);
        } else {
            throw new RuntimeException("Error: Invalid token in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    private void I() {
        if (this.peek().type() == TokenType.ID) {
            this.consume(TokenType.ID);
        } else {
            throw new RuntimeException("Error: Invalid Token ID in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    private void N() {
        if (this.peek().type() == TokenType.NUM) {
            this.consume(TokenType.NUM);
        } else {
            throw new RuntimeException("Error: Invalid number in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }


    
}
