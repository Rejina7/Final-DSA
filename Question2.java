/**
 * Question 2: Hydropower Plant Cascade Efficiency
 * Find the maximum total power generation from any continuous path in a binary
 * tree.
 */
public class Question2 {

    static class TreeNode {
        int val;
        TreeNode left, right;

        TreeNode(int v) {
            val = v;
        }
    }

    private static int maxSum;

    public static int maxPowerGeneration(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        maxGain(root);
        return Math.max(0, maxSum);
    }

    private static int maxGain(TreeNode n) {
        if (n == null)
            return 0;
        int l = Math.max(0, maxGain(n.left));
        int r = Math.max(0, maxGain(n.right));
        maxSum = Math.max(maxSum, n.val + l + r); // Best path through this node
        return n.val + Math.max(l, r); // Best path extending downward
    }

    public static void main(String[] args) {
        System.out.println("Q2: Hydropower Cascade - Max Path Sum in Binary Tree");

        // Tree 1: [1, 2, 3] -> Expected: 6
        TreeNode r1 = new TreeNode(1);
        r1.left = new TreeNode(2);
        r1.right = new TreeNode(3);
        System.out.println("Ex1 [1,2,3] -> " + maxPowerGeneration(r1) + " (Expected: 6)");

        // Tree 2: [-10, 9, 20, null, null, 15, 7] -> Expected: 42
        TreeNode r2 = new TreeNode(-10);
        r2.left = new TreeNode(9);
        r2.right = new TreeNode(20);
        r2.right.left = new TreeNode(15);
        r2.right.right = new TreeNode(7);
        System.out.println("Ex2 [-10,9,20,null,null,15,7] -> " + maxPowerGeneration(r2) + " (Expected: 42)");
    }
}