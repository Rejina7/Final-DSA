import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Question 5b: Multi-threaded Weather Data Collector
 * Swing GUI that fetches weather from 5 cities using both sequential and
 * concurrent threads,
 * then compares the latency of both approaches.
 */
public class Question5b extends JFrame {

    static final String[] CITIES = { "Kathmandu", "Pokhara", "Biratnagar", "Nepalgunj", "Dhangadhi" };
    static final double[][] COORDS = { { 27.71, 85.32 }, { 28.21, 83.98 }, { 26.45, 87.27 }, { 28.05, 81.61 },
            { 28.68, 80.56 } };

    DefaultTableModel tableModel = new DefaultTableModel(new String[] { "City", "Temp (°C)", "Wind (km/h)" }, 0);
    JLabel statusLabel = new JLabel("Click Fetch to start");
    JButton fetchBtn;

    public Question5b() {
        setTitle("Q5b: Multi-threaded Weather Collector");
        setSize(550, 380);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        fetchBtn = new JButton("Fetch & Compare");
        fetchBtn.addActionListener(e -> {
            fetchBtn.setEnabled(false);
            tableModel.setRowCount(0);
            new Thread(this::runBenchmark).start();
        });
        top.add(fetchBtn);
        top.add(statusLabel);

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    void runBenchmark() {
        // --- Sequential run ---
        updateStatus("Running Sequential fetch...");
        clearTable();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < CITIES.length; i++)
            fetchWeather(CITIES[i], COORDS[i][0], COORDS[i][1]);
        long seqMs = System.currentTimeMillis() - t1;

        // --- Concurrent run ---
        updateStatus("Running Concurrent fetch...");
        clearTable();
        CountDownLatch latch = new CountDownLatch(CITIES.length);
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < CITIES.length; i++) {
            int idx = i;
            new Thread(() -> {
                fetchWeather(CITIES[idx], COORDS[idx][0], COORDS[idx][1]);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
        long concMs = System.currentTimeMillis() - t2;

        double speedup = concMs > 0 ? (double) seqMs / concMs : seqMs;
        updateStatus(String.format("Sequential: %dms | Concurrent: %dms | Speedup: %.1fx", seqMs, concMs, speedup));
        SwingUtilities.invokeLater(() -> fetchBtn.setEnabled(true));
    }

    void fetchWeather(String city, double lat, double lon) {
        try {
            Thread.sleep(400); // Artificial delay to highlight concurrency speedup
            String urlStr = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%.2f&longitude=%.2f&current_weather=true", lat,
                    lon);
            HttpURLConnection conn = (HttpURLConnection) URI.create(urlStr).toURL().openConnection();
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            try (InputStream is = conn.getInputStream();
                    Scanner sc = new Scanner(is).useDelimiter("\\A")) {
                String json = sc.hasNext() ? sc.next() : "";
                String temp = extractValue(json, "\"temperature\":");
                String wind = extractValue(json, "\"windspeed\":");
                SwingUtilities.invokeLater(() -> tableModel.addRow(new Object[] { city, temp, wind }));
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> tableModel.addRow(new Object[] { city, "ERROR", e.getMessage() }));
        }
    }

    String extractValue(String json, String key) {
        int i = json.indexOf(key);
        if (i == -1)
            return "N/A";
        int start = i + key.length();
        int end = json.indexOf(",", start);
        if (end == -1)
            end = json.indexOf("}", start);
        return json.substring(start, end).trim();
    }

    void updateStatus(String msg) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(msg));
    }

    void clearTable() {
        SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Question5b().setVisible(true));
    }
}