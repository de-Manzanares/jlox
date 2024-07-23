package craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap<>();

  Object get(Token name) throws RuntimeError {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(Token name, Object value) throws RuntimeError {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    throw new RuntimeError(name,
            "Undefined variable '" + name.lexeme + "'.");
  }


  /*
   * "My rule about variables and scoping is, “When in doubt, do what Scheme
   * does”. The Scheme folks have probably spent more time thinking about
   * variable scope than we ever will—one of the main goals of Scheme was to
   * introduce lexical scoping to the world—so it’s hard to go wrong if you
   * follow in their footsteps.
   * Scheme allows redefining variables at the top level."
   */
  void define(String name, Object value) {
    values.put(name, value);
  }
}
