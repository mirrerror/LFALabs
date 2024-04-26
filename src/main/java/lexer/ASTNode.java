package lexer;

public class ASTNode {
    private final TokenType type;
    private final String value;
    private ASTNode leftChild;
    private ASTNode rightChild;

    public ASTNode(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public ASTNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(ASTNode leftChild) {
        this.leftChild = leftChild;
    }

    public ASTNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(ASTNode rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public String toString() {
        return "[" + type + ": " + value + "]";
    }
}
