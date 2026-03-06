import java.util.*;

/**
 * Question 6: Emergency Supply Logistics
 * Part A: Dijkstra Safest Path using -log(probability) weight transformation.
 * Part B: Edmonds-Karp Maximum Flow for maximum supply throughput.
 */
public class Question6 {

    // ─── Part A: Safest Path ──
    // graph[u] = list of {neighborIndex, safetyProbability}
    static Map<String, Double> safestPath(Map<String, List<double[]>> graph, String[] nodes, String src) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, Integer> idxOf = new HashMap<>();
        for (int i = 0; i < nodes.length; i++) {
            dist.put(nodes[i], Double.MAX_VALUE);
            idxOf.put(nodes[i], i);
        }
        dist.put(src, 0.0);

        // PQ: [transformed cost, nodeIndex]
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));
        pq.offer(new double[] { 0.0, idxOf.get(src) });

        while (!pq.isEmpty()) {
            double[] cur = pq.poll();
            String u = nodes[(int) cur[1]];
            if (cur[0] > dist.get(u))
                continue;
            for (double[] e : graph.getOrDefault(u, List.of())) {
                String v = nodes[(int) e[0]];
                double w = -Math.log(e[1]); // Key transformation: maximize product <=> minimize sum(-log)
                if (dist.get(u) + w < dist.get(v)) {
                    dist.put(v, dist.get(u) + w);
                    pq.offer(new double[] { dist.get(v), e[0] });
                }
            }
        }
        // Convert back: safety probability 
        Map<String, Double> result = new LinkedHashMap<>();
        for (String n : nodes) {
            double d = dist.get(n);
            result.put(n, d == Double.MAX_VALUE ? 0.0 : Math.exp(-d));
        }
        return result;
    }

    // ─── Part B: Max Flow ───
    static int maxFlow(int[][] cap, int src, int sink) {
        int n = cap.length, flow = 0;
        int[][] res = new int[n][n];
        for (int i = 0; i < n; i++)
            System.arraycopy(cap[i], 0, res[i], 0, n);

        while (true) {
            int[] parent = new int[n];
            Arrays.fill(parent, -1);
            parent[src] = src;
            Queue<Integer> q = new LinkedList<>();
            q.add(src);
            while (!q.isEmpty() && parent[sink] == -1) {
                int u = q.poll();
                for (int v = 0; v < n; v++)
                    if (parent[v] == -1 && res[u][v] > 0) {
                        parent[v] = u;
                        q.add(v);
                    }
            }
            if (parent[sink] == -1)
                break;

            int bottleneck = Integer.MAX_VALUE;
            for (int v = sink; v != src; v = parent[v])
                bottleneck = Math.min(bottleneck, res[parent[v]][v]);

            // Print augmenting path
            List<Integer> path = new ArrayList<>();
            for (int v = sink; v != src; v = parent[v])
                path.add(0, v);
            path.add(0, src);
            System.out.println("  Path: " + path + " | bottleneck = " + bottleneck);

            for (int v = sink; v != src; v = parent[v]) {
                res[parent[v]][v] -= bottleneck;
                res[v][parent[v]] += bottleneck;
            }
            flow += bottleneck;
        }
        return flow;
    }

    public static void main(String[] args) {
        // ── Part A ──
        System.out.println("━━━ Q6 Part A: Safest Path (Dijkstra + log transform) ━━━\n");
        String[] nodes = { "KTM", "JA", "JB", "PH", "BS" };
        Map<String, List<double[]>> g = new HashMap<>();
        g.put("KTM", List.of(new double[] { 1, 0.9 }, new double[] { 2, 0.8 }));
        g.put("JA", List.of(new double[] { 3, 0.95 }));
        g.put("JB", List.of(new double[] { 3, 0.7 }, new double[] { 4, 0.85 }));
        g.put("PH", List.of(new double[] { 4, 0.99 }));
        g.put("BS", List.of());

        Map<String, Double> probs = safestPath(g, nodes, "KTM");
        System.out.println("Safest path probabilities from KTM:");
        probs.forEach((k, v) -> System.out.printf("  KTM -> %-5s : %.4f (%.1f%% safe)\n", k, v, v * 100));
        System.out.println("\nKey insight: Maximize ∏p(i) == Minimize Σ(-log p(i))");
        System.out.println("Since -log(p) ≥ 0 for p ∈ (0,1], standard Dijkstra applies correctly.\n");

        // ── Part B ──
        System.out.println("━━━ Q6 Part B: Max Flow / Max Throughput (Edmonds-Karp) ━━━\n");
        int[][] cap = {
                { 0, 10, 15, 0, 0 }, // KTM
                { 0, 0, 0, 8, 5 }, // JA
                { 0, 4, 0, 0, 12 }, // JB
                { 0, 0, 0, 0, 6 }, // PH
                { 0, 0, 0, 0, 0 } // BS
        };
        System.out.println("Graph: KTM→JA(10), KTM→JB(15), JA→PH(8), JA→BS(5), JB→JA(4), JB→BS(12), PH→BS(6)");
        System.out.println("Augmenting paths:");
        int mf = maxFlow(cap, 0, 4);
        System.out.println("\nMax Flow (KTM → BS) = " + mf + " units");
        System.out.println("By Max-Flow Min-Cut theorem: max flow = min cut capacity separating KTM from BS.");
    }
}