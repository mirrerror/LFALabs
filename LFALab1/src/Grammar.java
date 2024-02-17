import java.util.HashMap;
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

    public FiniteAutomaton toFiniteAutomaton() {
        return new FiniteAutomaton(nonTerminalSymbols, terminalSymbols, buildTransitions(productions), startingSymbol);
    }

    private Map<String, Map<String, String>> buildTransitions(Map<String, List<String>> productions) {
        Map<String, Map<String, String>> transitions = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String state = entry.getKey();
            List<String> productionList = entry.getValue();

            Map<String, String> stateTransitions = new HashMap<>();

            for (String production : productionList) {
                String symbol = production.substring(0, 1); // Get the first character as the symbol

                String nextState = production.length() > 1 ? production.substring(1) : "accept";
                stateTransitions.put(symbol, nextState);
            }

            transitions.put(state, stateTransitions);
        }

        return transitions;
    }

}
