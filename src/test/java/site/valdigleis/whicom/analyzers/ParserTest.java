package site.valdigleis.whicom.analyzers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import site.valdigleis.whicom.ast.Cmd;
import site.valdigleis.whicom.ast.Conditional;
import site.valdigleis.whicom.ast.Expr;
import site.valdigleis.whicom.ast.Loop;
import site.valdigleis.whicom.ast.Unary;
import site.valdigleis.whicom.ast.ASTPrinter;
import site.valdigleis.whicom.ast.Assign;
import site.valdigleis.whicom.ast.Binary;
import site.valdigleis.whicom.ast.Block;

public class ParserTest {
    
    @Test
    public void testSimpleAssignment() {
        ArrayList<Token> tokens = new Lexer("x : 10;").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testSimpleSkip() {
        ArrayList<Token> tokens = new Lexer("skip;").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testSequenceAssignments() {
        ArrayList<Token> tokens = new Lexer("x : 10;y:10; z: 25;").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testSequenceSkips() {
        ArrayList<Token> tokens = new Lexer("skip; skip; skip ;").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testSimpleSequence() {
        ArrayList<Token> tokens = new Lexer("skip; x : 12; skip ; z : 5 + 7;").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testSimpleIf() {
        ArrayList<Token> tokens = new Lexer("if ((true = false) and !10 = 1) then { skip; } else { y: 10; }").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testSimpleWhile() {
        ArrayList<Token> tokens = new Lexer("while (true = false and !10 = z) { skip; }").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testOneProgram() {
        ArrayList<Token> tokens = new Lexer("x: 0; y : 49; z: x; while (x < y) { z: z + 1; x: z * z;} if (!(true)) then {skip;} else {skip;}").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testTwoProgram() {
        ArrayList<Token> tokens = new Lexer("x: 0; y : 49; z: x; while (x < y) { z: z + 1; x: z * z;} if (!(true) and (!(false) or x = y)) then {skip;} else {skip;}").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void testThreeProgram() {
        ArrayList<Token> tokens = new Lexer("x: 0; y : 49; z: x; while (x < y) { z: z + 1; x: var * z;} if (!(true) or (!(false) and x = y)) then {skip;} else {skip;}").tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }

    @Test
    public void shouldParseAssignmentAST() {
        ArrayList<Token> tokens = new Lexer("x : 10;").tokenize();
        Parser parser = new Parser(tokens);
        Cmd cmd = parser.parseAST();
        assertTrue(cmd instanceof Assign);
        Assign assign = (Assign) cmd;
        assertEquals("x", assign.getVariable().getLexeme());
    }

    @Test
    public void shouldParseBlockAST() {
        ArrayList<Token> tokens = new Lexer("x : 10; y : 1;").tokenize();
        Parser parser = new Parser(tokens);
        Cmd cmd = parser.parseAST();
        assertTrue(cmd instanceof Block);
        Block block = (Block) cmd;
        assertEquals(2, block.getCommands().size());
    }

    @Test
    public void shouldParseConditionalAST() {
        ArrayList<Token> tokens = new Lexer("if(false)then{skip;}else{skip;}").tokenize();
        Parser parser = new Parser(tokens);
        Cmd cmd = parser.parseAST();
        assertTrue(cmd instanceof Conditional);
    }

    @Test
    public void  shouldParseLoopAST() {
        ArrayList<Token> tokens = new Lexer("while(!(true)){skip;}").tokenize();
        Parser parser = new Parser(tokens);
        Cmd cmd = parser.parseAST();
        assertTrue(cmd instanceof Loop);
    }

    @Test
    public void shouldRespectPrecedenceAST() {
        ArrayList<Token> tokens = new Lexer("x : 1 + 2 * 3;").tokenize();
        Parser parser = new Parser(tokens);
        Assign assign = (Assign) parser.parseAST();
        Expr expr = assign.getValue();
        assertTrue(expr instanceof Binary);
        Binary root = (Binary) expr;
        assertEquals("+", root.getOperator().getLexeme());
        assertTrue(root.getRight() instanceof Binary);
        Binary child = (Binary) root.getRight();
        assertEquals("*", child.getOperator().getLexeme());
    }

     @Test
    public void shouldRespectPrecedenceBooleanAST() {
        ArrayList<Token> tokens = new Lexer("if (!true and false or true)then{ skip; } else { skip; }").tokenize();
        Parser parser = new Parser(tokens);
        Cmd cmd = parser.parseAST();
        assertTrue(cmd instanceof Conditional);
        Conditional cond = (Conditional) cmd;
        Expr condition = cond.getCondition();
        assertTrue(condition instanceof Binary);
        Binary orExpr = (Binary) condition;
        assertEquals("or", orExpr.getOperator().getLexeme());
        assertTrue(orExpr.getLeft() instanceof Binary);
        Binary andExpr = (Binary) orExpr.getLeft();
        assertEquals("and", andExpr.getOperator().getLexeme());
        assertTrue(andExpr.getLeft() instanceof Unary);
        Unary notExpr = (Unary) andExpr.getLeft();
        assertEquals("!", notExpr.getOperator().getLexeme());
    }

    @Test
    public void printProgram() {
        ArrayList<Token> tokens = new Lexer("x: 0; /* Um comentário aqui\n // olá aqui */y : 49; z: x; while (x < y) { z: z + 1; x: var * z;} if (!(true) or (!(false) and x = y)) then {skip;} else {skip;}").tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        Parser parser = new Parser(tokens);
        ASTPrinter printer = new ASTPrinter();
        System.out.println(printer.print(parser.parseAST()));
    }

}
