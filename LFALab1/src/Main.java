import java.util.*;

public class Main {
    public static void main(String[] args) {
        String startingSymbol = "S";
        Set<String> nonTerminalSymbols = Set.of("S", "P", "Q");
        Set<String> terminalSymbols = Set.of("a", "b", "c", "d", "e", "f");
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aP"),
                "P", List.of("bQ", "bP", "cP", "dQ", "e"),
                "Q", List.of("eQ", "a")
        );


        Grammar grammar = new Grammar(startingSymbol, nonTerminalSymbols, terminalSymbols, productions);
        for(int i = 0; i < 5; i++) System.out.println(grammar.generateString());

        FiniteAutomaton finiteAutomaton = grammar.toFiniteAutomaton(buildTransitions());
        System.out.println(finiteAutomaton.stringBelongsToLanguage("ada"));
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