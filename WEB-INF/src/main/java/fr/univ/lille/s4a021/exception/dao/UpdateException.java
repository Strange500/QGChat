package fr.univ.lille.s4a021.exception.dao;

public class UpdateException extends DaoException {
    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
