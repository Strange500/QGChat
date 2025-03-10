package fr.univ.lille.s4a021.exception;

public class ConfigErrorException extends MyDiscordException {
    public ConfigErrorException(String message) {
        super(message);
    }

    public ConfigErrorException(String message, Exception e) {
        super(message, e);
    }
}
