package fr.univ.lille.s4a021.exception.dao.message;

import fr.univ.lille.s4a021.exception.dao.CreationException;

public class MessageCreationException extends CreationException {
    public MessageCreationException(String message) {
        super(message);
    }

    public MessageCreationException(String message, Exception e) {
        super(message, e);
    }
}
