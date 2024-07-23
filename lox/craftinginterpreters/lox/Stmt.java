package craftinginterpreters.lox;

abstract class Stmt {
  interface Visitor<R> {
    R visitExpressionStmt(Expression stmt) throws RuntimeError;

    R visitPrintStmt(Print stmt) throws RuntimeError;

    R visitVarStmt(Var stmt) throws RuntimeError;
  }

  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) throws RuntimeError {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }

  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) throws RuntimeError {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }

  static class Var extends Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) throws RuntimeError {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }

  abstract <R> R accept(Visitor<R> visitor) throws RuntimeError;
}
