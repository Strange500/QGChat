package fr.univ.lille.s4a021.exception.dao;

public class NotFoundException extends DaoException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
