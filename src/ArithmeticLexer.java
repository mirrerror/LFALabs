import java.util.ArrayList;
import java.util.List;

public class ArithmeticLexer {

    public enum TokenType {
        NUMBER,
        OPERATOR,
        LEFT_PAREN,
        RIGHT_PAREN,
        WHITESPACE,
        ERROR
    }

    public static class Token {
        private TokenType type;
        private String value;
        private int position;

        public Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }

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

        @Override
        public String toString() {
            return "[" + type + ": " + value + ", position: " + position + "]";
        }
    }

    private boolean ignoreWhitespace = false;

    public ArithmeticLexer() {}

    public ArithmeticLexer(boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
    }

    public void setIgnoreWhitespace(boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
    }

    public boolean getIgnoreWhitespace() {
        return ignoreWhitespace;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        int currentPosition = 0;

        for (char c : input.toCharArray()) {
            currentPosition++;
            if (Character.isWhitespace(c)) {
                if (ignoreWhitespace) {
                    continue;
                }
                if (!currentToken.isEmpty()) {
                    tokens.add(createToken(currentToken.toString(), currentPosition - currentToken.length()));
                    currentToken.setLength(0);
                }
                tokens.add(new Token(TokenType.WHITESPACE, Character.toString(c), currentPosition));
            } else if (isOperator(c)) {
                if (!currentToken.isEmpty()) {
                    tokens.add(createToken(currentToken.toString(), currentPosition - currentToken.length()));
                    currentToken.setLength(0);
                }
                tokens.add(new Token(TokenType.OPERATOR, Character.toString(c), currentPosition));
            } else if (Character.isDigit(c)) {
                currentToken.append(c);
            } else if (c == '(') {
                if (!currentToken.isEmpty()) {
                    tokens.add(createToken(currentToken.toString(), currentPosition - currentToken.length()));
                    currentToken.setLength(0);
                }
                tokens.add(new Token(TokenType.LEFT_PAREN, Character.toString(c), currentPosition));
            } else if (c == ')') {
                if (!currentToken.isEmpty()) {
                    tokens.add(createToken(currentToken.toString(), currentPosition - currentToken.length()));
                    currentToken.setLength(0);
                }
                tokens.add(new Token(TokenType.RIGHT_PAREN, Character.toString(c), currentPosition));
            } else {
                return invalidTokenError(Character.toString(c), currentPosition);
            }
        }

        if (!currentToken.isEmpty()) {
            tokens.add(createToken(currentToken.toString(), currentPosition - currentToken.length()));
        }

        return tokens;
    }

    private List<Token> invalidTokenError(String token, int position) {
        List<Token> errorTokens = new ArrayList<>();
        errorTokens.add(new Token(TokenType.ERROR, "Invalid character: '" + token + "' at position " + position + ".", position));
        return errorTokens;
    }

    private Token createToken(String tokenString, int position) {
        if (tokenString.matches("\\d+")) {
            return new Token(TokenType.NUMBER, tokenString, position);
        } else {
            return new Token(TokenType.ERROR, "Invalid token: " + tokenString, position);
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
}
