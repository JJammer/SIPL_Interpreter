package lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Lexer {
    private char nextSymbol;
    private boolean isAtEnd;
    private StringBuilder currentLexeme = new StringBuilder();
    private int line = 1;

    private BufferedReader reader;

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
    }

    private List<Token> tokens = new ArrayList<>();

    Lexer(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
            nextSymbol = (char) read();

        } catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
        }
    }

    public List<Token> scanTokens() {
        while (!isAtEnd) {
            scanToken();
        }
        return tokens;
    }

    private void scanToken() { // find next token and return it
        char c = returnSymbol();
        boolean hasError = false;
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
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
                    hasError = true;
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
                    hasError = true;
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                currentLexeme.deleteCharAt(0);
                break;

            case '\n':
                currentLexeme.deleteCharAt(0);
                line++;
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isLetter(c)){
                    identifier();
                }

        }
    }

    private void number() {
        while (isDigit(peek())) {
            returnSymbol();
        }
        addToken(TokenType.NUMBER, currentLexeme.toString());
    }

    private void identifier() {
        while (isLetter(peek())) {
            returnSymbol();
        }

        // Is identifier a reserved word?
        TokenType type = keywords.get(currentLexeme.toString());
        if (type == null) { type = TokenType.IDENTIFIER; }

        addToken(type);
    }

    private boolean isLetter(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private char peek() {
        return (!isAtEnd) ? nextSymbol : '\0';
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        tokens.add(new Token(type, currentLexeme.toString(), line, literal));
        currentLexeme = new StringBuilder();
    }

    private boolean match(char c) {
        if (nextSymbol == c) {
            returnSymbol();
            return true;
        } else {
            return false;
        }
    }

    private char returnSymbol() {
        char symbolToReturn = nextSymbol;
        currentLexeme.append(symbolToReturn);
        nextSymbol = (char) read();
        return symbolToReturn;
    }

    private int read() {  // wrapper for reader.read();
        int r = -2;
        try {
            r = reader.read();
            if (r == -1) { isAtEnd = true; }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return r;
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer("/Users/Vlada/Desktop/SIPL_Interpreter/src/tests/test1.txt");
        for (Token t : lexer.scanTokens()) {
            System.out.println(t);
        }
    }
}
