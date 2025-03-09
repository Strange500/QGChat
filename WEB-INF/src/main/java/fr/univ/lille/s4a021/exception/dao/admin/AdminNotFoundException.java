package fr.univ.lille.s4a021.exception.dao.admin;

import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

public class AdminNotFoundException extends UserNotFoundException {
    public AdminNotFoundException(String message) {
        super(message);
    }
}
