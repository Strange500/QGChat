package fr.univ.lille.s4a021.exception.dao.message;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;

public class MessageNotFoundException extends NotFoundException {
    public MessageNotFoundException(String message) {
        super(message);
    }

    public MessageNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
