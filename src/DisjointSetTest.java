/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // Own checks
        DisjointSet large = new DisjointSet(1000);
        for (int i = 0; i < 999; i++) {
            large.union(i, i + 1);
        }
        check(large.count() == 1, "large chain reduces to 1 set");
        check(large.connected(0, 999), "chain 0 to 999 connected");
        check(!large.union(10, 20), "union of already connected set returns false");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}