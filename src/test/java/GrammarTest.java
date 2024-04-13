import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GrammarTest {

    @Test
    void eliminateEpsilonProductions() {
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aB", "AC"),
                "A", List.of("a", "ASC", "BC", "aD"),
                "B", List.of("b", "bS"),
                "C", List.of("", "BA"),
                "D", List.of("abC"),
                "E", List.of("aB")
        );

        Grammar grammar = new Grammar(productions);
        grammar.eliminateEpsilonProductions();

        assertFalse(grammar.getProductions().get("C").contains(""));
    }

    @Test
    void eliminateRenamingProductions() {
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aB", "AC"),
                "A", List.of("a", "ASC", "BC", "aD"),
                "B", List.of("b", "bS"),
                "C", List.of("", "BA"),
                "D", List.of("abC"),
                "E", List.of("aB")
        );

        Grammar grammar = new Grammar(productions);
        grammar.eliminateRenamingProductions();

        assertEquals(productions, grammar.getProductions());
    }

    @Test
    void eliminateInaccessibleSymbols() {
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aB", "AC"),
                "A", List.of("a", "ASC", "BC", "aD"),
                "B", List.of("b", "bS"),
                "C", List.of("", "BA"),
                "D", List.of("abC"),
                "E", List.of("aB")
        );

        Grammar grammar = new Grammar(productions);
        grammar.eliminateInaccessibleSymbols();

        assertFalse(grammar.getProductions().containsKey("E"));
    }

    @Test
    void eliminateNonProductiveSymbols() {
        Map<String, List<String>> productions = new HashMap<>(Map.of(
                "A", List.of("a", "ASC", "BC", "aD"),
                "B", List.of("b", "bS"),
                "S", List.of("aB", "AC"),
                "C", List.of("", "BA"),
                "D", List.of("abC"),
                "E", List.of("aB")
        ));

        Grammar grammar = new Grammar(productions);
        grammar.eliminateNonProductiveSymbols();

        productions.remove("E");

        assertEquals(productions, grammar.getProductions());
    }

    @Test
    void normalizeToChomskyForm() {
        Map<String, List<String>> productions = Map.of(
                "S", List.of("aB", "AC"),
                "A", List.of("a", "ASC", "BC", "aD"),
                "B", List.of("b", "bS"),
                "C", List.of("", "BA"),
                "D", List.of("abC"),
                "E", List.of("aB")
        );

        Grammar grammar = new Grammar(productions);
        grammar.normalizeToChomskyForm();

        assertTrue(countSymbolsWithVariablesAsOneSymbol(Set.of("X0", "X1", "X2", "X3"), grammar));
    }

    private boolean countSymbolsWithVariablesAsOneSymbol(Set<String> variables, Grammar grammar) {
        return grammar.getProductions().values().stream().allMatch(productions ->
                productions.stream().allMatch(production -> countSymbols(variables, production) <= 2)
        );
    }

    private int countSymbols(Set<String> variables, String production) {
        int count = 0;
        int i = 0;
        while (i < production.length()) {
            boolean isVariable = false;
            for (String variable : variables) {
                if (production.startsWith(variable, i)) {
                    count++;
                    i += variable.length();
                    isVariable = true;
                    break;
                }
            }
            if (!isVariable) {
                count++;
                i++;
            }
        }
        return count;
    }

}