package token;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final Object literal;

    public Token(TokenType type, String lexeme, int line, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.literal = literal;
    }

    @Override
    public String toString() {
        return String.format("%s '%s'", getType(), getLexeme());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Token)) { return false; }
        if (obj == this) { return true; }

        return lexeme.equals(((Token) obj).getLexeme());
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public Object getLiteral() {
        return literal;
    }
}
