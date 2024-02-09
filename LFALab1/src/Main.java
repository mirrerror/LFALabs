public class Main {
    public static void main(String[] args) {
        Grammar grammar = new Grammar();
        for(int i = 0; i < 5; i++) System.out.println(grammar.generateString());
    }
}