import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Playfair cipher.
 *
 * <p>Three steps, matching the classic algorithm:
 * <ol>
 *   <li>{@link #generateKeyMatrix(String)} builds a 5x5 key square from a secret key
 *       (key letters first, then the rest of the alphabet; I and J share a cell).</li>
 *   <li>{@link #encrypt(String)} splits the plaintext into letter pairs (an X separates a doubled
 *       letter and pads an odd-length message) and transforms each pair.</li>
 *   <li>{@link #decrypt(String)} reverses the transformation.</li>
 * </ol>
 */
public class PlayfairCipher {

    private static final String ALPHABET = "ABCDEFGHIKLMNOPQRSTUVWXYZ"; // no J

    private final char[][] matrix;
    private final int[] rowOf = new int[26];
    private final int[] colOf = new int[26];

    public PlayfairCipher(String key) {
        this.matrix = generateKeyMatrix(key);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                rowOf[matrix[r][c] - 'A'] = r;
                colOf[matrix[r][c] - 'A'] = c;
            }
        }
    }

    /** Builds the 5x5 key square for a secret key. */
    public static char[][] generateKeyMatrix(String key) {
        Set<Character> letters = new LinkedHashSet<>();
        for (char ch : (normalize(key) + ALPHABET).toCharArray()) {
            letters.add(ch);
        }
        char[][] m = new char[5][5];
        int i = 0;
        for (char ch : letters) {
            m[i / 5][i % 5] = ch;
            i++;
        }
        return m;
    }

    /** Uppercases, maps J to I, and drops everything that is not a letter. */
    static String normalize(String text) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toUpperCase().toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                sb.append(ch == 'J' ? 'I' : ch);
            }
        }
        return sb.toString();
    }

    /** Splits plaintext into digraphs, inserting X between doubled letters and padding the end. */
    static List<String> toDigraphs(String plaintext) {
        String s = normalize(plaintext);
        List<String> pairs = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char a = s.charAt(i);
            char filler = a == 'X' ? 'Q' : 'X';
            if (i + 1 >= s.length()) {             // odd letter at the end
                pairs.add("" + a + filler);
                i += 1;
            } else if (s.charAt(i + 1) == a) {     // doubled letter in a pair
                pairs.add("" + a + filler);
                i += 1;
            } else {
                pairs.add("" + a + s.charAt(i + 1));
                i += 2;
            }
        }
        return pairs;
    }

    public String encrypt(String plaintext) {
        StringBuilder out = new StringBuilder();
        for (String pair : toDigraphs(plaintext)) {
            out.append(transform(pair.charAt(0), pair.charAt(1), 1));
        }
        return out.toString();
    }

    public String decrypt(String ciphertext) {
        String s = normalize(ciphertext);
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException("Ciphertext must have an even number of letters");
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i += 2) {
            out.append(transform(s.charAt(i), s.charAt(i + 1), -1));
        }
        return out.toString();
    }

    /** Applies the row / column / rectangle rule. dir = +1 to encrypt, -1 to decrypt. */
    private String transform(char a, char b, int dir) {
        int ra = rowOf[a - 'A'], ca = colOf[a - 'A'];
        int rb = rowOf[b - 'A'], cb = colOf[b - 'A'];
        if (ra == rb) {
            return "" + matrix[ra][mod(ca + dir)] + matrix[rb][mod(cb + dir)];
        } else if (ca == cb) {
            return "" + matrix[mod(ra + dir)][ca] + matrix[mod(rb + dir)][cb];
        } else {
            return "" + matrix[ra][cb] + matrix[rb][ca];
        }
    }

    private static int mod(int x) {
        return ((x % 5) + 5) % 5;
    }

    public String matrixToString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : matrix) {
            for (char ch : row) {
                sb.append(ch == 'I' ? "I/J" : " " + ch + " ").append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Small interactive console program. */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Secret key: ");
        PlayfairCipher cipher = new PlayfairCipher(in.nextLine());
        System.out.println("\nKey square:\n" + cipher.matrixToString());

        while (true) {
            System.out.print("(e)ncrypt, (d)ecrypt or (q)uit? ");
            if (!in.hasNextLine()) {
                break;
            }
            String choice = in.nextLine().trim().toLowerCase();
            if (choice.startsWith("q")) {
                break;
            }
            System.out.print("Text: ");
            String text = in.nextLine();
            try {
                if (choice.startsWith("e")) {
                    System.out.println("Ciphertext: " + cipher.encrypt(text) + "\n");
                } else if (choice.startsWith("d")) {
                    System.out.println("Plaintext:  " + cipher.decrypt(text) + "\n");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }
}
