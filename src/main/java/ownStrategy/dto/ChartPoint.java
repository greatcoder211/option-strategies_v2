package ownStrategy.dto;

public class ChartPoint {
    double price;
    double profit;
    public ChartPoint(double price, double profit) {
        this.price = price;
        this.profit = profit;
    }
    public double getPrice() {
        return price;
    }
    public double getProfit() {
        return profit;
    }
    @Override
    public String toString() {
        return "ChartPoint{" + "price=" + price + ", profit=" + profit + '}';
    }
}
