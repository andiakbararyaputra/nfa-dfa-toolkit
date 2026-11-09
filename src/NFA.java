import java.util.*;

public class NFA {
    public static final char EPSILON = 'ε';

    final List<String> states = new ArrayList<>();
    final List<Character> alphabet = new ArrayList<>();
    final Map<String, Map<Character, Set<String>>> delta = new HashMap<>();
    final Set<String> finals = new HashSet<>();
    String start;

    void addState(String state) {
        if (!states.contains(state)) {
            states.add(state);
            delta.put(state, new HashMap<>());
        }
    }

    void addSymbol(char symbol) {
        if (!alphabet.contains(symbol)) {
            alphabet.add(symbol);
        }
    }

    void addTransition(String from, char symbol, String to) {
        delta.get(from).computeIfAbsent(symbol, k -> new TreeSet<>()).add(to);
    }

    Set<String> next(String state, char symbol) {
        return delta.getOrDefault(state, Collections.emptyMap()).getOrDefault(symbol, Collections.emptySet());
    }

    Set<String> epsilonClosure(Set<String> set) {
        Set<String> closure = new TreeSet<>(set);
        Deque<String> stack = new ArrayDeque<>(set);
        while (!stack.isEmpty()) {
            for (String t : next(stack.pop(), EPSILON)) {
                if (closure.add(t)) {
                    stack.push(t);
                }
            }
        }
        return closure;
    }

    Set<String> move(Set<String> set, char symbol) {
        Set<String> result = new TreeSet<>();
        for (String s : set) {
            result.addAll(next(s, symbol));
        }
        return result;
    }

    boolean accepts(String input) {
        Set<String> current = epsilonClosure(Collections.singleton(start));
        System.out.println("  ε-closure({" + start + "}) = " + format(current));
        for (char c : input.toCharArray()) {
            if (!alphabet.contains(c)) {
                System.out.println("  Simbol '" + c + "' tidak ada di alfabet.");
                return false;
            }
            current = epsilonClosure(move(current, c));
            System.out.println("  baca '" + c + "' → " + format(current));
        }
        return current.stream().anyMatch(finals::contains);
    }

    void printTable() {
        List<Character> columns = new ArrayList<>(alphabet);
        columns.add(EPSILON);
        List<String[]> rows = new ArrayList<>();
        String[] header = new String[columns.size() + 1];
        header[0] = "δ";
        for (int i = 0; i < columns.size(); i++) {
            header[i + 1] = String.valueOf(columns.get(i));
        }
        rows.add(header);
        for (String s : states) {
            String[] row = new String[columns.size() + 1];
            row[0] = Table.label(s, start, finals);
            for (int i = 0; i < columns.size(); i++) {
                row[i + 1] = format(next(s, columns.get(i)));
            }
            rows.add(row);
        }
        Table.print(rows);
    }

    static String format(Set<String> set) {
        return set.isEmpty() ? "∅" : "{" + String.join(",", set) + "}";
    }
}
