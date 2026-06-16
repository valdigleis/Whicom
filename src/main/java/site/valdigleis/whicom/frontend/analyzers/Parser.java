package site.valdigleis.whicom.frontend.analyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import site.valdigleis.whicom.frontend.utils.Symbol;
import site.valdigleis.whicom.frontend.utils.SymbolKind;
import site.valdigleis.whicom.frontend.utils.SymbolTable;
import site.valdigleis.whicom.frontend.utils.Token;
import site.valdigleis.whicom.frontend.utils.TokenType;
import site.valdigleis.whicom.frontend.utils.AST.Program;
import site.valdigleis.whicom.frontend.utils.AST.expression.BinaryExpr;
import site.valdigleis.whicom.frontend.utils.AST.expression.BoolLiteral;
import site.valdigleis.whicom.frontend.utils.AST.expression.Expression;
import site.valdigleis.whicom.frontend.utils.AST.expression.IntLiteral;
import site.valdigleis.whicom.frontend.utils.AST.expression.RelationalExpr;
import site.valdigleis.whicom.frontend.utils.AST.expression.UnaryExpr;
import site.valdigleis.whicom.frontend.utils.AST.expression.VariableExpr;
import site.valdigleis.whicom.frontend.utils.AST.operator.Operators;
import site.valdigleis.whicom.frontend.utils.AST.statement.AssignStmt;
import site.valdigleis.whicom.frontend.utils.AST.statement.BlockStmt;
import site.valdigleis.whicom.frontend.utils.AST.statement.IfStmt;
import site.valdigleis.whicom.frontend.utils.AST.statement.SkipStmt;
import site.valdigleis.whicom.frontend.utils.AST.statement.Statement;
import site.valdigleis.whicom.frontend.utils.AST.statement.WhileStmt;

public class Parser {
    
    private final List<Token> tokens;
    private final SymbolTable symbolTable;
    private int lookahead = 0;

    public Parser(List<Token> tokens, Map<String, Symbol> symbolTable) {
        this.tokens = tokens;
        this.symbolTable = new SymbolTable();
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

    /**
     * Método que implementa as produções do símbolo K, ou seja, implementa as produções:<br> K &rightarrow; true &mid; false &mid; E .
     */
    private void K(String tableRef) {
        if(this.peek().type() == TokenType.TRUE) {
            this.consume(TokenType.TRUE);
        } else if (this.peek().type() == TokenType.FALSE) {
            this.consume(TokenType.FALSE);
        } else if (this.peek().type() == TokenType.NUM || this.peek().type() == TokenType.ID || this.peek().type() == TokenType.LPAREN) {
            this.E();
        } else {
            throw new RuntimeException("Error: Invalid token in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    /**
     * Método que implementa a produção do símbolo E, ou seja, implementa a produção:<br> E &rightarrow; T E'.
     */
    private void E() { 
        this.T(); 
        this.E_line(); 
    }

    /**
     * Método que implementa as produções do símbolo E', ou seja, implementa a produções:<br> E' &rightarrow; + T E' &mid; - T E' &mid; &lambda;.
     */
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

    /**
     * Método que implementa a produção do símbolo T, ou seja, implementa a produção:<br> T &rightarrow; F T'.
     */
    private void T() {
        this.F();
        this.T_line();
    }

    /**
     * Método que implementa as produções do símbolo T', ou seja, implementa a produções:<br> T' &rightarrow; * F T' &mid; &lambda;.
     */
    private void T_line() {
        if (this.peek().type() == TokenType.TIMES) {
            this.consume(TokenType.TIMES);
            this.F(); 
            this.T_line(); 
        }
    }

    /**
     * Método que implementa as produções do símbolo F, ou seja, implementa a produções:<br> F &rightarrow; ( E ) &mid; id &mid; num.
     */
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

    /**
     * Método que implementa as produções do símbolo B, ou seja, implementa a produções:<br> B &rightarrow; A O.
     */
    private void B() {
        this.A();
        this.O();
    }

    /**
     * Método que implementa as produções do símbolo A, ou seja, implementa a produções:<br> A &rightarrow; B' A'.
     */
    private void A() {
        this.B_n();
        this.A_line();
    }

    /**
     * Método que implementa as produções do símbolo A', ou seja, implementa a produções:<br> A' &rightarrow; and B' A' &mid; &lambda;.
     */
    private void A_line() {
        if (this.peek().type() == TokenType.AND) {
            this.consume(TokenType.AND);
            this.B_n();
            this.A_line();
        }
    }

    /**
     * Método que implementa as produções do símbolo O, ou seja, implementa a produções:<br> O &rightarrow; or A O &mid; &lambda;.
     */
    private void O() {
        if (this.peek().type() == TokenType.OR) {
            this.consume(TokenType.OR);
            this.A();
            this.O();
        }
    }

    /**
     * Método que implementa as produções do símbolo B', ou seja, implementa a produções:<br> B' &rightarrow; not B' &mid; V.
     */
    private void B_n() {
        if (this.peek().type() == TokenType.NOT) {
            this.consume(TokenType.NOT);
            this.B_n();
        } else {
            this.V();
        }
    }

    /**
     * Método que implementa as produções do símbolo V, ou seja, implementa a produções:<br> V &rightarrow; E R E &mid; true  &mid; false  &mid; ( B ).
     */
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

    /**
     * Método que implementa as produções do símbolo R, ou seja, implementa a produções:<br> R &rightarrow; = &mid; < .
     */
    private void R() {
        if (this.peek().type() == TokenType.EQ) {
            this.consume(TokenType.EQ);
        } else if (this.peek().type() == TokenType.LT) {
            this.consume(TokenType.LT);
        } else {
            throw new RuntimeException("Error: Invalid token in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    /**
     * Método que consome os ID's.
     */
    private void I() {
        if (this.peek().type() == TokenType.ID) {
            this.consume(TokenType.ID);
        } else {
            throw new RuntimeException("Error: Invalid Token ID in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    /**
     * Método que consome os números.
     */
    private void N() {
        if (this.peek().type() == TokenType.NUM) {
            this.consume(TokenType.NUM);
        } else {
            throw new RuntimeException("Error: Invalid number in  Line: " + this.peek().line() + " Column:" + this.peek().column());
        }
    }

    // ----------------------------------------------------------------------------
    // Abaixo estão os métodos que efetuam o parse e constroem a AST
    // ----------------------------------------------------------------------------
    
    public Program parse_to_AST() {
        ArrayList<Statement> statements = this.S_to_AST();
        if (this.peek().type() != TokenType.EOF) {
            throw new RuntimeException("Error: Extra Tokens after WHILE program tree " + this.peek().line());

        }
        return new Program(statements);
    }

    /**
     * Implementa S &rightarrow; C S' e construindo a AST.
     */
    private ArrayList<Statement> S_to_AST() {
        ArrayList<Statement> statements = new ArrayList<>();
        statements.add(this.C_to_AST());
        this.S_line_to_AST(statements);
        return statements;
    }

    /**
     * Implementa
     *
     * S' &rightarrow; C S' &mid; λ
     *
     * acrescentando comandos à lista.
     */
    private void S_line_to_AST(ArrayList<Statement> statements) {
        if (this.peek().type() == TokenType.ID || 
            this.peek().type() == TokenType.IF || 
            this.peek().type() == TokenType.WHILE || 
            this.peek().type() == TokenType.SKIP) {
            statements.add(this.C_to_AST());
            this.S_line_to_AST(statements);
        }

    }

    /**
     * Implementa<br> 
     * C &rightarrow;  id := K; &mid; if (B) then {S} else {S} &mid; while (B) do {S} &mid; skip; <br>
     * construindo a AST.
     */
    private Statement C_to_AST() {
        if (this.peek().type() == TokenType.ID) {
            String variable = this.peek().lexeme();
            this.consume(TokenType.ID);
            this.register(variable);
            this.consume(TokenType.ASSIGN);
            Expression expression = this.K_to_AST();
            this.consume(TokenType.SEMI);
            return new AssignStmt(variable,expression);
        } else if (this.peek().type() == TokenType.IF) {
            this.consume(TokenType.IF);
            this.consume(TokenType.LPAREN);
            Expression condition = this.B_to_AST();
            this.consume(TokenType.RPAREN);
            this.consume(TokenType.THEN);
            this.consume(TokenType.LBRACE);
            BlockStmt thenBranch = new BlockStmt(this.S_to_AST());
            this.consume(TokenType.RBRACE);
            this.consume(TokenType.ELSE);
            this.consume(TokenType.LBRACE);
            BlockStmt elseBranch = new BlockStmt(this.S_to_AST());
            this.consume(TokenType.RBRACE);
            return new IfStmt(condition,thenBranch,elseBranch);
        } else if (this.peek().type() == TokenType.WHILE) {
            this.consume(TokenType.WHILE);
            this.consume(TokenType.LPAREN);
            Expression condition = this.B_to_AST();
            this.consume(TokenType.RPAREN);
            this.consume(TokenType.DO);
            this.consume(TokenType.LBRACE);
            BlockStmt body = new BlockStmt(this.S_to_AST());
            this.consume(TokenType.RBRACE);
            return new WhileStmt(condition,body);
        } else if (this.peek().type() == TokenType.SKIP) {
            this.consume(TokenType.SKIP);
            this.consume(TokenType.SEMI);
            return new SkipStmt();
        } else {
            throw new RuntimeException("Error: Invalid command in line: " + this.peek().line() + " column: " + this.peek().column());
        }
    }

    /**
     * Implementa  K &rightarrow; true &mid; false &mid; E <br>
     * construindo a AST.
     */
    private Expression K_to_AST() {
        if (this.peek().type() == TokenType.TRUE) {
            this.consume(TokenType.TRUE);
            return new BoolLiteral(true);
        } else if (this.peek().type() == TokenType.FALSE) {
            this.consume(TokenType.FALSE);
            return new BoolLiteral(false);
        } else if (this.peek().type() == TokenType.NUM || this.peek().type() == TokenType.ID || this.peek().type() == TokenType.LPAREN) {
            return this.E_to_AST();
        }
        throw new RuntimeException("Error: Invalid token in Line " + this.peek().line() + " Column " + this.peek().column());
    }

    /**
     * Implementa
     *
     * E &rightarrow; T E'<br>
     * construindo a AST.
     */
    private Expression E_to_AST() {
        Expression left = this.T_to_AST();
        return this.E_line_to_AST(left);
    }

    /**
     * Implementa
     * E' &rightarrow; + T E' &mid; - T E' &mid; λ<br>
     * construindo a AST.
     */
    private Expression E_line_to_AST(Expression left) {
        if (this.peek().type() == TokenType.PLUS) {
            this.consume(TokenType.PLUS);
            Expression right = this.T_to_AST();
            BinaryExpr expr = new BinaryExpr(left,Operators.Binary.PLUS,right);
            return this.E_line_to_AST(expr);
        } else if (this.peek().type() == TokenType.MINUS) {
            this.consume(TokenType.MINUS);
            Expression right = this.T_to_AST();
            BinaryExpr expr = new BinaryExpr(left,Operators.Binary.MINUS,right);
            return this.E_line_to_AST(expr);
        }
        return left;
    }

    /**
     * Implementa
     * T &rightarrow; F T'<br>
     * construindo a AST.
     */
    private Expression T_to_AST() {
        Expression left = this.F_to_AST();
        return this.T_line_to_AST(left);
    }

    /**
     * Implementa
     *
     * T' &rightarrow; * F T' &mid; λ<br>
     * construindo a AST.
     */
    private Expression T_line_to_AST(Expression left) {
        if (this.peek().type() == TokenType.TIMES) {
            this.consume(TokenType.TIMES);
            Expression right = this.F_to_AST();
            BinaryExpr expr = new BinaryExpr(left,Operators.Binary.TIMES,right);
            return this.T_line_to_AST(expr);
        }
        return left;
    }

    /**
     * Implementa
     *
     * F &rightarrow; (E) &mid; id &mid; num<br>
     * construindo a AST.
     */
    private Expression F_to_AST() {
        if (this.peek().type() == TokenType.LPAREN) {
            this.consume(TokenType.LPAREN);
            Expression expr = this.E_to_AST();
            this.consume(TokenType.RPAREN);
            return expr;
        } else if (this.peek().type() == TokenType.ID) {
            return this.I_to_AST();
        } else if (this.peek().type() == TokenType.NUM) {
            return this.N_to_AST();
        }
        throw new RuntimeException("Error: Invalid expression in Line " + this.peek().line() + " Column " + this.peek().column());
    }

    /**
     * Consome um identificador e devolve o nó correspondente da AST.
     */
    private VariableExpr I_to_AST() {
        if (this.peek().type() == TokenType.ID) {
            String name = this.peek().lexeme();
            this.consume(TokenType.ID);
            return new VariableExpr(name);
        }
        throw new RuntimeException("Error: Invalid identifier in Line " + this.peek().line() + " Column " + this.peek().column());
    }

    /**
     * Consome um número e devolve o nó correspondente da AST.
     */
    private IntLiteral N_to_AST() {
        if (this.peek().type() == TokenType.NUM) {
            int value = Integer.parseInt(this.peek().lexeme());
            this.consume(TokenType.NUM);
            return new IntLiteral(value);
        }
        throw new RuntimeException("Error: Invalid number in Line " + this.peek().line() + " Column " + this.peek().column());
    }

    /**
     * Implementa
     * B &rightarrow; A O<br>
     * construindo a AST.
     */
    private Expression B_to_AST() {
        Expression left = this.A_to_AST();
        return this.O_to_AST(left);
    }

    /**
     * Implementa
     * A &rightarrow; B' A'<br>
     * construindo a AST.
     */
    private Expression A_to_AST() {
        Expression left = this.B_n_to_AST();
        return this.A_line_to_AST(left);
    }

    /**
     * Implementa
     * A' &rightarrow; and B' A'  &mid; λ <br>
     * construindo a AST.
     */
    private Expression A_line_to_AST(Expression left) {
        if (this.peek().type() == TokenType.AND) {
            this.consume(TokenType.AND);
            Expression right = this.B_n_to_AST();
            BinaryExpr expr = new BinaryExpr(left, Operators.Binary.AND, right);
            return this.A_line_to_AST(expr);
        }
        return left;
    }

    /**
     * Implementa
     *
     * O &rightarrow; or A O &mid; λ<br>
     * construindo a AST.
     */
    private Expression O_to_AST(Expression left) {
        if (this.peek().type() == TokenType.OR) {
            this.consume(TokenType.OR);
            Expression right = this.A_to_AST();
            BinaryExpr expr = new BinaryExpr(left, Operators.Binary.OR, right);
            return this.O_to_AST(expr);
        }
        return left;
    }

    /**
     * Implementa B' &rightarrow; not B'  &mid; V<br>
     * construindo a AST.
     */
    private Expression B_n_to_AST() {
        if (this.peek().type() == TokenType.NOT) {
            this.consume(TokenType.NOT);
            Expression operand = this.B_n_to_AST();
            return new UnaryExpr(Operators.Unary.NOT, operand);
        }
        return this.V_to_AST();
    }

    /**
     * Implementa V &rightarrow; E R E &mid; true &mid; false &mid; ( B ) <br>
     * construindo a AST.
     */
    private Expression V_to_AST() {
        if (this.peek().type() == TokenType.TRUE) {
            this.consume(TokenType.TRUE);
            return new BoolLiteral(true);
        } else if (this.peek().type() == TokenType.FALSE) {
            this.consume(TokenType.FALSE);
            return new BoolLiteral(false);
        } else if (this.peek().type() == TokenType.LPAREN) {
            this.consume(TokenType.LPAREN);
            Expression expression = this.B_to_AST();
            this.consume(TokenType.RPAREN);
            return expression;
        } else {
            Expression left = this.E_to_AST();
            Operators.Relational operator = this.R_to_AST();
            Expression right = this.E_to_AST();
            return new RelationalExpr(left, operator, right);
        }
    }

    /**
     * Implementa
     *
     * R &rightarrow; = &mid; < <br>
     * construindo a AST.
     */
    private Operators.Relational R_to_AST() {
        if (this.peek().type() == TokenType.EQ) {
            this.consume(TokenType.EQ);
            return Operators.Relational.EQUAL;
        } else if (this.peek().type() == TokenType.LT) {
            this.consume(TokenType.LT);
            return Operators.Relational.LESS_THAN;
        }
        throw new RuntimeException("Error: Invalid relational operator in Line "  + this.peek().line() + " Column " + this.peek().column());
    }


    /**
     * Registra uma variável na tabela de símbolos.
     *
     * Caso ela já exista, nada é feito.
     */
    private void register(String name) {
        if (!this.symbolTable.contains(name)) {
            Symbol symbol = new Symbol(name, SymbolKind.VARIABLE,null, null);
            this.symbolTable.insert(symbol);

        }

    }
    
}
