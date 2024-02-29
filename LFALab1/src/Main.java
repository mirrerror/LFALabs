import java.util.*;

public class Main {
    public static void main(String[] args) {
//        testFirstLab(new Scanner(System.in));
        testSecondLab();
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

        Grammar grammar = finiteAutomaton.toGrammar(true);

        System.out.println("The grammar was converted to a finite automaton and then back to a grammar. The resulting grammar productions are: " + grammar.getProductions());

        grammar.defineChomskyType();

        System.out.println("The provided finite automaton is " + (finiteAutomaton.isDeterministic() ? "deterministic" : "non-deterministic") + ".");

        finiteAutomaton.visualize();
    }
}