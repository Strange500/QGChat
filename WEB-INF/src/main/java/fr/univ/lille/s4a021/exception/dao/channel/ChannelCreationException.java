package fr.univ.lille.s4a021.exception.dao.channel;

import fr.univ.lille.s4a021.exception.dao.CreationException;

public class ChannelCreationException extends CreationException {
    public ChannelCreationException(String message, Exception e) {
        super(message, e);
    }
    public ChannelCreationException(String message) {
        super(message);
    }
}
