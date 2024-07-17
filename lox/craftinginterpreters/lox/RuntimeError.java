package craftinginterpreters.lox;

class RuntimeError extends Throwable{
  final Token token;

  RuntimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
}
