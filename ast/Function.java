package ast;

import token.Token;

import java.util.ArrayList;
import java.util.List;

public class Function implements Statement{
    private Token name;
    private List<Token> parameters;
    private Expression body;

    public Function(Token name, List<Token> parameters, Expression body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public Token getName() {
        return name;
    }

    public List<Token> getParameters() {
        return parameters;
    }

    public Expression getBody() {
        return body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFunction(this);
    }

    @Override
    public String toString() {
        String paramString = "";
        if (parameters != null) {
            StringBuilder sb = new StringBuilder();
            for (Token parameter : parameters) {
                sb.append(parameter);
            }
            paramString = sb.toString();
        }
        return String.format("func %s(%s) %s", name.getLexeme(), paramString, body.toString());
    }
}
