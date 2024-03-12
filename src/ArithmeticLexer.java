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
        int leftParenCount = 0;
        int rightParenCount = 0;
        int lastDigitStartingPosition = 0;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (ignoreWhitespace) {
                    currentPosition++;
                    continue;
                }
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                tokens.add(new Token(TokenType.WHITESPACE, Character.toString(c), currentPosition));
            } else if (isOperator(c)) {
                if (requiresNumberBeforeAndAfter(c)) {
                    if (!hasPreviousNumber(input, currentPosition) && !hasPreviousBracket(input, currentPosition)) {
                        return invalidTokenError("Expected number or bracket before operator", currentPosition);
                    }
                    if (!hasNextNumber(input, currentPosition) && !hasNextBracket(input, currentPosition)) {
                        return invalidTokenError("Expected number or bracket after operator", currentPosition);
                    }
                }

                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                tokens.add(new Token(TokenType.OPERATOR, Character.toString(c), currentPosition));
            } else if (Character.isDigit(c)) {
                if(currentToken.isEmpty()) {
                    lastDigitStartingPosition = currentPosition;
                }
                currentToken.append(c);
            } else if (c == '(') {
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                tokens.add(new Token(TokenType.LEFT_PAREN, Character.toString(c), currentPosition));
                leftParenCount++;
            } else if (c == ')') {
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                rightParenCount++;
                if (rightParenCount > leftParenCount) {
                    return invalidTokenError(Character.toString(c), currentPosition);
                }
                tokens.add(new Token(TokenType.RIGHT_PAREN, Character.toString(c), currentPosition));
            } else {
                return invalidTokenError(Character.toString(c), currentPosition);
            }
            currentPosition++;
        }

        if (leftParenCount != rightParenCount) {
            return invalidTokenError("Mismatched parentheses", input.length());
        }

        addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);

        return tokens;
    }

    private List<Token> invalidTokenError(String token, int position) {
        List<Token> errorTokens = new ArrayList<>();
        errorTokens.add(new Token(TokenType.ERROR, "Invalid expression: " + token + " at position " + position + ".", position));
        return errorTokens;
    }

    private Token createToken(String tokenString, int position) {
        if (tokenString.matches("\\d+")) {
            return new Token(TokenType.NUMBER, tokenString, position);
        } else {
            return new Token(TokenType.ERROR, "Invalid expression: " + tokenString + " at position " + position + ".", position);
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static boolean requiresNumberBeforeAndAfter(char c) {
        return isOperator(c);
    }

    private boolean hasNextNumber(String input, int currentPosition) {
        for (int i = currentPosition + 1; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i))) {
                return true;
            } else if (!Character.isWhitespace(input.charAt(i))) {
                return false;
            }
        }
        return false;
    }

    private boolean hasNextBracket(String input, int currentPosition) {
        for (int i = currentPosition + 1; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                return true;
            } else if (!Character.isWhitespace(input.charAt(i))) {
                return false;
            }
        }
        return false;
    }

    private boolean hasPreviousNumber(String input, int currentPosition) {
        for (int i = currentPosition - 1; i >= 0; i--) {
            if (Character.isDigit(input.charAt(i))) {
                return true;
            } else if (!Character.isWhitespace(input.charAt(i))) {
                return false;
            }
        }
        return false;
    }

    private boolean hasPreviousBracket(String input, int currentPosition) {
        for (int i = currentPosition - 1; i >= 0; i--) {
            if (input.charAt(i) == ')') {
                return true;
            } else if (!Character.isWhitespace(input.charAt(i))) {
                return false;
            }
        }
        return false;
    }

    private void addNumberIfNeeded(StringBuilder currentToken, List<Token> tokens, int lastDigitStartingPosition) {
        if (!currentToken.isEmpty()) {
            tokens.add(createToken(currentToken.toString(), lastDigitStartingPosition));
            currentToken.setLength(0);
        }
    }
}
