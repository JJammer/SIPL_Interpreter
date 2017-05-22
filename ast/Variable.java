package ast;

import token.Token;

public class Variable implements Expression{
    private Token name;

    public Variable(Token name) {
        this.name = name;
    }

    public Token getName() {
        return name;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visitVariable(this);
    }

    @Override
    public String toString() {
        return name.getLexeme();
    }
}
