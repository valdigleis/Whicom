package site.valdigleis.whicom.analyzers;

import java.util.List;

public class ParserTest {
    
    private void parseCode(String input) {
        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        parser.parse();
    }
    
}
