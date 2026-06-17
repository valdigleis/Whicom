package site.valdigleis.whicom.frontend.analyzers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import site.valdigleis.whicom.frontend.utils.Symbol;
import site.valdigleis.whicom.frontend.utils.Token;
import site.valdigleis.whicom.frontend.utils.AST.Program;
import site.valdigleis.whicom.frontend.utils.AST.expression.IntLiteral;
import site.valdigleis.whicom.frontend.utils.AST.statement.AssignStmt;
import site.valdigleis.whicom.frontend.utils.AST.statement.WhileStmt;

public class ParserTest {
    
    @Test
    public void testAssignmentIntegerLiteral() {
        Lexer lexer = new Lexer("x := 10;");
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens, new HashMap<>());
        Program program = parser.parse_to_AST();
        assertEquals( 1,program.statements().size());
        assertTrue(program.statements().get(0) instanceof AssignStmt);
        AssignStmt stmt = (AssignStmt) program.statements().get(0);
        assertEquals("x", stmt.variable());
        assertTrue(stmt.expression() instanceof IntLiteral);
        IntLiteral literal = (IntLiteral) stmt.expression();
        assertEquals(10, literal.value());
    }

    @Test
    public void testSummationProgram() {
        String source =
                "X := 0;" +
                "i := 0;" +
                "while(i < 100) {" +
                "X := X + (2 * i);" +
                "i := i + 1;" +
                "}";
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens, new HashMap<String, Symbol>());
        Program program = parser.parse_to_AST();
        assertNotNull(program);
        assertEquals( 3, program.statements().size());
        assertTrue(program.statements().get(0) instanceof AssignStmt );
        assertTrue(program.statements().get(1) instanceof AssignStmt );
        assertTrue(program.statements().get(2) instanceof WhileStmt );
    }
}
