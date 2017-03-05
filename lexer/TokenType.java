package lexer;

public enum TokenType {
    // punctuation
    LEFT_PAREN, RIGHT_PAREN, SEMICOLON,

    // operation
    SLASH, MINUS, PLUS, STAR, ASSIGN,

    // boolean operation
    AND, OR, NOT, EQUAL,
    GREATER, LESS, LESS_EQUAL,
    GREATER_EQUAL, NOT_EQUAL,


    // literals
    NUMBER,

    // keywords
    BEGIN, END, IF, THEN, ELSE,
    WHILE, DO, TRUE, FALSE
}
