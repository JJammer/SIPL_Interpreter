package ast;

import token.Token;

public class IfExpression implements Expression {
    private Token ifToken;      // for error handling
    private Expression condition;
    private Expression thenBranch;
    private Expression elseBranch;

    public IfExpression(Token ifToken, Expression condition, Expression thenBranch, Expression elseBranch) {
        this.ifToken = ifToken;
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Token getIfToken() { return ifToken; }

    public Expression getCondition() { return condition; }

    public Expression getThenBranch() {
        return thenBranch;
    }

    public Expression getElseBranch() {
        return elseBranch;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visitIfExpr(this);
    }

    @Override
    public String toString() {
        return String.format("if %s then %s else %s ", condition.toString(), thenBranch.toString(), elseBranch.toString());
    }
}
