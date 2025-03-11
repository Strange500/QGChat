package fr.univ.lille.s4a021.exception.dao.reaction;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;

public class ReactionNotFoundException extends NotFoundException {
    public ReactionNotFoundException(String message) {
        super(message);
    }

    public ReactionNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
