package fr.univ.lille.s4a021.exception.dao.admin;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

public class AdminNotFoundException extends NotFoundException {
    public AdminNotFoundException(String message, Exception e) {
        super(message, e);
    }

    public AdminNotFoundException(String message) {
        super(message);
    }
}
