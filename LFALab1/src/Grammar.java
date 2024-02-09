import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Grammar {

    private String startingSymbol;
    private List<String> nonTerminalSymbols;
    private List<String> terminalSymbols;
    private Map<String, List<String>> productions;

    public Grammar(String startingSymbol, List<String> nonTerminals, List<String> terminals, Map<String, List<String>> productions) {
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

    public String getStartingSymbol() {
        return startingSymbol;
    }

    public void setStartingSymbol(String startingSymbol) {
        this.startingSymbol = startingSymbol;
    }

    public List<String> getNonTerminalSymbols() {
        return nonTerminalSymbols;
    }

    public void setNonTerminalSymbols(List<String> nonTerminalSymbols) {
        this.nonTerminalSymbols = nonTerminalSymbols;
    }

    public List<String> getTerminalSymbols() {
        return terminalSymbols;
    }

    public void setTerminalSymbols(List<String> terminalSymbols) {
        this.terminalSymbols = terminalSymbols;
    }

    public Map<String, List<String>> getProductions() {
        return productions;
    }

    public void setProductions(Map<String, List<String>> productions) {
        this.productions = productions;
    }
}
