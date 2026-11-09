# NFA, DFA dan Konversi NFA ke DFA

Program konsol Java untuk mata kuliah **Teori Otomata dan Bahasa**. Program ini dapat:

- Mendefinisikan **NFA** (termasuk transisi **ε** / ε-NFA) dan **DFA** secara interaktif.
- Menampilkan **tabel transisi** NFA dan DFA.
- Mengonversi **NFA → DFA** menggunakan metode **subset construction**, lengkap dengan langkah-langkahnya.
- Menguji apakah sebuah **string diterima atau ditolak** oleh NFA maupun DFA, beserta jejak prosesnya.

## Struktur Proyek

```
.
├── README.md
└── src/
    ├── Main.java       # Menu utama dan input interaktif
    ├── NFA.java        # Model NFA: ε-closure, move, uji string, tabel transisi
    ├── DFA.java        # Model DFA: uji string, tabel transisi
    ├── Converter.java  # Konversi NFA → DFA (subset construction)
    └── Table.java      # Pencetak tabel transisi
```

## Kebutuhan

- Java Development Kit (JDK) **11** atau lebih baru.
- Terminal yang mendukung **UTF-8** (agar simbol `ε`, `δ`, `∅`, `→` tampil dengan benar).

## Cara Menjalankan

Jalankan dari folder utama repositori:

```sh
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

## Menu Program

```
===== NFA, DFA & Konversi NFA → DFA =====
1. Input NFA (boleh transisi ε)
2. Input DFA
3. Tampilkan tabel transisi
4. Konversi NFA → DFA
5. Uji string
6. Muat contoh ε-NFA
0. Keluar
```

### Aturan Input

| Input            | Keterangan                                                        |
|------------------|-------------------------------------------------------------------|
| Daftar state     | Dipisah spasi, contoh: `q0 q1 q2`                                 |
| Alfabet          | Satu karakter per simbol, dipisah spasi, contoh: `a b`. Tidak boleh `ε` |
| State akhir      | Dipisah spasi, boleh lebih dari satu                              |
| Transisi NFA     | Beberapa state tujuan dipisah spasi, `-` jika tidak ada transisi  |
| Transisi DFA     | Tepat satu state tujuan, `-` jika tidak ada transisi              |
| Transisi epsilon | Ditanyakan otomatis sebagai `δ(q, ε)` untuk setiap state          |
| String kosong    | Ketik `ε` saat menguji string                                     |

## Cara Kerja Konversi (Subset Construction)

1. State awal DFA = **ε-closure**(state awal NFA).
2. Untuk setiap state DFA `S` (himpunan state NFA) dan setiap simbol `a`:
   `δ_DFA(S, a) = ε-closure(move(S, a))`
3. Setiap himpunan baru yang muncul menjadi state DFA baru, diproses sampai tidak ada state baru.
4. State DFA merupakan **state akhir** jika memuat minimal satu state akhir NFA.
5. Himpunan kosong `∅` menjadi *trap state* (state jebakan).

## Contoh (Menu 6 → Menu 4)

ε-NFA untuk bahasa `0*1*2*`:

```
| δ    | 0    | 1    | 2    | ε    |
|------|------|------|------|------|
| → q0 | {q0} | ∅    | ∅    | {q1} |
|   q1 | ∅    | {q1} | ∅    | {q2} |
|  *q2 | ∅    | ∅    | {q2} | ∅    |
(→ = state awal, * = state akhir)
```

Langkah subset construction:

```
State awal DFA = ε-closure({q0}) = {q0,q1,q2}

Proses {q0,q1,q2}:
  δ({q0,q1,q2}, 0) = ε-closure({q0}) = {q0,q1,q2}
  δ({q0,q1,q2}, 1) = ε-closure({q1}) = {q1,q2}  [state baru]
  δ({q0,q1,q2}, 2) = ε-closure({q2}) = {q2}  [state baru]

Proses {q1,q2}:
  δ({q1,q2}, 0) = ε-closure(∅) = ∅  [state baru]
  δ({q1,q2}, 1) = ε-closure({q1}) = {q1,q2}
  δ({q1,q2}, 2) = ε-closure({q2}) = {q2}

Proses {q2}:
  δ({q2}, 0) = ε-closure(∅) = ∅
  δ({q2}, 1) = ε-closure(∅) = ∅
  δ({q2}, 2) = ε-closure({q2}) = {q2}

Proses ∅:
  δ(∅, 0) = ε-closure(∅) = ∅
  δ(∅, 1) = ε-closure(∅) = ∅
  δ(∅, 2) = ε-closure(∅) = ∅
```

DFA hasil konversi:

```
| δ            | 0          | 1       | 2    |
|--------------|------------|---------|------|
| →*{q0,q1,q2} | {q0,q1,q2} | {q1,q2} | {q2} |
|  *{q1,q2}    | ∅          | {q1,q2} | {q2} |
|  *{q2}       | ∅          | ∅       | {q2} |
|   ∅          | ∅          | ∅       | ∅    |
(→ = state awal, * = state akhir)
```

Uji string (Menu 5): `0012` → **DITERIMA**, `210` → **DITOLAK** pada NFA maupun DFA hasil konversi.
