package ownStrategy.exceptions;

public class APILimitExceededException extends RuntimeException {
    public APILimitExceededException(String message) {
        super("API Limit exceeded" + message);
    }
}
