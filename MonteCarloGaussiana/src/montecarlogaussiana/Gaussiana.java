package montecarlogaussiana;

public class Gaussiana {
    private double mu; // media
    private double sigma; // deviazione standard
    
    public Gaussiana(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

    public double funzione(double x) {
        return (1 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(-((x - mu) * (x - mu)) / (2 * sigma * sigma));
    }
}
