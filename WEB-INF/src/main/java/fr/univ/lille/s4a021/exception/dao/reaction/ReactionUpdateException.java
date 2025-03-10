package fr.univ.lille.s4a021.exception.dao.reaction;

import fr.univ.lille.s4a021.exception.dao.UpdateException;

public class ReactionUpdateException extends UpdateException {
    public ReactionUpdateException(String message) {
        super(message);
    }

    public ReactionUpdateException(String message, Exception e) {
        super(message, e);
    }
}