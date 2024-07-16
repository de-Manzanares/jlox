package craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static craftinginterpreters.lox.TokenType.*;

class Lexer {
  private final String source;                          // source
  private final List<Token> tokens = new ArrayList<>(); // list of tokens
  private int start = 0;      // first char of the lexeme being lexed
  private int current = 0;    // character currently being considered
  private int line = 1;       // tracks current so tokens know their location

  Lexer(String source) {
    this.source = source;
  }

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and", AND);
    keywords.put("class", CLASS);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("for", FOR);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("nil", NIL);
    keywords.put("or", OR);
    keywords.put("print", PRINT);
    keywords.put("return", RETURN);
    keywords.put("super", SUPER);
    keywords.put("this", THIS);
    keywords.put("true", TRUE);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
  }

  // lex source, add EOF token at the end
  List<Token> lexTokens() {
    while (!isAtEnd()) {
      start = current;
      lexToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void lexToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN);     // single chars
        break;
      case ')': addToken(RIGHT_PAREN);
        break;
      case '{': addToken(LEFT_BRACE);
        break;
      case '}': addToken(RIGHT_BRACE);
        break;
      case ',': addToken(COMMA);
        break;
      case '.': addToken(DOT);
        break;
      case '-': addToken(MINUS);
        break;
      case '+': addToken(PLUS);
        break;
      case ';': addToken(SEMICOLON);
        break;
      case '*': addToken(STAR);
        break;

      // sometimes single chars
      case '!': addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }

      case ' ':                           // ignore whitespaces
      case '\r':
      case '\t':
        break;
      case '\n':
        line++;
        break;

      case '"': string();
        break;

      default:
        if (isDigit(c)) {   // in default so we don't have cases for 0-9
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          // lexical errors - unexpected characters
          // if there is an error, we don't execute the code, but we will keep
          // scanning so that we can report all the syntax errors at once.
          Lox.error(line, "Unexpected character.");
        }
        break;
    }
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;   // multiline strings lol
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    // the closing "
    advance();

    // trim the surrounding quotes
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);

    // note - lox does not support escape sequences
  }

  private void number() {
    while (isDigit(peek())) advance();  // consume digits

    // Look for decimal
    // lookahead 2 because we don't want to consume the '.'
    // until we're sure there's a number following it
    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      // if the decimal is present and properly placed
      // continue to the end of the number
      while (isDigit(peek())) advance();
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void identifier() {
    // following the rule of maximal much
    // consume the whole token
    while (isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);
    // then check if it is a keyword
    TokenType type = keywords.get(text);
    // if it's not a keyword, it's an identifier
    if (type == null) type = IDENTIFIER;
    addToken(type);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  // consumes next character
  private char advance() {
    return source.charAt(current++);
  }

  // creates token from current lexeme
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  // checking next char for double char operators
  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  // looks ahead, but doesn't consume the character
  // used for comments because we don't want to consume the new line char
  // we '\n' to advance to line count in our lex loop
  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  // note - generally, the smaller the lookahead (1 char in this case) the
  // faster the lexer runs

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  // lox supports alphanumeric identifiers
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }


}


