/**
 * Question 3: Agricultural Commodity Trading
 * Find maximum profit with at most k buy-sell transactions.
 */
public class Question3 {

    public static int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (n == 0 || k == 0)
            return 0;

        // Unlimited transactions for large k
        if (k >= n / 2) {
            int profit = 0;
            for (int i = 1; i < n; i++)
                if (prices[i] > prices[i - 1])
                    profit += prices[i] - prices[i - 1];
            return profit;
        }

        int[][][] dp = new int[n][k + 1][2];
        for (int j = 0; j <= k; j++)
            dp[0][j][1] = -prices[0];

        for (int i = 1; i < n; i++)
            for (int j = 1; j <= k; j++) {
                dp[i][j][0] = Math.max(dp[i - 1][j][0], dp[i - 1][j][1] + prices[i]); // sell or rest
                dp[i][j][1] = Math.max(dp[i - 1][j][1], dp[i - 1][j - 1][0] - prices[i]); // buy or rest
            }
        return dp[n - 1][k][0];
    }

    public static void main(String[] args) {
        System.out.println("Q3: Agricultural Commodity Trading (Stock Buy/Sell - Max k Transactions)");
        System.out.println("Ex1 k=2, prices=[2000,4000,1000] -> " + maxProfit(2, new int[] { 2000, 4000, 1000 })
                + " (Expected: 2000)");
        System.out.println("Ex2 k=2, prices=[3,2,6,5,0,3]   -> " + maxProfit(2, new int[] { 3, 2, 6, 5, 0, 3 })
                + " (Expected: 7)");
    }
}