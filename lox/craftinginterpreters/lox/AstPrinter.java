package craftinginterpreters.lox;

class AstPrinter implements Expr.Visitor<String> {
  String print(Expr expr) throws RuntimeError {
    return expr.accept(this);
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) throws RuntimeError {
    return "";
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) throws RuntimeError {
    return parenthesize(expr.operator.lexeme,
            expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) throws RuntimeError {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitLogicalExpr(Expr.Logical expr) throws RuntimeError {
    return "";
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) throws RuntimeError {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return "";
  }

  private String parenthesize(String name, Expr... exprs) throws RuntimeError {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expr expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");
    return builder.toString();
  }

  public static void main(String[] args) throws RuntimeError {
    Expr expression = new Expr.Binary(
            new Expr.Unary(
                    new Token(TokenType.MINUS, "-", null, 1),
                    new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(
                    new Expr.Literal(45.67)));
    System.out.println(new AstPrinter().print(expression));
  }
}
