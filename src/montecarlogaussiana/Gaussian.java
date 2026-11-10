package montecarlogaussiana;

public class Gaussian {
    private double mean;      // mu (μ)
    private double stdDev;    // sigma (σ)

    public Gaussian(double mean, double stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }

    // Probability Density Function (PDF) of a Gaussian distribution
    public double evaluate(double x) {
        double coefficient = 1.0 / (stdDev * Math.sqrt(2 * Math.PI));
        double exponent = -Math.pow(x - mean, 2) / (2 * Math.pow(stdDev, 2));
        return coefficient * Math.exp(exponent);
    }
}
