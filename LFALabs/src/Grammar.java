import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Grammar {

    private final String startingSymbol;
    private final Set<String> nonTerminalSymbols;
    private final Set<String> terminalSymbols;
    private final Map<String, List<String>> productions;

    public Grammar(String startingSymbol, Set<String> nonTerminals, Set<String> terminals, Map<String, List<String>> productions) {
        this.startingSymbol = startingSymbol;
        this.nonTerminalSymbols = new HashSet<>(nonTerminals);
        this.terminalSymbols = new HashSet<>(terminals);
        this.productions = new HashMap<>(productions);
    }

    public String generateString() {
        String result = getRandomProduction(startingSymbol);

        boolean containsNonTerminal = true;

        while (containsNonTerminal) {
            containsNonTerminal = false;
            for (char entry : result.toCharArray()) {
                String entryString = String.valueOf(entry);
                if (nonTerminalSymbols.contains(entryString)) {
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
        Map<String, Map<String, Set<String>>> transitions = buildTransitions();
        Set<String> acceptStates = Collections.singleton("");

        return new FiniteAutomaton(nonTerminalSymbols, terminalSymbols, transitions, startingSymbol, acceptStates);
    }

    private Map<String, Map<String, Set<String>>> buildTransitions() {
        Map<String, Map<String, Set<String>>> transitions = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String state = entry.getKey();
            List<String> productionList = entry.getValue();

            Map<String, Set<String>> stateTransitions = new HashMap<>();

            for (String production : productionList) {
                String symbol = production.substring(0, 1); // Get the first character as the symbol

                Set<String> nextStates = new HashSet<>();
                if (production.length() > 1) {
                    String nextState = production.substring(1);
                    nextStates.add(nextState);
                } else {
                    nextStates.add("");
                }

                stateTransitions.put(symbol, nextStates);
            }

            transitions.put(state, stateTransitions);
        }

        return transitions;
    }

    public void defineChomskyType() {
        if (productions.keySet().stream().allMatch(s -> s.length() == 1 && countNonTerminals(s) == 1)
                && productions.values().stream().allMatch(list -> list.stream().allMatch(l -> countTerminals(l) <= 1) && list.stream().allMatch(l -> countNonTerminals(l) <= 1))) {
            System.out.println("The grammar is of type 3 (regular).");
        } else if (productions.keySet().stream().allMatch(s -> s.length() == 1 && countNonTerminals(s) == 1)
                && productions.values().stream().allMatch(list -> (list.stream().allMatch(l -> countTerminals(l) >= 0) && list.stream().allMatch(l -> countNonTerminals(l) >= 0)))) {
            System.out.println("The grammar is of type 2 (context-free).");
        } else if (productions.keySet().stream().anyMatch(s -> s.length() > 1 && countTerminals(s) > 0 && countNonTerminals(s) > 0)) {
            System.out.println("The grammar is of type 0 (unrestricted).");
        } else {
            System.out.println("The grammar is of type 1 (context-sensitive).");
        }
    }

    private int countTerminals(List<String> list) {
        int count = 0;
        for (String s : list) {
            for (char c : s.toCharArray()) {
                if (terminalSymbols.contains(String.valueOf(c))) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countTerminals(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (terminalSymbols.contains(String.valueOf(c))) {
                count++;
            }
        }
        return count;
    }

    private int countNonTerminals(List<String> list) {
        int count = 0;
        for (String s : list) {
            for (char c : s.toCharArray()) {
                if (nonTerminalSymbols.contains(String.valueOf(c))) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countNonTerminals(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (nonTerminalSymbols.contains(String.valueOf(c))) {
                count++;
            }
        }
        return count;
    }

    public Map<String, List<String>> getProductions() {
        return productions;
    }

    public Set<String> getNonTerminalSymbols() {
        return nonTerminalSymbols;
    }

    public Set<String> getTerminalSymbols() {
        return terminalSymbols;
    }

    public String getStartingSymbol() {
        return startingSymbol;
    }
}
