import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String startingSymbol = "S";
        List<String> nonTerminalSymbols = List.of("S", "P", "Q");
        List<String> terminalSymbols = List.of("a", "b", "c", "d", "e", "f");
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aP"),
                "P", List.of("bQ", "bP", "cP", "dQ", "e"),
                "Q", List.of("eQ", "a")
        );


        Grammar grammar = new Grammar(startingSymbol, nonTerminalSymbols, terminalSymbols, productions);
        for(int i = 0; i < 5; i++) System.out.println(grammar.generateString());
    }
}