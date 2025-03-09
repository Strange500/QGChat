package fr.univ.lille.s4a021.exception.dao.reaction;

public class ReactionNotFoundException extends RuntimeException {
    public ReactionNotFoundException(String message) {
        super(message);
    }
}
