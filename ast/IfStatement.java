package ast;

import token.Token;

public class IfStatement implements Statement{
    private Token ifToken;          // for error handling
    private Expression condition;
    private Block thenBranch;
    private Block elseBranch;

    public IfStatement(Token ifToken, Expression condition, Block thenBranch, Block elseBranch) {
        this.ifToken = ifToken;
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Token getIfToken() { return ifToken; }

    public Expression getCondition() {
        return condition;
    }

    public Block getThenBranch() {
        return thenBranch;
    }

    public Block getElseBranch() {
        return elseBranch;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitIfStmt(this);
    }

    @Override
    public String toString() {
        return String.format("if %sthen %n%s %nelse %s%n", condition, thenBranch, elseBranch);
    }
}
