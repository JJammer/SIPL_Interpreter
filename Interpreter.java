import ast.*;
import token.Token;
import token.TokenType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Interpreter implements Visitor{

    private List<Function> functions = new ArrayList<>();
    private final Environment globalEnv = new Environment();
    private Environment environment = globalEnv;

    @Override
    public void visitIfStmt(IfStatement ifStatement) {
        Object condition = evaluate(ifStatement.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeError(ifStatement.getIfToken(), "Condition should be boolean.");
        }
        if (isTrue(condition)) { execute(ifStatement.getThenBranch());}
        else if (ifStatement.getElseBranch() != null) {
            execute(ifStatement.getElseBranch());
        }
    }

    public Environment interpret(Program program) {
        try {
            execProgram(program);
        } catch (RuntimeError error) {
            SIPL.runtimeError(error);
        }
        return environment;
    }
    private void execProgram(Program program) {
        if (program.getFunctions() != null) {
            for (Function function : program.getFunctions()) {
                execute(function);
            }
        }
        for (Statement statement : program.getBody().getStatements()) {
            execute(statement);
        }
    }

    @Override
    public void visitWhile(While whileStmt) {
        Object condition = evaluate(whileStmt.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeError(whileStmt.getWhileToken(), "Condition must be boolean");
        }

        while (isTrue(condition)) {
            execute(whileStmt.getBody());

            condition = evaluate(whileStmt.getCondition());
            if (!(condition instanceof Boolean)) {
                throw new RuntimeError(whileStmt.getWhileToken(), "Condition must be boolean");
            }
        }

    }

    @Override
    public void visitAssignment(Assignment assignment) {
        Object value = evaluate(assignment.getRight());
        environment.assign(assignment.getVariable(), value);
    }

    @Override
    public void visitBlock(Block block) {
        executeBody(block.getStatements(), environment.createInnerEnvironment());
    }

    @Override
    public void visitFunction(Function function) {
        functions.add(function);
    }

    @Override
    public Object visitArithmBinary(BinaryExpression.Arithm binaryArithm) {
        Object left = evaluate(binaryArithm.getLeft());
        Object right = evaluate(binaryArithm.getRight());
        checkNumberOperands(binaryArithm.getOperation(), left, right);

        TokenType operationType = binaryArithm.getOperation().getType();

        BigInteger op1 = (BigInteger) left;
        BigInteger op2 = (BigInteger) right;

        switch (operationType) {
            case PLUS:
                return op1.add(op2);
            case MINUS:
                return op1.subtract(op2);
            case SLASH:
                if (op2.equals(BigInteger.ZERO)) {
                    throw new RuntimeError(binaryArithm.getOperation(), "Division by zero!");
                }
                return op1.divide(op2);
            case ASTERISK:
                return op1.multiply(op2);
            case EQUAL:
                return op1.equals(op2);
            case NOT_EQUAL:
                return !op1.equals(op2);
            case GREATER:
                return op1.compareTo(op2) == 1;
            case GREATER_EQUAL:
                return op1.compareTo(op2) > -1;
            case LESS:
                return op1.compareTo(op2) == -1;
            case LESS_EQUAL:
                return op1.compareTo(op2) < 1;
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitLogicalBinary(BinaryExpression.Logical binaryLogical) {
        Object right = evaluate(binaryLogical.getRight());
        checkBoolOperand(binaryLogical.getOperation(), right);

        TokenType operatorType = binaryLogical.getOperation().getType();

        if (isTrue(right) && operatorType == TokenType.OR) { return true; }

        if (!isTrue(right) && operatorType == TokenType.AND) { return false; }

        Object left = evaluate(binaryLogical.getLeft());
        checkBoolOperand(binaryLogical.getOperation(), left);

        return left;
    }

    @Override
    public Object visitCall(CallExpression callExpression) {
        List<Object> arguments = new ArrayList<>();
        Token name = callExpression.getName();
        Function function = null;

        for (Function func : functions) {
            if (func.getName().equals(name)) { function = func; }
        }

        if (function == null) {
            throw new RuntimeError(callExpression.getName(), "Function with this name doesn't exist.");
        }

        //create environment for function
        Environment funcEnvironment = new Environment();

        if (callExpression.getArguments() != null && function.getParameters() != null) {
            for (Expression argument : callExpression.getArguments()) {
                arguments.add(evaluate(argument));
            }

            List<Token> parameters = function.getParameters();
            if (arguments.size() != parameters.size()) {
                throw new RuntimeError(callExpression.getName(), "Wrong number of arguments.");
            }

            for (int i = 0; i < arguments.size(); ++i) {
                funcEnvironment.assign(parameters.get(i), arguments.get(i));
            }
        } else if (callExpression.getArguments() == null || function.getParameters() == null) {
            throw new RuntimeError(callExpression.getName(), "Wrong number of arguments.");
        }

        Environment previous = environment;
        try {
            environment = funcEnvironment;
            return evaluate(function.getBody());    // RETURN HERE!!!
        } finally {
            environment = previous;
        }

    }

    @Override
    public Object visitIfExpr(IfExpression ifExpression) {
        Object condition = evaluate(ifExpression.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeError(ifExpression.getIfToken(), "Condition should be boolean.");
        }

        return (isTrue(condition)) ? evaluate(ifExpression.getThenBranch()) : evaluate(ifExpression.getElseBranch());
    }

    @Override
    public Object visitUnary(Unary unary) {
        Object right = evaluate(unary.getRight());

        TokenType operatorType = unary.getOperation().getType();

        switch (operatorType) {
            case MINUS:
                checkNumberOperand(unary.getOperation(), right);
                return ((BigInteger)right).negate();
            case NOT:
                checkBoolOperand(unary.getOperation(), right);
                return !(boolean)right;
        }

        // unreachable
        return null;
    }

    @Override
    public Object visitVariable(Variable variable) {
        return environment.get(variable.getName());
    }

    @Override
    public Object visitLiteral(Literal literal) {
        return literal.getValue();
    }

    private boolean isTrue(Object obj) {
        return (boolean)obj;
    }

    private void checkBoolOperand(Token operator, Object obj) {
        if (obj instanceof Boolean) { return; }
        throw new RuntimeError(operator, "Operand must be boolean");
    }

    private void checkNumberOperand(Token operator, Object obj) {
        if (obj instanceof BigInteger) { return; }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof BigInteger && right instanceof BigInteger) { return; }
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    private void execute(Statement statement) { statement.accept(this); }

    private void executeBody(List<Statement> statements, Environment env) {
        Environment previous = environment;
        environment = env;
        try {
            environment = env;

            for (Statement statement : statements) {
                execute(statement);
            }

        } finally {
            environment = previous;
        }
    }
}
