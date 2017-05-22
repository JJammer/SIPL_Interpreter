package ast;

import token.Token;

public class Unary implements Expression{
    private Token operation;
    private Expression right;

    public Unary(Token operation, Expression right) {
        this.operation = operation;
        this.right = right;
    }

    public Token getOperation() {
        return operation;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visitUnary(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s", operation.getLexeme(), right.toString());
    }
}
