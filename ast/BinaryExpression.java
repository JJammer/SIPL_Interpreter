package ast;

import token.Token;

public abstract class BinaryExpression implements Expression {
    private Expression left;
    private Token operation;
    private Expression right;

    public BinaryExpression(Expression left, Token operation, Expression right) {
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Token getOperation() {
        return operation;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s%n", left.toString(), operation.getLexeme(), right.toString());
    }

    public static class Logical extends BinaryExpression{
        public Logical(Expression left, Token operation, Expression right) {
            super(left, operation, right);
        }

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitLogicalBinary(this);
        }
    }

    public static class Arithm extends BinaryExpression {
        public Arithm(Expression left, Token operation, Expression right) {
            super(left, operation, right);
        }

        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitArithmBinary(this);
        }
    }
}
