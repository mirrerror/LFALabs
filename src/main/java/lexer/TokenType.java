package lexer;

public enum TokenType {
    NUMBER,         // Represents numeric values.
    OPERATOR,       // Represents arithmetic operators (+, -, *, /).
    LEFT_PAREN,     // Represents left parenthesis '('.
    RIGHT_PAREN,    // Represents right parenthesis ')'.
    WHITESPACE,     // Represents whitespace characters.
    ERROR           // Represents an error token.
}
