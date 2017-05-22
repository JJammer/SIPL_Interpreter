import ast.Program;
import token.Token;
import token.TokenType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SIPL {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException{
        String path = "/Users/Vlada/Desktop/SIPL_Interpreter/src/tests/test1.txt";
        runFile(path);

    }

    private static void runFile(String path) throws IOException{
        String fileContent = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        run(fileContent);

        if (hadError) { System.exit(65); }      // data format error
        if (hadRuntimeError) {System.exit(70);} // internal software error
    }

    private static void run(String content) {
        Lexer lexer = new Lexer(content);
        List<Token> tokens = lexer.scanTokens();
        Program program = new Parser(tokens).parseProgram();

        if (hadError) { return; }

        Interpreter interpreter = new Interpreter();
        Environment result = null;
        try {
            result = interpreter.interpret(program);

        } catch (Throwable e) {
            System.err.println(e.getClass().getSimpleName());
            hadRuntimeError = true;
        }
        if (!hadRuntimeError) { System.out.println(result); }
    }

    static void error(int line, String message) {
        report(line, "", message);
        hadError = true;
    }

    static private void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
    }

    static void error(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            report(token.getLine(), " at end", message);
        } else {
            report(token.getLine(), " at '" + token.getLexeme() + "'", message);
        }
        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        report(error.token.getLine(), "", error.getMessage());
        hadRuntimeError = true;
    }
}
