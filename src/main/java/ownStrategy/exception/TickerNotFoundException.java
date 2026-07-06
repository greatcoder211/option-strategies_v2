package ownStrategy.exception;

public class TickerNotFoundException extends RuntimeException {
    public TickerNotFoundException(String ticker) {
        super("No company found for ticker " + ticker);
    }
}
