import java.util.*;

public class Converter {
    // Subset construction: setiap state DFA adalah himpunan state NFA.
    static DFA convert(NFA nfa) {
        DFA dfa = new DFA();
        nfa.alphabet.forEach(dfa::addSymbol);

        Set<String> startSet = nfa.epsilonClosure(Collections.singleton(nfa.start));
        System.out.println("State awal DFA = ε-closure({" + nfa.start + "}) = " + NFA.format(startSet));
        dfa.start = NFA.format(startSet);

        Map<Set<String>, String> names = new LinkedHashMap<>();
        Deque<Set<String>> queue = new ArrayDeque<>();
        names.put(startSet, dfa.start);
        queue.add(startSet);

        while (!queue.isEmpty()) {
            Set<String> current = queue.poll();
            String name = names.get(current);
            dfa.addState(name);
            if (current.stream().anyMatch(nfa.finals::contains)) {
                dfa.finals.add(name);
            }
            System.out.println("\nProses " + name + ":");
            for (char c : nfa.alphabet) {
                Set<String> moved = nfa.move(current, c);
                Set<String> target = nfa.epsilonClosure(moved);
                boolean isNew = !names.containsKey(target);
                if (isNew) {
                    names.put(target, NFA.format(target));
                    queue.add(target);
                }
                System.out.println("  δ(" + name + ", " + c + ") = ε-closure(" + NFA.format(moved) + ") = "
                        + NFA.format(target) + (isNew ? "  [state baru]" : ""));
                dfa.addTransition(name, c, names.get(target));
            }
        }
        return dfa;
    }
}
