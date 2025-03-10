package fr.univ.lille.s4a021.exception.dao.user;

import fr.univ.lille.s4a021.exception.dao.CreationException;

public class UserCreationException extends CreationException {
    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(String message, Exception e) {
        super(message, e);
    }
}
