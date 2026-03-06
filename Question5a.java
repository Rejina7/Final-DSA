
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Question 5a: Tourist Spot Optimizer
 * Swing GUI that uses a greedy heuristic to plan an itinerary within given time
 * and budget.
 */
public class Question5a extends JFrame {

    // Static inner class instead of record to ensure compatibility
    static class Spot {
        String name;
        double lat, lon, fee;
        List<String> tags;

        Spot(String n, double la, double lo, double f, List<String> t) {
            name = n;
            lat = la;
            lon = lo;
            fee = f;
            tags = t;
        }
    }

    List<Spot> spots = new ArrayList<>();
    JTextField tF = new JTextField("8"), bF = new JTextField("1000");
    JComboBox<String> iC = new JComboBox<>(new String[] { "culture", "nature", "heritage", "adventure", "religious" });
    JTextArea out = new JTextArea();

    public Question5a() {
        // Load data
        spots.add(new Spot("Pashupatinath", 27.71, 85.34, 100, List.of("culture", "religious")));
        spots.add(new Spot("Swayambhunath", 27.714, 85.29, 200, List.of("culture", "heritage")));
        spots.add(new Spot("Garden of Dreams", 27.712, 85.31, 150, List.of("nature")));
        spots.add(new Spot("Chandragiri Hills", 27.66, 85.24, 700, List.of("nature", "adventure")));
        spots.add(new Spot("Durbar Square", 27.70, 85.30, 100, List.of("culture", "heritage")));

        setTitle("Q5a: Tourist Spot Optimizer");
        setSize(600, 420);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(4, 2, 5, 5));
        top.setBorder(BorderFactory.createTitledBorder("Preferences"));
        top.add(new JLabel("Hours:"));
        top.add(tF);
        top.add(new JLabel("Budget (Rs):"));
        top.add(bF);
        top.add(new JLabel("Interest:"));
        top.add(iC);
        JButton run = new JButton("Optimize Itinerary");
        run.addActionListener(e -> optimize());
        top.add(new JLabel());
        top.add(run);

        out.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(out), BorderLayout.CENTER);
    }

    void optimize() {
        double T, B;
        try {
            T = Double.parseDouble(tF.getText());
            B = Double.parseDouble(bF.getText());
        } catch (NumberFormatException ex) {
            out.setText("Error: Please enter valid numbers for Hours and Budget.");
            return;
        }
        String I = (String) iC.getSelectedItem();
        List<Spot> avail = new ArrayList<>(spots);
        double lat = 27.7, lon = 85.3;
        out.setText("Greedy heuristic: score = (1000 if interest matches) - fee - distance\n\n");

        while (!avail.isEmpty() && B > 0 && T > 0) {
            Spot best = null;
            double bestScore = Double.NEGATIVE_INFINITY;
            for (Spot sp : avail) {
                if (sp.fee > B)
                    continue;
                double dist = Math.sqrt(Math.pow(sp.lat - lat, 2) + Math.pow(sp.lon - lon, 2)) * 100;
                double estTime = 2 + dist / 50;
                if (estTime > T)
                    continue;
                double score = (sp.tags.contains(I) ? 1000 : 0) - sp.fee - dist;
                if (score > bestScore) {
                    bestScore = score;
                    best = sp;
                }
            }
            if (best == null)
                break;
            double dist = Math.sqrt(Math.pow(best.lat - lat, 2) + Math.pow(best.lon - lon, 2)) * 100;
            T -= (2 + dist / 50);
            B -= best.fee;
            lat = best.lat;
            lon = best.lon;
            out.append("✔ " + best.name + " | Rs " + (int) best.fee + " | Interest match: " + best.tags.contains(I)
                    + "\n");
            avail.remove(best);
        }
        out.append(String.format("%n--- Remaining Budget: Rs %.0f | Remaining Time: %.1f h ---%n", B, T));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Question5a().setVisible(true));
    }
}

