package ownStrategy.exceptions;

public class QuantityException extends RuntimeException {
    public QuantityException() {
        super("Wrong quantity. Enter a positive integer");
    }
}
