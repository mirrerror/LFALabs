package lexer;

// Class representing a token with its type, value, and position in the input string.
public class Token {
    private TokenType type;     // Type of the token.
    private String value;       // Value of the token.
    private int position;       // Position of the token in the input string.

    // Constructor to initialize the token.
    public Token(TokenType type, String value, int position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    // Getters and setters for token properties.
    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    // String representation of the token.
    @Override
    public String toString() {
        return "[" + type + ": " + value + ", position: " + position + "]";
    }
}
