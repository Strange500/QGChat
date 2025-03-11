package fr.univ.lille.s4a021.exception.dao.reaction;

import fr.univ.lille.s4a021.exception.dao.CreationException;

public class ReactionCreationException extends CreationException {
    public ReactionCreationException(String message) {
        super(message);
    }

    public ReactionCreationException(String message, Exception e) {
        super(message, e);
    }
}