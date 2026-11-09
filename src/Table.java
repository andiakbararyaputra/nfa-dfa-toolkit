import java.util.List;

class Table {
    static void print(List<String[]> rows) {
        int[] width = new int[rows.get(0).length];
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                width[i] = Math.max(width[i], row[i].length());
            }
        }
        for (int r = 0; r < rows.size(); r++) {
            StringBuilder line = new StringBuilder("|");
            for (int i = 0; i < width.length; i++) {
                line.append(' ').append(String.format("%-" + width[i] + "s", rows.get(r)[i])).append(" |");
            }
            System.out.println(line);
            if (r == 0) {
                StringBuilder sep = new StringBuilder("|");
                for (int w : width) {
                    sep.append("-".repeat(w + 2)).append('|');
                }
                System.out.println(sep);
            }
        }
        System.out.println("(→ = state awal, * = state akhir)");
    }

    static String label(String state, String start, java.util.Set<String> finals) {
        return (state.equals(start) ? "→" : " ") + (finals.contains(state) ? "*" : " ") + state;
    }
}
