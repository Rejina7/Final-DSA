import java.util.*;

/**
 * Question 4: Smart Energy Grid Load Distribution Optimization
 * Greedy allocation of cheapest available energy sources to meet district
 * demands.
 */
public class Question4 {

    record Src(String t, double cap, int s, int e, double c) {
    }

    public static void main(String[] args) {
        LinkedHashMap<String, Map<String, Double>> demand = new LinkedHashMap<>();
        demand.put("06", Map.of("A", 20.0, "B", 15.0, "C", 25.0));
        demand.put("07", Map.of("A", 22.0, "B", 16.0, "C", 28.0));

        List<Src> sources = Arrays.asList(
                new Src("Solar", 50.0, 6, 18, 1.0),
                new Src("Hydro", 40.0, 0, 24, 1.5),
                new Src("Diesel", 60.0, 17, 23, 3.0));

        double totalCost = 0;
        System.out.println("Hr\tDist\tSolar\tHydro\tDiesel\tUsed\tDemand\t%Met");

        for (var hr : demand.entrySet()) {
            int h = Integer.parseInt(hr.getKey());
            
            // Get active & copy sources for this hour
            var active = new ArrayList<Src>();
            for (Src s : sources)
                if (h >= s.s && h <= s.e)
                    active.add(new Src(s.t, s.cap, s.s, s.e, s.c));
            active.sort(Comparator.comparingDouble(s -> s.c)); // Greedy: cheapest first

            for (var d : hr.getValue().entrySet()) {
                double dem = d.getValue(), rem = dem;
                Map<String, Double> alloc = new HashMap<>();
                for (int i = 0; i < active.size() && rem > 0; i++) {
                    Src s = active.get(i);
                    double a = Math.min(rem, s.cap);
                    alloc.merge(s.t, a, Double::sum);
                    active.set(i, new Src(s.t, s.cap - a, s.s, s.e, s.c));
                    rem -= a;
                    totalCost += a * s.c;
                }
                double used = dem - rem;
                System.out.printf("%s\t%s\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f%%\n",
                        hr.getKey(), d.getKey(),
                        alloc.getOrDefault("Solar", 0.0),
                        alloc.getOrDefault("Hydro", 0.0),
                        alloc.getOrDefault("Diesel", 0.0),
                        used, dem, (used / dem) * 100);
            }
        }
        System.out.printf("\nTotal cost: Rs. %.2f\n", totalCost);
        System.out.println("Algorithm: Greedy (sort by cost) - O(N*M) per hour, N=districts, M=sources.");
    }
}
