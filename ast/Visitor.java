package ast;

public interface Visitor {
    void visitIfStmt(IfStatement ifStatement);
    //void visitProgram(Program program);
    void visitWhile(While whileStmt);
    void visitAssignment(Assignment assignment);
    void visitBlock(Block block);
    void visitFunction(Function function);

    Object visitArithmBinary(BinaryExpression.Arithm binaryArithm);
    Object visitLogicalBinary(BinaryExpression.Logical binaryLogical);
    Object visitCall(CallExpression callExpression);
    Object visitIfExpr(IfExpression ifExpression);
    Object visitLiteral(Literal literal);
    Object visitUnary(Unary unary);
    Object visitVariable(Variable variable);
}
