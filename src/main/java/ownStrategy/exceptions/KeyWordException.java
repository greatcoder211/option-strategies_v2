package ownStrategy.exceptions;

public class KeyWordException extends RuntimeException {
    public KeyWordException() {
        super("Key word must contain at least 2 characters");
    }
}
