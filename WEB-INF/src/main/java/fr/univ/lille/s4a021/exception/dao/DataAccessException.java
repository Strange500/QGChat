package fr.univ.lille.s4a021.exception.dao;

public class DataAccessException extends DaoException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Exception e) {
        super(message, e);
    }
}
