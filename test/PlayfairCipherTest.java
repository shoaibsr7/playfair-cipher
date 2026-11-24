/** Dependency-free tests. Run: javac -d out src/*.java test/*.java && java -cp out PlayfairCipherTest */
public class PlayfairCipherTest {

    private static int failures = 0;

    private static void check(String name, String expected, String actual) {
        boolean ok = expected.equals(actual);
        if (!ok) {
            failures++;
        }
        System.out.printf("%s %-40s %s%n", ok ? "PASS" : "FAIL", name, ok ? "" : "expected " + expected + " got " + actual);
    }

    public static void main(String[] args) {
        // Classic example (Wikipedia): key "playfair example"
        PlayfairCipher c = new PlayfairCipher("playfair example");
        check("key square first row", "PLAYF", new String(PlayfairCipher.generateKeyMatrix("playfair example")[0]));
        check("encrypt textbook example", "BMODZBXDNABEKUDMUIXMMOUVIF", c.encrypt("Hide the gold in the tree stump"));
        check("decrypt textbook example", "HIDETHEGOLDINTHETREXESTUMP", c.decrypt("BMODZBXDNABEKUDMUIXMMOUVIF"));

        PlayfairCipher m = new PlayfairCipher("MONARCHY");
        check("encrypt MONARCHY example", "GATLMZCLRQXA", m.encrypt("instruments"));
        check("round trip", "INSTRUMENTSX", m.decrypt(m.encrypt("instruments")));
        check("J is treated as I", m.encrypt("jam"), m.encrypt("iam"));
        check("double letters are split", "BALXLOON", String.join("", PlayfairCipher.toDigraphs("balloon")));

        if (failures > 0) {
            System.out.println(failures + " test(s) failed");
            System.exit(1);
        }
        System.out.println("All tests passed");
    }
}
