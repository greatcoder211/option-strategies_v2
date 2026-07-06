package ownStrategy.exception;

public class APILimitExceededException extends RuntimeException {
    public APILimitExceededException(String message) {
        super("API Limit exceeded" + message);
    }
}
