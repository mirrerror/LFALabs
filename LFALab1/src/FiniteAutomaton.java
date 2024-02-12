import java.util.Map;
import java.util.Set;

public class FiniteAutomaton {

    private final Set<String> states;
    private final Set<String> alphabet;
    private final Map<String, Map<String, String>> transitions;
    private final String startState;

    public FiniteAutomaton(Set<String> states, Set<String> alphabet, Map<String, Map<String, String>> transitions, String startState) {
        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.startState = startState;
    }

    public boolean stringBelongsToLanguage(String input) {
        String currentState = startState;

        for (char symbol : input.toCharArray()) {
            String symbolStr = String.valueOf(symbol);

            if (!alphabet.contains(symbolStr)) {
//                System.out.println("Invalid symbol: " + symbolStr);
                return false;
            }

            if (transitions.containsKey(currentState) && transitions.get(currentState).containsKey(symbolStr)) {
                currentState = transitions.get(currentState).get(symbolStr);
            } else {
//                System.out.println("No transition for state " + currentState + " and symbol " + symbolStr);
                return false;
            }
        }

        return currentState.equals("accept");
    }

}
