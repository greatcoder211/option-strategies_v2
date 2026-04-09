package ownStrategy.logic.finance;

public class BlackScholes {
    /**
     * Oblicza cenę opcji Call.
     *
     * @param S     Aktualna cena akcji (Spot Price)
     * @param K     Cena wykonania (Strike Price)
     * @param T     Czas do wygaśnięcia w latach (np. 30 dni = 30.0/365.0)
     * @param r     Stopa wolna od ryzyka (np. 0.05 dla 5%)
     * @param sigma Zmienność roczna (Volatility) (np. 0.30 dla 30%)
     * @return Teoretyczna cena opcji Call
     */
    public static double calculateCallPrice(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * Math.pow(sigma, 2)) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        return S * cumulativeDistribution(d1) - K * Math.exp(-r * T) * cumulativeDistribution(d2);
    }

    /**
     * Oblicza cenę opcji Put.
     */
    public static double calculatePutPrice(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * Math.pow(sigma, 2)) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        return K * Math.exp(-r * T) * cumulativeDistribution(-d2) - S * cumulativeDistribution(-d1);
    }
    /**
     * Dystrybuanta standardowego rozkładu normalnego (CND).
     * Używamy aproksymacji, ponieważ Java Math nie ma tej funkcji wbudowanej.
     */
    private static double cumulativeDistribution(double x) {
        double b1 = 0.319381530;
        double b2 = -0.356563782;
        double b3 = 1.781477937;
        double b4 = -1.821255978;
        double b5 = 1.330274429;
        double p = 0.2316419;
        double c = 0.39894228;

        if (x >= 0.0) {
            double t = 1.0 / (1.0 + p * x);
            return (1.0 - c * Math.exp(-x * x / 2.0) * t *
                    (t * (t * (t * (t * b5 + b4) + b3) + b2) + b1));
        } else {
            double t = 1.0 / (1.0 - p * x);
            return (c * Math.exp(-x * x / 2.0) * t *
                    (t * (t * (t * (t * b5 + b4) + b3) + b2) + b1));
        }
    }
}
