package site.valdigleis.whicom.frontend.analyzers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import site.valdigleis.whicom.frontend.utils.Token;
import site.valdigleis.whicom.frontend.utils.TokenType;

public class LexerTest {
    
    @Test
    public void testSimpleAssign() {
        String input = "x := 10;";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        // Esperamos: ID, ASSIGN, NUM, SEMI, EOF
        assertEquals(5, tokens.size());
        assertEquals(TokenType.ID, tokens.get(0).type());
        assertEquals(TokenType.ASSIGN, tokens.get(1).type());
        assertEquals(TokenType.NUM, tokens.get(2).type());
        assertEquals(TokenType.SEMI, tokens.get(3).type());
        assertEquals(TokenType.EOF, tokens.get(4).type());
    }

    @Test
    public void testTwoAssign() {
        String input = "WHILE:=10;\ny:=0-25;";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        // Esperamos: ID, ASSIGN, NUM, SEMI, EOF
        assertEquals(11, tokens.size()); 
        assertEquals(TokenType.ID, tokens.get(0).type());
        assertEquals(TokenType.ASSIGN, tokens.get(1).type());
        assertEquals(TokenType.NUM, tokens.get(2).type());
        assertEquals(TokenType.SEMI, tokens.get(3).type());
        assertEquals(TokenType.ID, tokens.get(4).type());
        assertEquals(TokenType.ASSIGN, tokens.get(5).type());
        assertEquals(TokenType.NUM, tokens.get(6).type());
        assertEquals(TokenType.MINUS, tokens.get(7).type());
        assertEquals(TokenType.NUM, tokens.get(8).type());
        assertEquals(TokenType.SEMI, tokens.get(9).type());
        assertEquals(TokenType.EOF, tokens.get(10).type());
    }

    @Test
    public void ignoreSimplesComemnts() {
        String input = "x := 101; // comentário simples\n/* comentário\n\nde múltiplas linhas */y := 2;";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        // Total esperado: 9 tokens (ID, ASSIGN, NUM, SEMI, ID, ASSIGN, NUM, SEMI, EOF)
        assertEquals(9, tokens.size());
        assertEquals("x", tokens.get(0).lexeme());
        assertEquals(":=", tokens.get(1).lexeme());
        assertEquals("101", tokens.get(2).lexeme());
        assertEquals(";", tokens.get(3).lexeme());
        assertEquals("y", tokens.get(4).lexeme());
        assertEquals(":=", tokens.get(5).lexeme());
        assertEquals("2", tokens.get(6).lexeme());
        assertEquals(";", tokens.get(7).lexeme());
        assertEquals(TokenType.EOF, tokens.get(8).type());
    }

    @Test
    public void computeLineColumnTokien() {
        String input = "x := false; // comentário simples\n/* comentário\n\nde múltiplas linhas */ y := 2;";
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        assertEquals(4, tokens.get(4).line());
        assertEquals(25, tokens.get(4).column());
    }
}
