import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Grammar {

    private String startingSymbol;
    private Set<String> nonTerminalSymbols;
    private Set<String> terminalSymbols;
    private Map<String, List<String>> productions;

    public Grammar(String startingSymbol, Map<String, List<String>> productions) {
        setupGrammar(startingSymbol, productions);
    }

    public Grammar(Map<String, List<String>> productions) {
        setupGrammar(null, productions);
    }

    private void setupGrammar(String startingSymbol, Map<String, List<String>> productions) {
        this.nonTerminalSymbols = new HashSet<>();
        this.terminalSymbols = new HashSet<>();
        this.productions = new HashMap<>();

        productions.forEach((key, value) -> this.productions.put(key, new ArrayList<>(value)));

        if(startingSymbol == null) {
            Optional<String> firstSymbol = productions.keySet().stream().findFirst();
            if(firstSymbol.isPresent()) {
                this.startingSymbol = firstSymbol.get();
            } else {
                throw new IllegalArgumentException("The productions map is empty.");
            }
        } else {
            this.startingSymbol = startingSymbol;
        }

        determineTerminalsAndNonTerminals();
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

    public static boolean isLeftHanded(String rule) {
        // Check if the rule is in the form xY
        if (rule.length() == 2) {
            char firstChar = rule.charAt(0);
            char secondChar = rule.charAt(1);

            return Character.isLowerCase(firstChar) && Character.isUpperCase(secondChar);
        }
        return false;
    }

    public static boolean isRightHanded(String rule) {
        // Check if the rule is in the form Yx
        if (rule.length() == 2) {
            char firstChar = rule.charAt(0);
            char secondChar = rule.charAt(1);

            return Character.isUpperCase(firstChar) && Character.isLowerCase(secondChar);
        }
        return false;
    }

    public void defineChomskyType() {
        boolean atLeastOneLeftHanded = false;
        boolean atLeastOneRightHanded = false;
        boolean atLeastOneAmbiguous = false;

        for(String s : productions.keySet()) {
            if((atLeastOneLeftHanded && atLeastOneRightHanded) || atLeastOneAmbiguous)
                break;
            List<String> entryList = productions.get(s);
            for(String entry : entryList) {
                if(isLeftHanded(entry)) {
                    atLeastOneLeftHanded = true;
                }
                if(isRightHanded(entry)) {
                    atLeastOneRightHanded = true;
                }
                if(!isLeftHanded(entry) && !isRightHanded(entry)) {
                    atLeastOneAmbiguous = true;
                }
                if((atLeastOneLeftHanded && atLeastOneRightHanded) || atLeastOneAmbiguous)
                    break;
            }
        }

        if (((atLeastOneLeftHanded && !atLeastOneRightHanded) || (!atLeastOneLeftHanded && atLeastOneRightHanded)) && !atLeastOneAmbiguous
                && productions.keySet().stream().allMatch(s -> s.length() == 1 && countNonTerminals(s) == 1)
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

    public void eliminateEpsilonProductions() {
        Set<String> epsilonSymbols = findSymbolsWithEpsilonProductions();

        Map<String, List<String>> newProductions = new HashMap<>();

        for(Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productionsList = entry.getValue();
            List<String> newProductionsList = new ArrayList<>();

            for(String production : productionsList) {
                if(production.isEmpty()) {
                    continue;
                }

                boolean added = false;
                for(String nullableSymbol : epsilonSymbols) {
                    int count = production.length() - production.replace(nullableSymbol, "").length();
                    if(count == 0 && !added) {
                        newProductionsList.add(production);
                        added = true;
                    } else {
                        String newProduction = production;
                        for(int i = 0; i < count; i++) {
                            newProduction = newProduction.replaceFirst(nullableSymbol, "");
                            if(!added) newProductionsList.add(production);
                            newProductionsList.add(newProduction);
                            added = true;
                        }
                    }
                }
            }

            newProductions.put(nonTerminal, newProductionsList);
        }

        this.productions = newProductions;
        determineTerminalsAndNonTerminals();
    }

    public Set<String> findSymbolsWithEpsilonProductions() {
        Set<String> epsilonSymbols = new HashSet<>();
        for(Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productionsList = entry.getValue();
            if(productionsList.contains("")) {
                epsilonSymbols.add(nonTerminal);
            }
        }
        return epsilonSymbols;
    }

    public void eliminateRenamingProductions() {
        Map<String, List<String>> newProductions = new HashMap<>(productions);

        while (countRenamingProductions(newProductions) > 0) {

            for (Map.Entry<String, List<String>> entry : newProductions.entrySet()) {
                String nonTerminal = entry.getKey();
                List<String> productionsList = entry.getValue();
                List<String> newProductionsList = new ArrayList<>();

                for (String production : productionsList) {
                    if (production.length() == 1 && Character.isUpperCase(production.charAt(0))) {
                        newProductionsList.addAll(productions.getOrDefault(production, Collections.emptyList()));
                    } else {
                        newProductionsList.add(production);
                    }
                }

                newProductions.put(nonTerminal, newProductionsList);
            }

        }

        this.productions = newProductions;
        determineTerminalsAndNonTerminals();
    }

    private int countRenamingProductions(Map<String, List<String>> productions) {
        int count = 0;
        for(Map.Entry<String, List<String>> entry : productions.entrySet()) {
            List<String> productionsList = entry.getValue();
            for(String production : productionsList) {
                if(production.length() == 1 && Character.isUpperCase(production.charAt(0))) {
                    count++;
                }
            }
        }
        return count;
    }

    public void eliminateInaccessibleSymbols() {
        Set<String> reachableSymbols = findReachableSymbols();
        productions.keySet().retainAll(reachableSymbols);
        determineTerminalsAndNonTerminals();
    }

    public Set<String> findReachableSymbols() {
        Map<String, Integer> reaches = new HashMap<>();
        reaches.put(startingSymbol, 1);

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            for(String production : entry.getValue()) {
                for(char c : production.toCharArray()) {
                    if(Character.isUpperCase(c)) {
                        reaches.put(String.valueOf(c), reaches.getOrDefault(String.valueOf(c), 0) + 1);
                    }
                }
            }
        }

        Set<String> reachable = new HashSet<>();

        for (Map.Entry<String, Integer> entry : reaches.entrySet()) {
            if(entry.getValue() > 0) {
                reachable.add(entry.getKey());
            }
        }

        return reachable;
    }

    public void eliminateNonProductiveSymbols() {
        Set<String> productiveSymbols = findProductiveSymbols();
        productions.keySet().retainAll(productiveSymbols);
        determineTerminalsAndNonTerminals();
    }

    public Set<String> findProductiveSymbols() {
        Set<String> productiveSymbols = new HashSet<>();
        Set<String> reachableSymbols = findReachableSymbols();

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productionsList = entry.getValue();

            if (reachableSymbols.contains(nonTerminal)) {
                for (String production : productionsList) {
                    boolean isProductive = true;
                    for (char c : production.toCharArray()) {
                        if (Character.isUpperCase(c) && !reachableSymbols.contains(String.valueOf(c))) {
                            isProductive = false;
                            break;
                        }
                    }
                    if (isProductive) {
                        productiveSymbols.add(nonTerminal);
                        break;
                    }
                }
            }
        }
        return productiveSymbols;
    }

    public void normalizeToChomskyForm() {
        eliminateEpsilonProductions();
        eliminateRenamingProductions();
        eliminateInaccessibleSymbols();
        eliminateNonProductiveSymbols();
        convertToChomskyForm();
    }

    private void convertToChomskyForm() {
        for(Map.Entry<String, List<String>> entry : new HashMap<>(productions).entrySet()) {
            boolean found = false;
            List<String> productionsList = entry.getValue();

            for(String production : productionsList) {
                for(char c : production.toCharArray()) {
                    if(startingSymbol.equals(String.valueOf(c))) {
                        productions.put(startingSymbol + "'", new ArrayList<>(List.of(startingSymbol)));
                        found = true;
                        break;
                    }
                }
                if(found) break;
            }
            if(found) break;
        }

        setupVariables();
        determineTerminalsAndNonTerminals();
    }

    private void setupVariables() {
        Map<String, String> terminalNonTerminals = new HashMap<>();
        Map<String, String> newNonTerminals = new HashMap<>();
        Map<String, String> nonTerminalMapping = new HashMap<>();
        Map<String, List<String>> newProductions = new HashMap<>(productions);
        int index = 0;

        for (Map.Entry<String, List<String>> entry : newProductions.entrySet()) {
            String fromState = entry.getKey();
            List<String> toStates = entry.getValue();
            List<String> newToStates = new ArrayList<>();
            for (String production : toStates) {
                if (production.length() > 2) {
                    String newNonTerminal = nonTerminalMapping.get(production);
                    if (newNonTerminal == null) {
                        newNonTerminal = generateNewVariable(newNonTerminals, index++, production);
                        newNonTerminals.put(newNonTerminal, production.substring(1));
                        nonTerminalMapping.put(production, newNonTerminal);
                    }
                    newToStates.add(production.charAt(0) + newNonTerminal);
                } else {
                    newToStates.add(production);
                }
            }
            newProductions.put(fromState, newToStates);
        }

        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
            newProductions.put(entry.getKey(), List.of(entry.getValue()));
        }

        for (Map.Entry<String, List<String>> entry : newProductions.entrySet()) {
            String fromState = entry.getKey();
            List<String> toStates = entry.getValue();
            List<String> newToStates = new ArrayList<>();
            for (String production : toStates) {
                if (production.length() == 2 && Character.isLowerCase(production.charAt(0)) && Character.isUpperCase(production.charAt(1))) {
                    String terminal = production.substring(0, 1);
                    String nonTerminal = terminalNonTerminals.getOrDefault(terminal, null);
                    if (nonTerminal == null) {
                        nonTerminal = generateNewVariable(newNonTerminals, index++, terminal);
                        terminalNonTerminals.put(terminal, nonTerminal);
                    }
                    newToStates.add(nonTerminal + production.charAt(1));
                } else {
                    newToStates.add(production);
                }
            }
            newProductions.put(fromState, newToStates);
        }

        for (Map.Entry<String, String> entry : newNonTerminals.entrySet()) {
            String nonTerminal = entry.getKey();
            String terminal = entry.getValue();
            newProductions.put(nonTerminal, List.of(terminal));
        }

        this.productions = newProductions;
    }

    private String generateNewVariable(Map<String, String> newNonTerminals, int index, String symbols) {
        String newNonTerminal = "X" + index;
        newNonTerminals.put(newNonTerminal, symbols);
        return newNonTerminal;
    }

    private void determineTerminalsAndNonTerminals() {
        determineNonTerminals();
        determineTerminals();
    }

    private void determineNonTerminals() {
        nonTerminalSymbols.clear();
        for (String nonTerminal : productions.keySet()) {
            nonTerminalSymbols.add(nonTerminal);
            List<String> productionList = productions.get(nonTerminal);
            for (String production : productionList) {
                for (char c : production.toCharArray()) {
                    if (Character.isUpperCase(c)) {
                        nonTerminalSymbols.add(String.valueOf(c));
                    }
                }
            }
        }
    }

    private void determineTerminals() {
        terminalSymbols.clear();
        for (String nonTerminal : productions.keySet()) {
            List<String> productionList = productions.get(nonTerminal);
            for (String production : productionList) {
                for (char c : production.toCharArray()) {
                    if (Character.isLowerCase(c)) {
                        terminalSymbols.add(String.valueOf(c));
                    }
                }
            }
        }
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
