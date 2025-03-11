package fr.univ.lille.s4a021.exception.dao.subscription;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;

public class SubscriptionNotFoundException extends NotFoundException {
    public SubscriptionNotFoundException(String message) {
        super(message);
    }

    public SubscriptionNotFoundException(String message, Exception e) {
        super(message, e);
    }
}