package fr.univ.lille.s4a021.exception.dao;

import fr.univ.lille.s4a021.exception.MyDiscordException;

public class DaoException extends MyDiscordException {
    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
