package ownStrategy.dto;
public record ChartPoint(double price, double profit){
    @Override
    public String toString() {
        return "ChartPoint{" + "strikePrice=" + price + ", profit=" + profit + '}';
    }
}
