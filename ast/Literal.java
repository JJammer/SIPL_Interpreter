package ast;

public class Literal implements Expression{
    private Object value;

    public Literal(Object value) {
        this.value = value;
    }

    public Object getValue() { return value; }

    @Override
    public Object accept(Visitor visitor) {
        return visitor.visitLiteral(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
