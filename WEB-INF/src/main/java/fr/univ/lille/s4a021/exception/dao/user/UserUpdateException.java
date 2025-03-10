package fr.univ.lille.s4a021.exception.dao.user;

import fr.univ.lille.s4a021.exception.dao.UpdateException;

public class UserUpdateException extends UpdateException {
    public UserUpdateException(String message) {
        super(message);
    }

    public UserUpdateException(String message, Exception e) {
        super(message, e);
    }
}