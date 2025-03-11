package fr.univ.lille.s4a021.exception.dao.message;

import fr.univ.lille.s4a021.exception.dao.UpdateException;

public class MessageUpdateException extends UpdateException {
    public MessageUpdateException(String message) {
        super(message);
    }

    public MessageUpdateException(String message, Exception e) {
        super(message, e);
    }
}
