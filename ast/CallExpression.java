package ast;

import token.Token;

import java.util.ArrayList;
import java.util.List;

public class CallExpression implements Expression{
    private Token name;
    private List<Expression> arguments = new ArrayList<>();

    public CallExpression(Token name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public Token getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visitCall(this);
    }

    @Override
    public String toString() {
        String stringArg = "";
        if (arguments != null) {
            StringBuilder sb = new StringBuilder();
            for (Expression exp : arguments) {
                sb.append(exp);
            }
            stringArg = sb.toString();
        }
        return String.format("%s(%s)", name.getLexeme(), stringArg);
    }
}
