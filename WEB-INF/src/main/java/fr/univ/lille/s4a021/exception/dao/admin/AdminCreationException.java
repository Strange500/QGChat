package fr.univ.lille.s4a021.exception.dao.admin;

import fr.univ.lille.s4a021.exception.dao.CreationException;

public class AdminCreationException extends CreationException {
    public AdminCreationException(String message, Exception e) {
        super(message, e);
    }
    public AdminCreationException(String message) {
        super(message);
    }
}
