package fr.univ.lille.s4a021.exception;

public class MyDiscordException extends Exception {
    public MyDiscordException(String message) {
        super(message);
    }

    public MyDiscordException(String message, Throwable e) {
        super(message, e);
    }
}
