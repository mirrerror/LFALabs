package lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// This class is responsible for lexing arithmetic expressions, breaking them down into tokens.
public class ArithmeticLexer {
    private boolean ignoreWhitespace = false;    // Flag to determine if whitespace should be ignored.

    // Default constructor.
    public ArithmeticLexer() {}

    // Constructor with an option to set whether to ignore whitespace.
    public ArithmeticLexer(boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
    }

    // Setter for ignoreWhitespace flag.
    public void setIgnoreWhitespace(boolean ignoreWhitespace) {
        this.ignoreWhitespace = ignoreWhitespace;
    }

    // Getter for ignoreWhitespace flag.
    public boolean getIgnoreWhitespace() {
        return ignoreWhitespace;
    }

    // Method to tokenize the input string and return a list of tokens.
    public List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();  // List to store tokens.
        StringBuilder currentToken = new StringBuilder(); // StringBuilder to build the current token.
        int currentPosition = 0;    // Current position in the input string.
        int leftParenCount = 0;     // Count of left parentheses.
        int rightParenCount = 0;    // Count of right parentheses.
        int lastDigitStartingPosition = 0; // Starting position of the last digit encountered.

        // Iterate through each character in the input string.
        for (char c : input.toCharArray()) {
            // Check if the character is a whitespace.
            if (Character.isWhitespace(c)) {
                // If whitespace should be ignored, skip to the next character.
                if (ignoreWhitespace) {
                    currentPosition++;
                    continue;
                }
                // Add the current number token if any, and add the whitespace token.
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                tokens.add(new Token(TokenType.WHITESPACE, Character.toString(c), currentPosition));
            }
            // Check if the character is an operator.
            else if (isOperator(c)) {
                // Check if the operator requires a number before and after it.
                if (requiresNumberBeforeAndAfter(c)) {
                    // Check if there is a number or a bracket before the operator.
                    if (!hasPreviousNumber(input, currentPosition) && !hasPreviousBracket(input, currentPosition)) {
                        return invalidTokenError("Expected number or bracket before operator", currentPosition);
                    }
                    // Check if there is a number or a bracket after the operator.
                    if (!hasNextNumber(input, currentPosition) && !hasNextBracket(input, currentPosition)) {
                        return invalidTokenError("Expected number or bracket after operator", currentPosition);
                    }
                }

                // Add the current number token if any, and add the operator token.
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                tokens.add(new Token(TokenType.OPERATOR, Character.toString(c), currentPosition));
            }
            // Check if the character is a digit.
            else if (Character.isDigit(c)) {
                // If the current token is empty, update the starting position of the last digit encountered.
                if(currentToken.isEmpty()) {
                    lastDigitStartingPosition = currentPosition;
                }
                // Append the digit to the current token.
                currentToken.append(c);
            }
            // Check if the character is a left parenthesis.
            else if (c == '(') {
                // Add the current number token if any, and add the left parenthesis token.
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                tokens.add(new Token(TokenType.LEFT_PAREN, Character.toString(c), currentPosition));
                leftParenCount++;
            }
            // Check if the character is a right parenthesis.
            else if (c == ')') {
                // Add the current number token if any, and add the right parenthesis token.
                addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);
                rightParenCount++;
                // Check if there are more right parentheses than left parentheses.
                if (rightParenCount > leftParenCount) {
                    return invalidTokenError(Character.toString(c), currentPosition);
                }
                tokens.add(new Token(TokenType.RIGHT_PAREN, Character.toString(c), currentPosition));
            }
            // If none of the above conditions are met, return an error token.
            else {
                return invalidTokenError(Character.toString(c), currentPosition);
            }
            currentPosition++; // Move to the next position in the input string.
        }

        // Check if the number of left parentheses matches the number of right parentheses.
        if (leftParenCount != rightParenCount) {
            return invalidTokenError("Mismatched parentheses", input.length());
        }

        // Add the current number token if any.
        addNumberIfNeeded(currentToken, tokens, lastDigitStartingPosition);

        return tokens; // Return the list of tokens.
    }

    // Method to create an error token with the provided message and position.
    private List<Token> invalidTokenError(String token, int position) {
        List<Token> errorTokens = new ArrayList<>();
        // Add an error token indicating the invalid expression and its position.
        errorTokens.add(new Token(TokenType.ERROR, "Invalid expression: " + token + " at position " + position + ".", position));
        return errorTokens; // Return the list containing the error token.
    }

    // Method to create a token from the provided string and position.
    private Token createToken(String tokenString, int position) {
        // Check if the token string represents a numeric value.
        if (tokenString.matches("\\d+")) {
            // If yes, create and return a token with NUMBER type.
            return new Token(TokenType.NUMBER, tokenString, position);
        } else {
            // If not, create and return an error token.
            return new Token(TokenType.ERROR, "Invalid expression: " + tokenString + " at position " + position + ".", position);
        }
    }

    // Method to check if a character is an operator.
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // Method to check if an operator requires a number before and after it.
    private static boolean requiresNumberBeforeAndAfter(char c) {
        return isOperator(c);
    }

    // Method to check if there is a number after the current position.
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

    // Method to check if there is a bracket after the current position.
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

    // Method to check if there is a number before the current position.
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

    // Method to check if there is a bracket before the current position.
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

    // Method to add the current number token to the list if it's not empty.
    private void addNumberIfNeeded(StringBuilder currentToken, List<Token> tokens, int lastDigitStartingPosition) {
        if (!currentToken.isEmpty()) {
            tokens.add(createToken(currentToken.toString(), lastDigitStartingPosition));
            currentToken.setLength(0); // Clear the current token StringBuilder.
        }
    }

    // Method to evaluate the arithmetic expression.
    public int evaluate(List<Token> tokens) {
        Stack<Integer> operandStack = new Stack<>();  // Stack to store operands.
        Stack<Character> operatorStack = new Stack<>();  // Stack to store operators.

        for (Token token : tokens) {
            switch (token.getType()) {
                case NUMBER:
                    operandStack.push(Integer.parseInt(token.getValue()));
                    break;
                case OPERATOR:
                    while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token.getValue().charAt(0))) {
                        evaluateTop(operandStack, operatorStack);
                    }
                    operatorStack.push(token.getValue().charAt(0));
                    break;
                case LEFT_PAREN:
                    operatorStack.push('(');
                    break;
                case RIGHT_PAREN:
                    while (operatorStack.peek() != '(') {
                        evaluateTop(operandStack, operatorStack);
                    }
                    operatorStack.pop(); // Pop the '('
                    break;
                default:
                    // Do nothing for other token types.
                    break;
            }
        }

        while (!operatorStack.isEmpty()) {
            evaluateTop(operandStack, operatorStack);
        }

        return operandStack.pop(); // Result will be on top of the operand stack.
    }

    // Method to evaluate the top of the stacks.
    private void evaluateTop(Stack<Integer> operandStack, Stack<Character> operatorStack) {
        char operator = operatorStack.pop();
        int operand2 = operandStack.pop();
        int operand1 = operandStack.pop();
        int result = performOperation(operand1, operand2, operator);
        operandStack.push(result);
    }

    // Method to perform arithmetic operation.
    private int performOperation(int operand1, int operand2, char operator) {
        return switch (operator) {
            case '+' -> operand1 + operand2;
            case '-' -> operand1 - operand2;
            case '*' -> operand1 * operand2;
            case '/' -> {
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield operand1 / operand2;
            }
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

    // Method to get precedence of operators.
    private int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }
}