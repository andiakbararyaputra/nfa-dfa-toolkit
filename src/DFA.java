import java.util.*;

public class DFA {
    final List<String> states = new ArrayList<>();
    final List<Character> alphabet = new ArrayList<>();
    final Map<String, Map<Character, String>> delta = new HashMap<>();
    final Set<String> finals = new HashSet<>();
    String start;

    void addState(String state) {
        if (!states.contains(state)) {
            states.add(state);
        }
    }

    void addSymbol(char symbol) {
        if (!alphabet.contains(symbol)) {
            alphabet.add(symbol);
        }
    }

    void addTransition(String from, char symbol, String to) {
        delta.computeIfAbsent(from, k -> new HashMap<>()).put(symbol, to);
    }

    String next(String state, char symbol) {
        return delta.getOrDefault(state, Collections.emptyMap()).get(symbol);
    }

    boolean accepts(String input) {
        String current = start;
        System.out.println("  mulai di " + current);
        for (char c : input.toCharArray()) {
            if (!alphabet.contains(c)) {
                System.out.println("  Simbol '" + c + "' tidak ada di alfabet.");
                return false;
            }
            String target = next(current, c);
            if (target == null) {
                System.out.println("  δ(" + current + ", " + c + ") tidak terdefinisi.");
                return false;
            }
            System.out.println("  δ(" + current + ", " + c + ") = " + target);
            current = target;
        }
        return finals.contains(current);
    }

    void printTable() {
        List<String[]> rows = new ArrayList<>();
        String[] header = new String[alphabet.size() + 1];
        header[0] = "δ";
        for (int i = 0; i < alphabet.size(); i++) {
            header[i + 1] = String.valueOf(alphabet.get(i));
        }
        rows.add(header);
        for (String s : states) {
            String[] row = new String[alphabet.size() + 1];
            row[0] = Table.label(s, start, finals);
            for (int i = 0; i < alphabet.size(); i++) {
                String target = next(s, alphabet.get(i));
                row[i + 1] = target == null ? "-" : target;
            }
            rows.add(row);
        }
        Table.print(rows);
    }
}
