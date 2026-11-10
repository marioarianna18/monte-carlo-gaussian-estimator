package montecarlogaussiana;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.text.DecimalFormat;

public class GaussianMonteCarloFrame extends JFrame {

    private JTextField txtPoints, txtStdDev, txtMean;
    private JLabel lblResult;
    private SimulationPanel simulationPanel;
    private Random random;

    public GaussianMonteCarloFrame() {
        setTitle("Monte Carlo Gaussian Area Estimator");
        setSize(750, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        random = new Random();

        // --- TOP TITLE ---
        JLabel lblTitle = new JLabel("Monte Carlo Integration: Gaussian Curve Area", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- CENTER: SIMULATION PANEL ---
        simulationPanel = new SimulationPanel();
        simulationPanel.setBackground(Color.WHITE);
        simulationPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(simulationPanel, BorderLayout.CENTER);

        // --- RIGHT: CONTROL PANEL ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        controlPanel.add(createFieldRow("Total Points:", txtPoints = new JTextField("10000")));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(createFieldRow("Std Dev (σ):", txtStdDev = new JTextField("1.0")));
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(createFieldRow("Mean (μ):", txtMean = new JTextField("0.0")));
        controlPanel.add(Box.createVerticalStrut(20));

        JButton btnRun = new JButton("Run Simulation");
        btnRun.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRun.setMaximumSize(new Dimension(160, 35));
        btnRun.addActionListener(e -> runSimulation());
        controlPanel.add(btnRun);

        controlPanel.add(Box.createVerticalStrut(25));
        lblResult = new JLabel("Estimated Area: -");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResult.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(lblResult);

        add(controlPanel, BorderLayout.EAST);
    }

    private JPanel createFieldRow(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(85, 25));
        textField.setPreferredSize(new Dimension(80, 25));
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(200, 30));
        return panel;
    }

    private void runSimulation() {
        int totalPoints;
        double sigma, mu;

        try {
            totalPoints = Integer.parseInt(txtPoints.getText().trim());
            sigma = Double.parseDouble(txtStdDev.getText().trim());
            mu = Double.parseDouble(txtMean.getText().trim());
            if (sigma <= 0 || totalPoints <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid positive numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Gaussian gaussian = new Gaussian(mu, sigma);

        // Bounding box range: from mu - 3*sigma to mu + 3*sigma
        double xMin = mu - 3 * sigma;
        double xMax = mu + 3 * sigma;
        double yMax = 1.0 / (sigma * Math.sqrt(2 * Math.PI));

        int pointsUnderCurve = 0;
        simulationPanel.clearPoints();

        // Monte Carlo sampling loop
        for (int i = 0; i < totalPoints; i++) {
            double x = xMin + random.nextDouble() * (xMax - xMin);
            double y = random.nextDouble() * yMax;

            boolean isUnder = (y <= gaussian.evaluate(x));
            if (isUnder) {
                pointsUnderCurve++;
            }
            // Store points for rendering (limit sample storage for performance if points are huge)
            if (totalPoints <= 20000 || i % (totalPoints / 20000) == 0) {
                simulationPanel.addPoint(x, y, isUnder);
            }
        }

        // Area calculation: Rectangle Area * (Points Under Curve / Total Points)
        double rectangleArea = (xMax - xMin) * yMax;
        double estimatedArea = ((double) pointsUnderCurve / totalPoints) * rectangleArea;

        simulationPanel.setupParameters(mu, sigma, xMin, xMax, yMax, gaussian);
        simulationPanel.repaint();

        DecimalFormat df = new DecimalFormat("#.#####");
        lblResult.setText("Estimated Area: " + df.format(estimatedArea));
    }

    // --- INNER CLASS FOR SAFE CUSTOM RENDERING ---
    private static class SimulationPanel extends JPanel {
        private double mu, sigma, xMin, xMax, yMax;
        private Gaussian gaussian;
        private final List<DataPoint> pointsList = new ArrayList<>();

        private static class DataPoint {
            double x, y;
            boolean underCurve;
            DataPoint(double x, double y, boolean underCurve) {
                this.x = x; this.y = y; this.underCurve = underCurve;
            }
        }

        public void setupParameters(double mu, double sigma, double xMin, double xMax, double yMax, Gaussian g) {
            this.mu = mu; this.sigma = sigma; this.xMin = xMin; this.xMax = xMax; this.yMax = yMax;
            this.gaussian = g;
        }

        public void clearPoints() {
            pointsList.clear();
        }

        public void addPoint(double x, double y, boolean underCurve) {
            pointsList.add(new DataPoint(x, y, underCurve));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw Monte Carlo sampled points
            for (DataPoint p : pointsList) {
                int xp = (int) ((p.x - xMin) / (xMax - xMin) * w);
                int yp = (int) ((1.0 - (p.y / yMax)) * h);

                g2.setColor(p.underCurve ? new Color(255, 105, 180, 140) : new Color(147, 112, 219, 90));
                g2.fillRect(xp, yp, 2, 2);
            }

            // Draw theoretical Gaussian curve on top
            if (gaussian != null) {
                g2.setColor(new Color(0, 102, 204));
                g2.setStroke(new BasicStroke(2f));
                int lastXp = -1, lastYp = -1;

                for (int xp = 0; xp < w; xp++) {
                    double x = xMin + ((double) xp / w) * (xMax - xMin);
                    double y = gaussian.evaluate(x);
                    int yp = (int) ((1.0 - (y / yMax)) * h);

                    if (lastXp != -1) {
                        g2.drawLine(lastXp, lastYp, xp, yp);
                    }
                    lastXp = xp;
                    lastYp = yp;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GaussianMonteCarloFrame().setVisible(true));
    }
}