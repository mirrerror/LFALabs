import java.util.*;

public class FiniteAutomaton {

    private final Set<String> states;
    private final Set<String> alphabet;
    private final Map<String, Map<String, Set<String>>> transitions;
    private final String startState;
    private final Set<String> acceptStates;

    public FiniteAutomaton(Set<String> states, Set<String> alphabet, Map<String, Map<String, Set<String>>> transitions, String startState, Set<String> acceptStates) {
        this.states = new HashSet<>(states);
        this.alphabet = new HashSet<>(alphabet);
        this.transitions = new HashMap<>(transitions);
        this.startState = startState;
        this.acceptStates = new HashSet<>(acceptStates);
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

    public static String mapStateToNonTerminal(String state) {
        int stateNumber;

        try {
            stateNumber = Integer.parseInt(state.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("State must be in the format qN, where N is a number");
        }

        if (stateNumber < 0) {
            throw new IllegalArgumentException("State number must be non-negative");
        }

        char letter = (char) ('A' + stateNumber);

        return String.valueOf(letter);
    }

    private Map<String, List<String>> buildProductions(boolean mapStatesToNonTerminals) {
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
                        productionList.add(symbol + ((mapStatesToNonTerminals) ? mapStateToNonTerminal(nextState) : nextState));
                    }
                }
            }
        }

        if(mapStatesToNonTerminals) {
            Map<String, List<String>> newProductions = new HashMap<>();
            for (String state : productions.keySet()) {
                newProductions.put(mapStateToNonTerminal(state), productions.get(state));
            }
            return newProductions;
        }

        return productions;
    }

    public Grammar toGrammar(boolean mapStatesToNonTerminals) {
        return new Grammar(startState, states, alphabet, buildProductions(mapStatesToNonTerminals));
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

    public FiniteAutomaton convertToDeterministic() {
        if (isDeterministic()) {
            return this;
        }

        Set<String> newStates = new HashSet<>();
        Map<String, Map<String, Set<String>>> newTransitions = new HashMap<>();
        Set<String> newAcceptStates = new HashSet<>();

        Queue<Set<String>> stateQueue = new LinkedList<>();
        stateQueue.add(Set.of(startState));

        while (!stateQueue.isEmpty()) {
            Set<String> currentState = stateQueue.poll();
            String stateName = String.join("", currentState);

            if (newStates.contains(stateName)) {
                continue;
            }

            newStates.add(stateName);

            for (String state : currentState) {
                if (acceptStates.contains(state)) {
                    newAcceptStates.add(stateName);
                    break;
                }
            }

            Map<String, Set<String>> currentStateTransitions = new HashMap<>();

            for (String symbol : alphabet) {
               for (String state : currentState) {
                   if (transitions.containsKey(state) && transitions.get(state).containsKey(symbol)) {
                       Set<String> nextStates = transitions.get(state).get(symbol);
                       currentStateTransitions.put(symbol, Set.of(String.join("", nextStates)));
                       stateQueue.add(nextStates);
                   }
               }
            }

            newTransitions.put(stateName, currentStateTransitions);
        }

        return new FiniteAutomaton(newStates, alphabet, newTransitions, startState, newAcceptStates);
    }

    public void visualize(String title) {
        new FiniteAutomatonVisualizer(this, title);
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