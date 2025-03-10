package fr.univ.lille.s4a021.exception.dao.user;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
