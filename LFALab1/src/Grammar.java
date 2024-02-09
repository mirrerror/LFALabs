import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Grammar {

    public static final String START_SYMBOL = "S";
    public static final List<String> NON_TERMINALS = List.of("S", "P", "Q");
    public static final List<String> TERMINALS = List.of("a", "b", "c", "d", "e", "f");
    public static final Map<String, List<String>> PRODUCTIONS = new HashMap<>();

    static {
        PRODUCTIONS.put("S", List.of("aP"));
        PRODUCTIONS.put("P", List.of("bQ", "bP", "cP", "dQ", "e"));
        PRODUCTIONS.put("Q", List.of("eQ", "a"));
    }

    public String generateString() {
        String result = "";

        result += getRandomProduction(START_SYMBOL);

        boolean containsNonTerminal = true;

        while(containsNonTerminal) {
            containsNonTerminal = false;
            for(char entry : result.toCharArray()) {
                if(NON_TERMINALS.contains(String.valueOf(entry))) {
                    result = result.replaceFirst(String.valueOf(entry), getRandomProduction(String.valueOf(entry)));
                    containsNonTerminal = true;
                }
            }
        }

        return result;
    }

    private String getRandomProduction(String nonTerminal) {
        return PRODUCTIONS.get(nonTerminal).get(ThreadLocalRandom.current().nextInt(PRODUCTIONS.get(nonTerminal).size()));
    }

}
