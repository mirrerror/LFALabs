package lexer;

import java.util.List;

public class ASTBuilder {

    private List<Token> tokens;
    private ASTNode root;

    public ASTBuilder(List<Token> tokens) {
        this.tokens = tokens;
        this.root = null;
    }

    public void buildAST() {
        root = buildASTRecursive(0, tokens.size() - 1);
    }

    private ASTNode buildASTRecursive(int start, int end) {
        if (start > end) {
            return null;
        }

        // Find the operator with the lowest precedence
        int lowestPrecedence = Integer.MAX_VALUE;
        int index = -1;
        for (int i = start; i <= end; i++) {
            Token token = tokens.get(i);
            if (token.getType() == TokenType.OPERATOR) {
                int precedence = getPrecedence(token);
                if (precedence <= lowestPrecedence) {
                    lowestPrecedence = precedence;
                    index = i;
                }
            }
        }

        if (index == -1) {
            // If no operator found, it must be a single token (operand)
            return new ASTNode(tokens.get(start).getType(), tokens.get(start).getValue());
        }

        // Recursively build left and right subtrees
        ASTNode node = new ASTNode(tokens.get(index).getType(), tokens.get(index).getValue());
        node.setLeftChild(buildASTRecursive(start, index - 1));
        node.setRightChild(buildASTRecursive(index + 1, end));
        return node;
    }

    private int getPrecedence(Token token) {
        return switch (token.getValue()) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> 0;
        };
    }

    public void printAST() {
        if(root == null) {
            buildAST();
        }

        if (root != null) {
            printASTRecursive(root, 0);
        }
    }

    private void printASTRecursive(ASTNode node, int indentLevel) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("  ");
        }
        System.out.println(node);
        // Print left subtree
        printASTRecursive(node.getLeftChild(), indentLevel + 1);
        // Print right subtree
        printASTRecursive(node.getRightChild(), indentLevel + 1);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode getRoot() {
        return root;
    }
}
