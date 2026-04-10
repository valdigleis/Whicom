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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class LexerTest {
    
    @Test
    public void testTokenizeKeywords() {
        Lexer lexer = new Lexer("if then else skip true false and or");
        ArrayList<Token> tokens = lexer.tokenize();
        // Verifica o tamanho: 8 palavras + 1 EOF = 9 tokens
        assertEquals(9, tokens.size());
        assertEquals(Token.Type.IF, tokens.get(0).getType());
        assertEquals(Token.Type.THEN, tokens.get(1).getType());
        assertEquals(Token.Type.ELSE, tokens.get(2).getType());
        assertEquals(Token.Type.SKIP, tokens.get(3).getType());
        assertEquals(Token.Type.TRUE, tokens.get(4).getType());
        assertEquals(Token.Type.FALSE, tokens.get(5).getType());
        assertEquals(Token.Type.AND, tokens.get(6).getType());
        assertEquals(Token.Type.OR, tokens.get(7).getType());
        assertEquals(Token.Type.EOF, tokens.get(8).getType());
    }

    @Test
    public void testTokenizeSymbols() {
        Lexer lexer = new Lexer(": =  < + - * ; ! ( )");
        ArrayList<Token> tokens = lexer.tokenize();
        assertEquals(11, tokens.size());
        assertEquals(Token.Type.ASSIGN, tokens.get(0).getType());
        assertEquals(Token.Type.EQUAL, tokens.get(1).getType());
        assertEquals(Token.Type.LESS, tokens.get(2).getType());
        assertEquals(Token.Type.PLUS, tokens.get(3).getType());
        assertEquals(Token.Type.MINUS, tokens.get(4).getType());
        assertEquals(Token.Type.PRODUCT, tokens.get(5).getType());
        assertEquals(Token.Type.PUNCTUATION, tokens.get(6).getType());
        assertEquals(Token.Type.NOT, tokens.get(7).getType());
        assertEquals(Token.Type.LEFTP, tokens.get(8).getType());
        assertEquals(Token.Type.RIGHTP, tokens.get(9).getType());
        assertEquals(Token.Type.EOF, tokens.get(10).getType());
    }

    @Test
    public void testTokenizeIDandNUMBER() {
        Lexer lexer = new Lexer("I1D 42 id 17");
        ArrayList<Token> tokens = lexer.tokenize();
        assertEquals(5, tokens.size());
        assertEquals(Token.Type.ID, tokens.get(0).getType());
        assertEquals(Token.Type.NUMBER, tokens.get(1).getType());
        assertEquals(Token.Type.ID, tokens.get(2).getType());
        assertEquals(Token.Type.NUMBER, tokens.get(3).getType());
        assertEquals(Token.Type.EOF, tokens.get(4).getType());
    }

    @Test
    public void testEmptyInput() {
        Lexer lexer = new Lexer("");
        ArrayList<Token> tokens = lexer.tokenize();
        assertEquals(1, tokens.size());
        assertEquals(Token.Type.EOF, tokens.get(0).getType());
    }

    @Test(expected = RuntimeException.class)
    public void testUnexpectedCharacter() {
        Lexer lexer = new Lexer("var @");
        lexer.tokenize();
    }

}
