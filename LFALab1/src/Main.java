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
        FiniteAutomaton finiteAutomaton = grammar.toFiniteAutomaton();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Generating 5 strings using the provided grammar:");
        for(int i = 0; i < 5; i++) System.out.println((i + 1) + ". " + grammar.generateString());

        System.out.print("Enter a string to check if it belongs to the language: ");
        String input = scanner.nextLine();
        System.out.println("The string \"" + input + "\"" + (finiteAutomaton.stringBelongsToLanguage(input) ? " belongs " : " doesn't belong ") + "to the language.");
    }
}