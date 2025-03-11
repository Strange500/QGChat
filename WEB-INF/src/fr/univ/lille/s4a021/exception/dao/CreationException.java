package fr.univ.lille.s4a021.exception.dao;

public class CreationException extends DaoException {
    public CreationException(String message) {
        super(message);
    }

    public CreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
