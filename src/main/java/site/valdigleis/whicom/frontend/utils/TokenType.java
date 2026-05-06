package site.valdigleis.whicom.frontend.utils;

public enum TokenType {
    // Palavras reservadas
    IF, THEN, ELSE, WHILE, DO, SKIP, TRUE, FALSE, AND, OR, NOT,
    // Operadores e Símbolos de pontuação
    ASSIGN,   // :=
    PLUS,     // +
    MINUS,    // -
    TIMES,    // *
    EQ,       // =
    LT,       // <
    SEMI,     // ;
    LBRACE,   // {
    RBRACE,   // }
    LPAREN,   // (
    RPAREN,   // )
    // Identificadores
    ID,
    // Números
    NUM,
    // Fim de arquivo
    EOF       
}
