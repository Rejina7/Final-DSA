import java.util.*;

/**
 * Question 1a: Ideal Repeater Placement
 */
public class Question1a {

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static int maxCustomerCoverage(int[][] locs) {
        if (locs.length <= 2)
            return locs.length;
        int max = 0;
        for (int i = 0; i < locs.length; i++) {
            Map<String, Integer> slopes = new HashMap<>();
            int dups = 1, curMax = 0, x1 = locs[i][0], y1 = locs[i][1];
            for (int j = i + 1; j < locs.length; j++) {
                int x2 = locs[j][0], y2 = locs[j][1];
                if (x1 == x2 && y1 == y2) {
                    dups++;
                    continue;
                }
                if (x1 == x2) {
                    slopes.merge("inf", 1, Integer::sum);
                    curMax = Math.max(curMax, slopes.get("inf"));
                    continue;
                }
                int dy = y2 - y1, dx = x2 - x1, g = gcd(Math.abs(dy), Math.abs(dx));
                if (dx < 0) {
                    dx = -dx;
                    dy = -dy;
                }
                String k = (dy / g) + "/" + (dx / g);
                slopes.merge(k, 1, Integer::sum);
                curMax = Math.max(curMax, slopes.get(k));
            }
            max = Math.max(max, curMax + dups);
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println("Q1a: Ideal Repeater Placement (Max Points on a Line)");
        System.out.println("Ex1 [1,1],[2,2],[3,3] -> "
                + maxCustomerCoverage(new int[][] { { 1, 1 }, { 2, 2 }, { 3, 3 } }) + " (Expected: 3)");
        System.out.println("Ex2 [1,1],[3,2],[5,3],[4,1],[2,3],[1,4] -> "
                + maxCustomerCoverage(new int[][] { { 1, 1 }, { 3, 2 }, { 5, 3 }, { 4, 1 }, { 2, 3 }, { 1, 4 } })
                + " (Expected: 4)");
    }
}