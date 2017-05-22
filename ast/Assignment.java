package ast;

import token.Token;

public class Assignment implements Statement{
    private Token variable;
    private Expression right;

    public Assignment(Token variable, Expression right) {
        this.variable = variable;
        this.right = right;
    }

    public Token getVariable() {
        return variable;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAssignment(this);
    }

    @Override
    public String toString() {
        return String.format("%s := %s%n", variable.getLexeme(), right.toString());
    }
}
