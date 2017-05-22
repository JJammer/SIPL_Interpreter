import token.Token;
import token.TokenType;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Lexer {
    private int line = 1;       //current line of code
    private int current = 0;    // current position
    private int start = 0;      // starting position of new lexeme
    private String input;


    private static final HashMap<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();

        keywords.put("begin", TokenType.BEGIN);
        keywords.put("end", TokenType.END);

        keywords.put("and", TokenType.AND);
        keywords.put("or", TokenType.OR);
        keywords.put("not", TokenType.NOT);

        keywords.put("if", TokenType.IF);
        keywords.put("then", TokenType.THEN);
        keywords.put("else", TokenType.ELSE);

        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);

        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);

        keywords.put("func", TokenType.FUNC);
        keywords.put("program", TokenType.PROGRAM);
    }

    private List<Token> tokens = new ArrayList<>();

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, null));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= input.length();
    }

    private void scanToken() { // find next token
        char c = returnSymbol();
        boolean hasError = false;
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case  ',':
                addToken(TokenType.COMMA);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;

            case '/':
                addToken(TokenType.SLASH);
                break;
            case '*':
                addToken(TokenType.ASTERISK);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case ':':
                if (match('=')) {
                    addToken(TokenType.ASSIGN);
                } else {
                    SIPL.error(line, "Unexpected character");
                }
                break;

            case '=':
                addToken(TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '!':
                if (match('=')) {
                    addToken(TokenType.NOT_EQUAL);
                } else {
                    SIPL.error(line, "Unexpected character");
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isLetter(c)){
                    identifier();
                } else {
                    SIPL.error(line, "Unexpected character");
                }

        }
    }

    private void number() {
        while (isDigit(peek())) {
            returnSymbol();
        }
        addToken(TokenType.NUMBER, new BigInteger(input.substring(start, current)));
    }

    private void identifier() {
        while (isLetter(peek())) {
            returnSymbol();
        }

        // Is identifier a reserved word?
        String text = input.substring(start, current);
        TokenType type = keywords.get(text);

        if (type == TokenType.TRUE || type == TokenType.FALSE) {
            addToken(type, Boolean.parseBoolean(text));
        } else {
            if (type == null) {
                type = TokenType.IDENTIFIER;
            }

            addToken(type);
        }
    }

    private boolean isLetter(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private char peek() {
        return (!isAtEnd()) ? input.charAt(current) : '\0';
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        tokens.add(new Token(type, input.substring(start, current), line, literal));
    }

    private boolean match(char c) {
        if (peek() != c) {
            return false;
        }
        ++current;
        return true;
    }

    private char returnSymbol() {
        return input.charAt(current++);
    }
}
