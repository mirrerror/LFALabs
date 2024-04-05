import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the lab number (from 1 to 5): ");
        int labNumber = scanner.nextInt();

        Scanner labScanner = new Scanner(System.in);

        switch (labNumber) {
            case 1 -> testFirstLab(labScanner);
            case 2 -> testSecondLab();
            case 3 -> testThirdLab(labScanner);
            case 4 -> testFourthLab();
            case 5 -> testFifthLab();
            default -> System.err.println("Invalid lab number.");
        }
    }

    private static void testFirstLab(Scanner scanner) {
        String startingSymbol = "S";
        Set<String> nonTerminalSymbols = Set.of("S", "P", "Q");
        Set<String> terminalSymbols = Set.of("a", "b", "c", "d", "e", "f");
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aP", "bQ"),
                "P", List.of("bP", "cP", "dQ", "e"),
                "Q", List.of("eQ", "fQ", "a")
        );

        Grammar grammar = new Grammar(startingSymbol, nonTerminalSymbols, terminalSymbols, productions);
        FiniteAutomaton finiteAutomaton = grammar.toFiniteAutomaton();

        System.out.println("Generating 5 strings using the provided grammar:");
        for(int i = 1; i <= 5; i++) System.out.println(i + ". " + grammar.generateString());

        System.out.print("Enter a string to check if it belongs to the language: ");
        String input = scanner.nextLine();
        System.out.println("The string \"" + input + "\"" + (finiteAutomaton.stringBelongsToLanguage(input) ? " belongs " : " doesn't belong ") + "to the language.");
    }

    private static void testSecondLab() {
        FiniteAutomaton finiteAutomaton = new FiniteAutomaton(
                Set.of("q0", "q1", "q2", "q3"),
                Set.of("a", "c", "b"),
                Map.of(
                        "q0", Map.of("a", Set.of("q0", "q1")),
                        "q1", Map.of("c", Set.of("q1"), "b", Set.of("q2")),
                        "q2", Map.of("b", Set.of("q3")),
                        "q3", Map.of("a", Set.of("q1"))
                ),
                "q0",
                Set.of("q2")
        );

        Grammar grammar = finiteAutomaton.toGrammar();

        System.out.println("The grammar was converted to a finite automaton and then back to a grammar. The resulting grammar productions are: " + grammar.getProductions());

        grammar.defineChomskyType();

        System.out.println("The provided finite automaton is " + (finiteAutomaton.isDeterministic() ? "deterministic" : "non-deterministic") + ".");

        finiteAutomaton.visualize("The initial finite automaton");

        FiniteAutomaton convertedAutomaton = finiteAutomaton.convertToDeterministic();

        System.out.println("The finite automaton was converted to deterministic. The resulting transitions are: " + convertedAutomaton.getTransitions());
        convertedAutomaton.visualize("The converted finite automaton");
    }
    private static void testThirdLab(Scanner scanner) {
        ArithmeticLexer arithmeticLexer = new ArithmeticLexer();

        System.out.print("Enter an arithmetic expression: ");
        String input = scanner.nextLine();
        System.out.print("Should whitespaces be ignored? (y/n): ");
        String ignoreWhitespace = scanner.nextLine();

        while (!ignoreWhitespace.equalsIgnoreCase("y") && !ignoreWhitespace.equalsIgnoreCase("n")) {
            System.err.print("Invalid input. Please enter 'y' or 'n': ");
            ignoreWhitespace = scanner.nextLine();
        }

        arithmeticLexer.setIgnoreWhitespace(ignoreWhitespace.equalsIgnoreCase("y"));

        List<ArithmeticLexer.Token> tokens = arithmeticLexer.tokenize(input);

        List<String> answer = new ArrayList<>();

        for (ArithmeticLexer.Token token : tokens) {
            if (token.getType() == ArithmeticLexer.TokenType.ERROR) {
                System.err.println(token.getValue());
                return;
            }
            answer.add(token.toString());
        }

        if (answer.isEmpty()) {
            System.out.println("No tokens were found.");
        } else {
            System.out.println("The tokens are: ");
            int i = 1;
            for (String token : answer) {
                System.out.println(i + ". " + token);
                i++;
            }
            System.out.println("Result: " + arithmeticLexer.evaluate(tokens));
        }
    }

    private static void testFourthLab() {
        System.out.println("I used my right to skip this lab, because I had three bonus points.");
    }

    private static void testFifthLab() {
        String startingSymbol = "S";
        Set<String> nonTerminalSymbols = Set.of("S", "A", "B", "C", "D", "E");
        Set<String> terminalSymbols = Set.of("a", "b");
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aB", "AC"),
                "A", List.of("a", "ASC", "BC", "aD"),
                "B", List.of("b", "bS"),
                "C", List.of("", "BA"),
                "D", List.of("abC"),
                "E", List.of("aB")
        );

        Grammar grammar = new Grammar(startingSymbol, nonTerminalSymbols, terminalSymbols, productions);

        grammar.normalizeToChomskyForm();

        System.out.println("The initial grammar productions are: " + productions);
        System.out.println("The grammar was normalized to the Chomsky form. The resulting grammar productions are: " + grammar.getProductions());
    }
}