package token;

public enum TokenType {
    // punctuation
    LEFT_PAREN, RIGHT_PAREN, SEMICOLON, COMMA,

    // operation
    SLASH, MINUS, PLUS, ASTERISK, ASSIGN,

    // boolean operation
    AND, OR, NOT, EQUAL,
    GREATER, LESS, LESS_EQUAL,
    GREATER_EQUAL, NOT_EQUAL,


    // literals
    NUMBER, IDENTIFIER,

    // keywords
    BEGIN, END, IF, THEN, ELSE,
    WHILE, DO, TRUE, FALSE, FUNC, PROGRAM,

    EOF
}
