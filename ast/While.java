package ast;

import token.Token;

public class While implements Statement{
    private Token whileToken;       //for error handling
    private Expression condition;
    private Block body;

    public While(Token whileToken, Expression condition, Block body) {
        this.whileToken = whileToken;
        this.condition = condition;
        this.body = body;
    }

    public Token getWhileToken() { return whileToken; }

    public Expression getCondition() { return condition; }

    public Block getBody() {
        return body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitWhile(this);
    }

    @Override
    public String toString() {
        return String.format("while %s %n %s", condition.toString(), body.toString());
    }
}
