package ast;

import java.util.List;

public class Program implements Node{
    private List<Function> functions;
    private Block body;

    public Program(List<Function> functions, Block body) {
        this.functions = functions;
        this.body = body;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public Block getBody() {
        return body;
    }

    //public void accept(Visitor visitor) {
    //    visitor.visitProgram(this);
    //}

    @Override
    public String toString() {
        String funcs = (functions == null) ? null : functions.toString();
        return String.format("program %n%s %n%s", funcs, body.toString());
    }
}
