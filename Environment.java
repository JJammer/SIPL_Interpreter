import token.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment outer;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        outer = null;
    }

    Environment(Environment outer) {
        this.outer = outer;
    }

    public Environment createInnerEnvironment() { return new Environment(this); }

    public void assign(Token name, Object value) {
        Environment owner = getOwner(name);
        if (owner == null) { this.set(name, value); }
        else owner.set(name, value);
    }

    private void set(Token name, Object value) {
        values.put(name.getLexeme(), value);
    }

    // environment that contains variable
    private Environment getOwner(Token name) {
        if (values.containsKey(name.getLexeme())) { return this; }

        if (outer != null) { return outer.getOwner(name); }

        return null;
    }

    Object get(Token name) {

        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }

        if (outer != null) return outer.get(name);

        // Unreachable error
        throw new RuntimeError(name, "Variable with this name doesn't exist.");
    }

    @Override
    public String toString() {
        return values.toString();   // TODO
    }
}
