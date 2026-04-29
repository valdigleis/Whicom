package site.valdigleis.whicom.analyzers;

import java.util.ArrayList;

import org.junit.Test;

import site.valdigleis.whicom.ast.Cmd;
import site.valdigleis.whicom.ast.ASTPrinter;

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
    public void testSimpleASTGenerate() {
        ArrayList<Token> tokens = new Lexer("x: 0; y : 49; z: x; while (x < y) { z: z + 1; x: var * z;} if (!(true) or (!(false) and x = y)) then {x : 17; y: x-id;} else {x : z - 1;}").tokenize();
        Parser parser = new Parser(tokens);
        Cmd program = parser.parseAST();
        ASTPrinter printer = new ASTPrinter();
        System.out.println(printer.print(program));
    }

}
