import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
    private static NFA nfa;
    private static DFA dfa;

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        while (true) {
            System.out.println();
            System.out.println("===== NFA, DFA & Konversi NFA → DFA =====");
            System.out.println("1. Input NFA (boleh transisi ε)");
            System.out.println("2. Input DFA");
            System.out.println("3. Tampilkan tabel transisi");
            System.out.println("4. Konversi NFA → DFA");
            System.out.println("5. Uji string");
            System.out.println("6. Muat contoh ε-NFA");
            System.out.println("0. Keluar");
            switch (ask("Pilih menu: ")) {
                case "1": inputNfa(); break;
                case "2": inputDfa(); break;
                case "3": showTables(); break;
                case "4": convert(); break;
                case "5": testString(); break;
                case "6": loadExample(); break;
                case "0": System.out.println("Selesai."); return;
                default: System.out.println("Menu tidak valid.");
            }
        }
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) {
            System.exit(0);
        }
        return in.nextLine().trim();
    }

    private static List<String> tokens(String line) {
        return line.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(line.split("\\s+")));
    }

    private static List<String> askStates() {
        while (true) {
            List<String> states = new ArrayList<>(new LinkedHashSet<>(tokens(ask("Daftar state (pisah spasi, mis. q0 q1 q2): "))));
            if (!states.isEmpty()) {
                return states;
            }
            System.out.println("Minimal harus ada satu state.");
        }
    }

    private static List<Character> askAlphabet() {
        while (true) {
            List<String> symbols = tokens(ask("Alfabet (pisah spasi, mis. a b): "));
            boolean valid = !symbols.isEmpty()
                    && symbols.stream().allMatch(s -> s.length() == 1 && s.charAt(0) != NFA.EPSILON);
            if (valid) {
                return symbols.stream().map(s -> s.charAt(0)).distinct().collect(Collectors.toList());
            }
            System.out.println("Setiap simbol harus 1 karakter dan bukan ε.");
        }
    }

    private static String askState(String prompt, List<String> states) {
        while (true) {
            String s = ask(prompt);
            if (states.contains(s)) {
                return s;
            }
            System.out.println("State '" + s + "' tidak terdaftar.");
        }
    }

    private static List<String> askStateSet(String prompt, List<String> states) {
        while (true) {
            String line = ask(prompt);
            List<String> result = line.equals("-") ? new ArrayList<>() : tokens(line);
            if (states.containsAll(result)) {
                return result;
            }
            System.out.println("Ada state yang tidak terdaftar.");
        }
    }

    private static void inputNfa() {
        NFA n = new NFA();
        List<String> states = askStates();
        states.forEach(n::addState);
        askAlphabet().forEach(n::addSymbol);
        n.start = askState("State awal: ", states);
        n.finals.addAll(askStateSet("State akhir (pisah spasi): ", states));

        System.out.println("Isi transisi. Pisahkan beberapa state tujuan dengan spasi, '-' jika kosong.");
        List<Character> symbols = new ArrayList<>(n.alphabet);
        symbols.add(NFA.EPSILON);
        for (String s : states) {
            for (char c : symbols) {
                for (String t : askStateSet("δ(" + s + ", " + c + ") = ", states)) {
                    n.addTransition(s, c, t);
                }
            }
        }
        nfa = n;
        System.out.println("\nNFA tersimpan:");
        nfa.printTable();
    }

    private static void inputDfa() {
        DFA d = new DFA();
        List<String> states = askStates();
        states.forEach(d::addState);
        askAlphabet().forEach(d::addSymbol);
        d.start = askState("State awal: ", states);
        d.finals.addAll(askStateSet("State akhir (pisah spasi): ", states));

        System.out.println("Isi transisi dengan tepat satu state tujuan, '-' jika tidak ada.");
        for (String s : states) {
            for (char c : d.alphabet) {
                while (true) {
                    String t = ask("δ(" + s + ", " + c + ") = ");
                    if (t.equals("-")) {
                        break;
                    }
                    if (states.contains(t)) {
                        d.addTransition(s, c, t);
                        break;
                    }
                    System.out.println("State '" + t + "' tidak terdaftar.");
                }
            }
        }
        dfa = d;
        System.out.println("\nDFA tersimpan:");
        dfa.printTable();
    }

    private static void showTables() {
        if (nfa == null && dfa == null) {
            System.out.println("Belum ada automata. Input dulu lewat menu 1, 2, atau 6.");
            return;
        }
        if (nfa != null) {
            System.out.println("\nTabel transisi NFA:");
            nfa.printTable();
        }
        if (dfa != null) {
            System.out.println("\nTabel transisi DFA:");
            dfa.printTable();
        }
    }

    private static void convert() {
        if (nfa == null) {
            System.out.println("Belum ada NFA. Input dulu lewat menu 1 atau 6.");
            return;
        }
        System.out.println("\nNFA asal:");
        nfa.printTable();
        System.out.println("\nLangkah subset construction:");
        dfa = Converter.convert(nfa);
        System.out.println("\nDFA hasil konversi (disimpan sebagai DFA aktif):");
        dfa.printTable();
    }

    private static void testString() {
        if (nfa == null && dfa == null) {
            System.out.println("Belum ada automata. Input dulu lewat menu 1, 2, atau 6.");
            return;
        }
        String w = ask("Masukkan string (ketik ε untuk string kosong): ");
        if (w.equals(String.valueOf(NFA.EPSILON))) {
            w = "";
        }
        if (nfa != null) {
            System.out.println("NFA:");
            System.out.println(nfa.accepts(w) ? "  → DITERIMA" : "  → DITOLAK");
        }
        if (dfa != null) {
            System.out.println("DFA:");
            System.out.println(dfa.accepts(w) ? "  → DITERIMA" : "  → DITOLAK");
        }
    }

    // ε-NFA untuk bahasa 0*1*2*
    private static void loadExample() {
        NFA n = new NFA();
        for (String s : new String[] {"q0", "q1", "q2"}) {
            n.addState(s);
        }
        for (char c : new char[] {'0', '1', '2'}) {
            n.addSymbol(c);
        }
        n.start = "q0";
        n.finals.add("q2");
        n.addTransition("q0", '0', "q0");
        n.addTransition("q0", NFA.EPSILON, "q1");
        n.addTransition("q1", '1', "q1");
        n.addTransition("q1", NFA.EPSILON, "q2");
        n.addTransition("q2", '2', "q2");
        nfa = n;
        System.out.println("Contoh ε-NFA untuk bahasa 0*1*2* dimuat:");
        nfa.printTable();
    }
}
