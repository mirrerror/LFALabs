import java.util.*;

public class FiniteAutomaton {

    private final Set<String> states;
    private final Set<String> alphabet;
    private final Map<String, Map<String, Set<String>>> transitions;
    private final String startState;
    private final Set<String> acceptStates;

    public FiniteAutomaton(Set<String> states, Set<String> alphabet, Map<String, Map<String, Set<String>>> transitions, String startState, Set<String> acceptStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.startState = startState;
        this.acceptStates = acceptStates;
    }

    public boolean stringBelongsToLanguage(String input) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);

        for (char symbol : input.toCharArray()) {
            String symbolStr = String.valueOf(symbol);

            if (!alphabet.contains(symbolStr)) {
                // Invalid symbol
                return false;
            }

            Set<String> nextStates = new HashSet<>();

            for (String currentState : currentStates) {
                if (transitions.containsKey(currentState) && transitions.get(currentState).containsKey(symbolStr)) {
                    nextStates.addAll(transitions.get(currentState).get(symbolStr));
                }
            }

            if (nextStates.isEmpty()) {
                // No transition for the current symbol and current states
                return false;
            }

            currentStates = nextStates;
        }

        // Check if any of the current states is an accept state
        for (String currentState : currentStates) {
            if (acceptStates.contains(currentState)) {
                return true;
            }
        }

        return false;
    }

    private Map<String, List<String>> buildProductions() {
        Map<String, List<String>> productions = new HashMap<>();

        for (String state : transitions.keySet()) {
            Map<String, Set<String>> stateTransitions = transitions.get(state);

            for (String symbol : stateTransitions.keySet()) {
                Set<String> nextStates = stateTransitions.get(symbol);

                if (!productions.containsKey(state)) {
                    productions.put(state, new ArrayList<>());
                }

                List<String> productionList = productions.get(state);

                if (nextStates.isEmpty()) {
                    // If there are no next states, add the symbol as it is
                    productionList.add(symbol);
                } else {
                    // Add a production for each next state
                    for (String nextState : nextStates) {
                        productionList.add(symbol + nextState);
                    }
                }
            }
        }

        return productions;
    }

    public Grammar toGrammar() {
        return new Grammar(startState, states, alphabet, buildProductions());
    }

    public boolean isDeterministic() {
        for (String state : transitions.keySet()) {
            Map<String, Set<String>> stateTransitions = transitions.get(state);

            for (String symbol : stateTransitions.keySet()) {
                Set<String> nextStates = stateTransitions.get(symbol);

                if (nextStates.size() > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    public FiniteAutomatonVisualizer visualize() {
        return new FiniteAutomatonVisualizer(this);
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public Map<String, Map<String, Set<String>>> getTransitions() {
        return transitions;
    }

    public String getStartState() {
        return startState;
    }

    public Set<String> getAcceptStates() {
        return acceptStates;
    }
}