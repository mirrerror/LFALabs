import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Grammar {

    private final String startingSymbol;
    private final Set<String> nonTerminalSymbols;
    private final Set<String> terminalSymbols;
    private final Map<String, List<String>> productions;

    public Grammar(String startingSymbol, Set<String> nonTerminals, Set<String> terminals, Map<String, List<String>> productions) {
        this.startingSymbol = startingSymbol;
        this.nonTerminalSymbols = nonTerminals;
        this.terminalSymbols = terminals;
        this.productions = productions;
    }

    public String generateString() {
        String result = getRandomProduction(startingSymbol);

        boolean containsNonTerminal = true;

        while(containsNonTerminal) {
            containsNonTerminal = false;
            for(char entry : result.toCharArray()) {
                String entryString = String.valueOf(entry);
                if(nonTerminalSymbols.contains(entryString)) {
                    result = result.replaceFirst(entryString, getRandomProduction(entryString));
                    containsNonTerminal = true;
                }
            }
        }

        return result;
    }

    private String getRandomProduction(String nonTerminal) {
        return productions.get(nonTerminal).get(ThreadLocalRandom.current().nextInt(productions.get(nonTerminal).size()));
    }

    public FiniteAutomaton toFiniteAutomaton(Map<String, Map<String, String>> transitions) {
        return new FiniteAutomaton(nonTerminalSymbols, terminalSymbols, transitions, startingSymbol);
    }

}
