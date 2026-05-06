package site.valdigleis.whicom.frontend.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import site.valdigleis.whicom.frontend.utils.Symbol;
import site.valdigleis.whicom.frontend.utils.SymbolKind;
import site.valdigleis.whicom.frontend.utils.Token;
import site.valdigleis.whicom.frontend.utils.TokenType;

public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> KEYWORDS;

    static {
        KEYWORDS = new HashMap<>();
        KEYWORDS.put("if",    TokenType.IF);
        KEYWORDS.put("then",  TokenType.THEN);
        KEYWORDS.put("else",  TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("do",    TokenType.DO);
        KEYWORDS.put("skip",  TokenType.SKIP);
        KEYWORDS.put("true",  TokenType.TRUE);
        KEYWORDS.put("false", TokenType.FALSE);
        KEYWORDS.put("and",   TokenType.AND);
        KEYWORDS.put("or",    TokenType.OR);
        KEYWORDS.put("not",   TokenType.NOT);
    }

    private final Map<String, Symbol> symbolTable = new HashMap<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            this.start = current;
            this.scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private void scanToken() {
        char c = this.advance();
        switch (c) {
            case '/' -> {
                if (match('/')) {
                    while (this.peek() != '\n' && !this.isAtEnd()) 
                        this.advance();
                } else if (match('*')) {
                    this.multiLineComment();
                } else {
                    System.err.printf("Error [L:%d, C:%d]: Invalid character '/'", line, column - 1);
                }
            }
            case '(' -> this.addToken(TokenType.LPAREN);
            case ')' -> this.addToken(TokenType.RPAREN);
            case '{' -> this.addToken(TokenType.LBRACE);
            case '}' -> this.addToken(TokenType.RBRACE);
            case ';' -> this.addToken(TokenType.SEMI);
            case '+' -> this.addToken(TokenType.PLUS);
            case '-' -> this.addToken(TokenType.MINUS);
            case '*' -> this.addToken(TokenType.TIMES);
            case '=' -> this.addToken(TokenType.EQ);
            case '<' -> this.addToken(TokenType.LT);
            case ':' -> { 
                if (this.match('=')) {
                    this.addToken(TokenType.ASSIGN); 
                } else {
                    System.err.printf("Erro [L:%d, C:%d]: Expected =, but received %n", line, column, c);
                }
            }
            case ' ', '\r', '\t' -> {} // Espaços, tabulações apenas aumentam a coluna (no advance)
            case '\n' -> { this.line++;  this.column = 1; }
            default -> { 
                if (Character.isDigit(c)) { 
                    this.number();
                } else if (Character.isLetter(c)) {
                    this.identifier();
                } else {
                    System.err.printf("Erro [L:%d, C:%d]: Invalid character... %n", line, column, c);
                }
            }
        }
    }

    private char advance() {
        this.column++;
        return this.source.charAt(this.current++);
    }
    
    private void addToken(TokenType type) {
        String text = this.source.substring(this.start, this.current);
        int tokenColumn = this.column - text.length();
        Token token = new Token(type, text, line, tokenColumn);
        this.tokens.add(token);
    }

    private void identifier() {
        while (Character.isLetterOrDigit(this.peek())) {
            this.advance();
        }
        String text = this.source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.ID);
        if (type == TokenType.ID) {
            Symbol s = new Symbol(text, SymbolKind.VARIABLE, null, null);
            this.symbolTable.putIfAbsent(text, s);
        }
        this.addToken(type);
    }

    private void number() {
        while (Character.isDigit(this.peek())) advance();
        this.addToken(TokenType.NUM);
    }

    private boolean isAtEnd() { 
        return current >= source.length(); 
    }

    private char peek() { 
        return this.isAtEnd() ? '\0' : this.source.charAt(current); 
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) {
        return false;
        }
        this.current++; 
        this.column++; // Incrementamos a coluna pois consumimos o '=' do ':='
        return true;
    }

    public Map<String, Symbol> getSymbolTable() { 
        return this.symbolTable; 
    }

    private void multiLineComment() {
        while (!isAtEnd()) {
            if (this.peek() == '*' && this.peekNext() == '/') {
                this.advance(); // Consome '*'
                this.advance(); // Consome '/'
                return;
            }
            if (this.peek() == '\n') {
                this.line++;
                this.column = 1;
            }
            this.advance();
        }
        System.err.printf("Error [Line:%d]: multi-line comment not closed.", line);
    }

    private char peekNext() {
        if (current + 1 >= this.source.length()) {
            return '\0';
        }
        return this.source.charAt(current + 1);
    }

}
