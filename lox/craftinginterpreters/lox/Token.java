package craftinginterpreters.lox;

class Token {
  // package-private member variables (default access level is package-private)
  final TokenType type; // `final` is similar to `const` in c++
  final String lexeme;
  final Object literal;
  final int line;

  // constructor
  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
