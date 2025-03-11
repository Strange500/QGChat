package fr.univ.lille.s4a021.exception.dao.channel;

import fr.univ.lille.s4a021.exception.dao.UpdateException;

public class ChannelUpdateException extends UpdateException {
    public ChannelUpdateException(String message) {
        super(message);
    }

    public ChannelUpdateException(String message, Exception e) {
        super(message, e);
    }
}