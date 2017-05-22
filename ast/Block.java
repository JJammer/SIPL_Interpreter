package ast;


import java.util.List;

public class Block implements Statement{
    private List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBlock(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement);
        }
        return String.format( "begin %n%s end%n", sb);
    }
}
