package lexer;

class Token {
    final TokenType type;
    final String lexeme;
    final int line;
    final Object literal;

    public Token(TokenType type, String lexeme, int line, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.literal = literal;
    }

    @Override
    public String toString() {
        return String.format("%s '%s'", type, lexeme);
    }
}
