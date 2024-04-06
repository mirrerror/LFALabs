import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Grammar {

    private final String startingSymbol;
    private final Set<String> nonTerminalSymbols;
    private final Set<String> terminalSymbols;
    private Map<String, List<String>> productions;

    public Grammar(String startingSymbol, Set<String> nonTerminals, Set<String> terminals, Map<String, List<String>> productions) {
        this.startingSymbol = startingSymbol;
        this.nonTerminalSymbols = new HashSet<>(nonTerminals);
        this.terminalSymbols = new HashSet<>(terminals);
        this.productions = new HashMap<>();
        productions.forEach((key, value) -> this.productions.put(key, new ArrayList<>(value)));
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

//    public Set<String> findNullableSymbols() {
//        Set<String> nullable = new HashSet<>();
//
//        for(Map.Entry<String, List<String>> entry : productions.entrySet()) {
//            String nonTerminal = entry.getKey();
//            List<String> productionsList = entry.getValue();
//
//            if(productionsList.contains("")) {
//                nullable.add(nonTerminal);
//            }
//        }
//
//        for(Map.Entry<String, List<String>> entry : productions.entrySet()) {
//            String nonTerminal = entry.getKey();
//            List<String> productionsList = entry.getValue();
//
//            for(String production : productionsList) {
//                boolean isNullable = false;
//                for(char c : production.toCharArray()) {
//                    if(nullable.contains(String.valueOf(c))) {
//                        isNullable = true;
//                        break;
//                    }
//                }
//                if(isNullable) {
//                    nullable.add(nonTerminal);
//                    break;
//                }
//            }
//        }
//
//        return nullable;
//    }

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
        nonTerminalSymbols.retainAll(reachableSymbols);
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
        nonTerminalSymbols.retainAll(productiveSymbols);
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

        this.productions = addVariableConditions();
    }

    private Map<String, List<String>> addVariableConditions() {
        int currentIndex = 0;
        Map<String, List<String>> newProductions = new HashMap<>();
        Set<String> addedVariables = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            List<String> productionsList = entry.getValue();
            List<String> newProductionsList = new ArrayList<>();

            for(String production : productionsList) {
                if((countNonTerminals(production) == 2 && countTerminals(production) == 0) || (countTerminals(production) == 1 && countNonTerminals(production) == 0) || nonTerminal.equals(startingSymbol + "'")) {
                    newProductionsList.add(production);
                    continue;
                }

                if(countNonTerminals(production) == 1 && countTerminals(production) == 1) {
                    char nonTerminalChar = Character.isUpperCase(production.charAt(0)) ? production.charAt(0) : production.charAt(1);
                    char terminalChar = Character.isLowerCase(production.charAt(0)) ? production.charAt(0) : production.charAt(1);

                    String neededVar = getNeededVariable(String.valueOf(terminalChar), newProductions, addedVariables);
                    if(neededVar == null) {
                        String var = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(var);
                        newProductionsList.add(var + nonTerminalChar);
                        newProductions.put(var, new ArrayList<>(List.of(String.valueOf(terminalChar))));
                    } else {
                        newProductionsList.add(neededVar + nonTerminalChar);
                    }
                }

                if(countNonTerminals(production) == 3) {
                    char nonTerminalChar1 = production.charAt(0);
                    char nonTerminalChar2 = production.charAt(1);
                    char nonTerminalChar3 = production.charAt(2);

                    String neededVar = getNeededVariable(nonTerminalChar1 + String.valueOf(nonTerminalChar2), newProductions, addedVariables);
                    if(neededVar == null) {
                        String newVar = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(newVar);
                        newProductionsList.add(newVar + nonTerminalChar3);
                        newProductions.put(newVar, new ArrayList<>(List.of(nonTerminalChar1 + String.valueOf(nonTerminalChar2))));
                    } else {
                        newProductionsList.add(neededVar + nonTerminalChar3);
                    }
                }

                if(countTerminals(production) == 2 && countNonTerminals(production) == 0) {
                    char terminalChar1 = production.charAt(0);
                    char terminalChar2 = production.charAt(1);

                    boolean added1 = false;
                    String newVar1 = getNeededVariable(String.valueOf(terminalChar1), newProductions, addedVariables);
                    if(newVar1 == null) {
                        newVar1 = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(newVar1);
                        added1 = true;
                    }

                    boolean added2 = false;
                    String newVar2 = getNeededVariable(String.valueOf(terminalChar2), newProductions, addedVariables);
                    if(newVar2 == null) {
                        newVar2 = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(newVar2);
                        added2 = true;
                    }

                    if(added1) {
                        addedVariables.add(newVar1);
                        newProductions.put(newVar1, new ArrayList<>(List.of(String.valueOf(terminalChar1))));
                    }
                    if(added2) {
                        addedVariables.add(newVar2);
                        newProductions.put(newVar2, new ArrayList<>(List.of(String.valueOf(terminalChar2))));
                    }

                    newProductionsList.add(newVar1 + newVar2);
                }

                if(countNonTerminals(production) == 1 && countTerminals(production) == 2) {
                    int usedNonTerminal;

                    if(Character.isUpperCase(production.charAt(0))) {
                        usedNonTerminal = 0;
                    } else if(Character.isUpperCase(production.charAt(1))) {
                        usedNonTerminal = 1;
                    } else {
                        usedNonTerminal = 2;
                    }

                    char nonTerminalChar = production.charAt(usedNonTerminal);
                    int usedTerminal1 = 0;
                    char terminalChar1 = 0;

                    for(int i = 0; i < 3; i++) {
                        if(i != usedNonTerminal) {
                            terminalChar1 = production.charAt(i);
                            usedTerminal1 = i;
                            break;
                        }
                    }

                    char terminalChar2 = 0;
                    for(int i = 0; i < 3; i++) {
                        if(i != usedTerminal1 && i != usedNonTerminal) {
                            terminalChar2 = production.charAt(i);
                            break;
                        }
                    }

                    boolean added1 = false;
                    String newVar1 = getNeededVariable(String.valueOf(terminalChar1), newProductions, addedVariables);
                    if(newVar1 == null) {
                        newVar1 = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(newVar1);
                        added1 = true;
                    }

                    boolean added2 = false;
                    String newVar2 = getNeededVariable(String.valueOf(terminalChar2), newProductions, addedVariables);
                    if(newVar2 == null) {
                        newVar2 = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(newVar2);
                        added2 = true;
                    }

                    boolean added3 = false;
                    String newVar3 = getNeededVariable(newVar1 + newVar2, newProductions, addedVariables);
                    if(newVar3 == null) {
                        newVar3 = "X" + currentIndex;
                        currentIndex++;
                        addedVariables.add(newVar3);
                        added3 = true;
                    }

                    if(added1) {
                        addedVariables.add(newVar1);
                        newProductions.put(newVar1, new ArrayList<>(List.of(String.valueOf(terminalChar1))));
                    }
                    if(added2) {
                        addedVariables.add(newVar2);
                        newProductions.put(newVar2, new ArrayList<>(List.of(String.valueOf(terminalChar2))));
                    }
                    if(added3) {
                        addedVariables.add(newVar3);
                        newProductions.put(newVar3, new ArrayList<>(List.of(newVar1 + newVar2)));
                    }

                    newProductionsList.add(newVar3 + nonTerminalChar);
                }
            }

            newProductions.put(nonTerminal, newProductionsList);
        }
        return newProductions;
    }

    private String getNeededVariable(String s, Map<String, List<String>> productions, Set<String> addedVariables) {
        for(Map.Entry<String, List<String>> entry : productions.entrySet()) {
            List<String> productionsList = entry.getValue();
            for(String production : productionsList) {
                if(production.contains(s) && addedVariables.contains(entry.getKey())) {
                    return entry.getKey();
                }
            }
        }
        return null;
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
