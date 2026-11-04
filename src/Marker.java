import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        for (int i = 1, k = 0; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];
            }
            if (p.charAt(i) == p.charAt(k)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        if (p.isEmpty()) return new int[0];
        int n = text.length();
        int m = p.length();
        if (n < m) return new int[0];

        int[] pi = prefixFunction(p);
        List<Integer> matches = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < n; i++) {
            while (k > 0 && (k == m || text.charAt(i) != p.charAt(k))) {
                k = pi[k - 1];
            }
            if (text.charAt(i) == p.charAt(k)) {
                k++;
            }
            if (k == m) {
                matches.add(i - m + 1);
            }
        }

        int[] res = new int[matches.size()];
        for (int i = 0; i < matches.size(); i++) {
            res[i] = matches.get(i);
        }
        return res;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        int n = a.length();
        int m = b.length();
        if (n < k || m < k) return 0;

        final long BASE = 31;
        final long MOD = 1_000_000_007L;

        long basePow = 1;
        for (int i = 0; i < k - 1; i++) {
            basePow = (basePow * BASE) % MOD;
        }

        Map<Long, List<Integer>> bHashes = new HashMap<>();

        // Compute hashes for all windows in b
        long hashB = 0;
        for (int i = 0; i < k; i++) {
            hashB = (hashB * BASE + b.charAt(i)) % MOD;
        }
        bHashes.computeIfAbsent(hashB, x -> new ArrayList<>()).add(0);

        for (int j = 1; j <= m - k; j++) {
            long prevChar = b.charAt(j - 1);
            long nextChar = b.charAt(j + k - 1);
            hashB = ((hashB - (prevChar * basePow) % MOD + MOD) % MOD * BASE + nextChar) % MOD;
            bHashes.computeIfAbsent(hashB, x -> new ArrayList<>()).add(j);
        }

        // Slide over a and check
        int count = 0;
        long hashA = 0;
        for (int i = 0; i < k; i++) {
            hashA = (hashA * BASE + a.charAt(i)) % MOD;
        }

        if (matchesAny(a, 0, b, bHashes.get(hashA), k)) {
            count++;
        }

        for (int i = 1; i <= n - k; i++) {
            long prevChar = a.charAt(i - 1);
            long nextChar = a.charAt(i + k - 1);
            hashA = ((hashA - (prevChar * basePow) % MOD + MOD) % MOD * BASE + nextChar) % MOD;

            if (matchesAny(a, i, b, bHashes.get(hashA), k)) {
                count++;
            }
        }

        return count;
    }

    private static boolean matchesAny(String a, int startA, String b, List<Integer> bPositions, int k) {
        if (bPositions == null) return false;
        for (int startB : bPositions) {
            boolean equal = true;
            for (int d = 0; d < k; d++) {
                if (a.charAt(startA + d) != b.charAt(startB + d)) {
                    equal = false;
                    break;
                }
            }
            if (equal) return true;
        }
        return false;
    }
}
