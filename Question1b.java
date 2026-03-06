import java.util.*;

/**
 * Question 1b: Search Query Segmentation
 * Find all possible ways to split a query string using a dictionary of
 * marketing keywords.
 */
public class Question1b {

    public static List<String> keywordSegmentation(String s, List<String> dict) {
        return dfs(s, new HashSet<>(dict), 0, new HashMap<>());
    }

    private static List<String> dfs(String s, Set<String> dict, int idx, Map<Integer, List<String>> memo) {
        if (memo.containsKey(idx))
            return memo.get(idx);
        List<String> res = new ArrayList<>();
        if (idx == s.length()) {
            res.add("");
            return res;
        }
        for (int i = idx + 1; i <= s.length(); i++) {
            String w = s.substring(idx, i);
            if (dict.contains(w))
                for (String rest : dfs(s, dict, i, memo))
                    res.add(rest.isEmpty() ? w : w + " " + rest);
        }
        memo.put(idx, res);
        return res;
    }

    public static void main(String[] args) {
        System.out.println("Q1b: Search Query Segmentation");
        System.out.println("Ex1: " + keywordSegmentation("nepaltrekkingguide",
                Arrays.asList("nepal", "trekking", "guide", "nepaltrekking")));
        System.out.println("Ex2: " + keywordSegmentation("visitkathmandunepal",
                Arrays.asList("visit", "kathmandu", "nepal", "visitkathmandu", "kathmandunepal")));
        System.out.println("Ex3: " + keywordSegmentation("everesthikingtrail",
                Arrays.asList("everest", "hiking", "trek")));
    }
}