package ownStrategy.exception;

public class InvalidAPITokenException extends RuntimeException {
    public InvalidAPITokenException(String message) {
        super(message);
    }
}
