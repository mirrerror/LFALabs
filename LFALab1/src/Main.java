import java.util.*;

public class Main {
    public static void main(String[] args) {
        String startingSymbol = "S";
        Set<String> nonTerminalSymbols = Set.of("S", "P", "Q");
        Set<String> terminalSymbols = Set.of("a", "b", "c", "d", "e", "f");
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aP", "bQ"),
                "P", List.of("bP", "cP", "dQ", "e"),
                "Q", List.of("eQ", "fQ", "a")
        );

        Grammar grammar = new Grammar(startingSymbol, nonTerminalSymbols, terminalSymbols, productions);
        FiniteAutomaton finiteAutomaton = grammar.toFiniteAutomaton(buildTransitions());
        Scanner scanner = new Scanner(System.in);

        System.out.println("Generating 5 strings using the provided grammar:");
        for(int i = 0; i < 5; i++) System.out.println((i + 1) + ". " + grammar.generateString());

        System.out.print("Enter a string to check if it belongs to the language: ");
        String input = scanner.nextLine();
        System.out.println("The string \"" + input + "\"" + (finiteAutomaton.stringBelongsToLanguage(input) ? " belongs " : " doesn't belong ") + "to the language.");
    }

    private static Map<String, Map<String, String>> buildTransitions() {
        Map<String, Map<String, String>> transitions = new HashMap<>();

        Map<String, String> sTransitions = new HashMap<>();
        sTransitions.put("a", "P");
        sTransitions.put("b", "Q");
        transitions.put("S", sTransitions);

        Map<String, String> pTransitions = new HashMap<>();
        pTransitions.put("b", "P");
        pTransitions.put("c", "P");
        pTransitions.put("d", "Q");
        pTransitions.put("e", "accept");
        transitions.put("P", pTransitions);

        Map<String, String> qTransitions = new HashMap<>();
        qTransitions.put("a", "accept");
        qTransitions.put("e", "Q");
        qTransitions.put("f", "Q");
        transitions.put("Q", qTransitions);

        return transitions;
    }
}