import ast.*;
import token.Token;
import token.TokenType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Program parseProgram(){
        try {
            consume(TokenType.PROGRAM, "Expect 'program'.");
            List<Function> functions = null;
            if (check(TokenType.FUNC)) {
                functions = parseFunctions();
            }
            Block body = parseBlock();
            return new Program(functions, body);

        } catch (ParseError parseError) {
            return null;
        }
    }

    private List<Function> parseFunctions() {
        List<Function> functions = new ArrayList<>();
        functions.add(parseFunction());

        while (match(TokenType.SEMICOLON)) {
            functions.add(parseFunction());
        }
        return functions;
    }

    private Function parseFunction() {
        try {
            consume(TokenType.FUNC, "Expect 'func' for function declaration.");
            Token name = consume(TokenType.IDENTIFIER, "Expect function name.");
            consume(TokenType.LEFT_PAREN, "Expect '(' after function name.");

            List<Token> arguments = null;

            if (check(TokenType.IDENTIFIER)) {
                arguments = parseArguments();
            }
            consume(TokenType.RIGHT_PAREN, "Expect ')'.");
            consume(TokenType.EQUAL, "Expect '=' before function body.");

            Expression body = parseExpression();

            return new Function(name, arguments, body);
        } catch (ParseError parseError) {
            skipFuncError();
            return null;
        }
    }

    private List<Token> parseArguments() {
        List<Token> arguments = new ArrayList<>();
        arguments.add(nextToken());

        while (match(TokenType.COMMA)) {
            arguments.add(nextToken());
        }

        return arguments;
    }

    private Block parseBlock() {
        try {
            List<Statement> statements = new ArrayList<>();
            consume(TokenType.BEGIN, "Expect 'begin'");

            statements.add(parseStatement());
            while (match(TokenType.SEMICOLON)) {
                statements.add(parseStatement());
            }

            consume(TokenType.END, "Expect 'end' after block.");
            return new Block(statements);

        } catch (ParseError parseError) {
            skipError();
            return null;
        }
    }

    private Statement parseStatement() {
        try {
            if (match(TokenType.IF)) {
                return parseIfStmt();
            }
            if (match(TokenType.WHILE)) {
                return parseWhile();
            }
            if (match(TokenType.IDENTIFIER)) {
                return parseAssignment();
            }
            throw error(peek(), "Invalid statement");
        } catch (ParseError parseError) {
            skipError();
            return null;
        }
    }

    private Assignment parseAssignment() {
        Token variable = previous();
        consume(TokenType.ASSIGN, "Expect ':='.");
        Expression expression;

        expression = parseBoolExpr();

        return new Assignment(variable, expression);
    }

    private While parseWhile() {
        Token whileToken = previous();
        Expression condition = parseBoolExpr();

        consume(TokenType.DO, "Expect 'do' after condition.");
        Block body = parseBlock();

        return new While(whileToken, condition, body);
    }

    private IfStatement parseIfStmt() {
        Token ifToken = previous();
        Expression condition = parseBoolExpr();

        consume(TokenType.THEN, "Expect 'then' after condition.");
        Block thenBranch = parseBlock();

        Block elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = parseBlock();
        }
        return new IfStatement(ifToken, condition, thenBranch, elseBranch);
    }

    private Expression parseBoolExpr() {
        return parseOr();
    }

    private Expression parseOr() {
        Expression expr = parseAnd();
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expression right = parseAnd();
            expr = new BinaryExpression.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expression parseAnd() {
        Expression expr = parseExpression();
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expression right = unary();
            expr = new BinaryExpression.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expression parseEquality() {
        Expression expr = parsePlusMinus();

        if (match(TokenType.EQUAL, TokenType.LESS_EQUAL, TokenType.LESS, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.NOT_EQUAL)) {
            Token operator = previous();
            Expression right = parsePlusMinus();
            expr = new BinaryExpression.Arithm(expr, operator, right);
        }
        return expr;
    }

    private Expression parseExpression() {
        return parseEquality();
    }

    private Expression parsePlusMinus() {
        Expression expr = parseMultDiv();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseMultDiv();
            expr = new BinaryExpression.Arithm(expr, operator, right);
        }
        return expr;
    }

    private Expression parseMultDiv() {
        Expression expr = unary();

        while (match(TokenType.ASTERISK, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = parseMultDiv();
            expr = new BinaryExpression.Arithm(expr, operator, right);
        }
        return expr;
    }

    private Expression unary() {
        if (match(TokenType.MINUS, TokenType.NOT)) {
            Token operator = previous();
            Expression right = unary();
            return new Unary(operator, right);
        }
        return parsePrimary();
    }

    private Expression parsePrimary() {
        if (match(TokenType.LEFT_PAREN)) {
            Expression expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' ");
            return expr;
        }
        if (match(TokenType.IF)) {
            return parseIfExpr();
        }
        if (match(TokenType.NUMBER, TokenType.TRUE, TokenType.FALSE)) {
            return new Literal(previous().getLiteral());
        }
        if (check(TokenType.IDENTIFIER) && peekNext().getType() == TokenType.LEFT_PAREN) {
            return parseCallExpression();
        } else
        if (match(TokenType.IDENTIFIER)) {
            return new Variable(previous());
        }

        throw error(peek(), "Invalid expression");
    }

    private Expression parseCallExpression() {
        Token name = nextToken();
        consume(TokenType.LEFT_PAREN, "Expect '(' in function call.");
        List<Expression> arguments;

        if (match(TokenType.RIGHT_PAREN)) {
            arguments = null;
        } else {
            arguments = new ArrayList<Expression>();
            Expression expr = parseExpression();
            arguments.add(expr);

            while (!match(TokenType.RIGHT_PAREN)) {
                consume(TokenType.COMMA, "Expect ',' between arguments in function call");
                expr = parseExpression();
                arguments.add(expr);
            }
        }

        return new CallExpression(name, arguments);
    }

    private Expression parseIfExpr() {
        Token ifToken = previous();
        Expression condition = parseBoolExpr();

        consume(TokenType.THEN, "Expect 'then' after condition.");
        Expression thenBranch = parseExpression();

        Expression elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = parseExpression();
        }

        return new IfExpression(ifToken, condition, thenBranch, elseBranch);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) { return nextToken(); }

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        SIPL.error(token, message);
        return new ParseError();
    }

    private boolean match(TokenType... tokenTypes) {
        for (TokenType type : tokenTypes) {
            if (check(type)) {
                nextToken();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().getType() == tokenType;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token peekNext() {
        return tokens.get(current + 1);
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token nextToken() {
        return tokens.get(current++);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void skipFuncError() {

        while (!isAtEnd()) {
            switch (peek().getType()) {
                case SEMICOLON:
                case FUNC:
                case BEGIN:
                    return;
            }
            nextToken();
        }
    }

    private void skipError() {
        while (!isAtEnd()) {
            switch (peek().getType()) {
                case END:
                case SEMICOLON:
                case FUNC:
                case IF:
                case WHILE:
                    return;
            }
            nextToken();
        }
    }
}
