package craftinginterpreters.lox;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>,
        Stmt.Visitor<Void> {
  // post order traversal
  // each node evaluates its children before doing its own work

  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) throws RuntimeError {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        // cast to double dynamically typed lol
        checkNumberOperand(expr.operator, right);
        return -(double) right;
    }
    // unreachable
    return null;
  }

  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean) object;
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    // handle null specifically
    if (a == null && b == null) return true;
    if (a == null) return false;
    return a.equals(b);
  }

  private String stringify(Object object) {
    if (object == null) return "nil";
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return object.toString();
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) throws RuntimeError {
    return evaluate(expr.expression);
  }

  private Object evaluate(Expr expr) throws RuntimeError {
    return expr.accept(this);
  }

  private void execute(Stmt stmt) throws RuntimeError {
    stmt.accept(this);
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) throws RuntimeError {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case GREATER:
        checkNumberOperand(expr.operator, left, right);
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperand(expr.operator, left, right);
        return (double) left >= (double) right;
      case LESS:
        checkNumberOperand(expr.operator, left, right);
        return (double) left < (double) right;
      case LESS_EQUAL:
        checkNumberOperand(expr.operator, left, right);
        return (double) left <= (double) right;
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
      case MINUS:
        checkNumberOperand(expr.operator, left, right);
        return (double) left - (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }
        if (left instanceof String && right instanceof String) {
          return (String) left + (String) right;
        }
        throw new RuntimeError(expr.operator,
                "Operands must be two numbers or two strings.");
      case SLASH:
        checkNumberOperand(expr.operator, left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperand(expr.operator, left, right);
        return (double) left * (double) right;
    }
    // unreachable
    return null;
  }

  private void checkNumberOperand(Token operator, Object operand) throws RuntimeError {
    if (operand instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperand(Token operator, Object left, Object right) throws RuntimeError {
    if (left instanceof Double && right instanceof Double) return;
    throw new RuntimeError(operator, "Operand must be a number.");
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) throws RuntimeError {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) throws RuntimeError {
    Object value = evaluate(stmt.expression);
    System.out.println(stringify(value));
    return null;
  }
}
