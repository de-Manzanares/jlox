package craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();
  static boolean hadError = false; // don't run a program with an error
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException, RuntimeError {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {  // `lox <FILENAME>` interpret file
      runFile(args[0]);
    } else {
      runPrompt();                  // `lox` REPL (read evaluate print) loop
    }
  }


  // run file `lox <FILENAME>`
  private static void runFile(String path) throws IOException, RuntimeError {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    // runs the Lexer with a String parameter
    // the String is created by decoding the bytes using UTF-8
    run(new String(bytes, Charset.defaultCharset()));

    // Indicate an error in the exit code
    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  // run REPL `lox`
  private static void runPrompt() throws IOException, RuntimeError {
    // reads bytes from console input and decodes to UTF-8 characters
    InputStreamReader input = new InputStreamReader(System.in);
    // wrap with a buffered reader?
    BufferedReader reader = new BufferedReader(input);

    for (; ; ) {                        // infinite loop
      System.out.print("> ");           // prompt char
      String line = reader.readLine();  // read line
      if (line == null) break;          // if null, end of use
      run(line);                        // otherwise run it =-)
      hadError = false;                 // don't crash REPL for an error
    }
  }

  private static void run(String source) throws RuntimeError {
    Lexer lexer = new Lexer(source);          // lexical analysis
    List<Token> tokens = lexer.lexTokens();   // breaking a source into tokens
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();
    if (hadError) return;
    interpreter.interpret(statements);
  }

  // error handler
  static void error(int line, String message) {
    report(line, "", message);
  }

  private static void report(int line, String where, String message) {
    System.err.println(
            "[line " + line + "] Error " + where + ": " + message);
    hadError = true;
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
            "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }


}



