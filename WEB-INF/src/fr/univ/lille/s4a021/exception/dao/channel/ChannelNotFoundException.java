package fr.univ.lille.s4a021.exception.dao.channel;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;

public class ChannelNotFoundException extends NotFoundException {
    public ChannelNotFoundException(String message, Exception e) {
        super(message, e);
    }
    public ChannelNotFoundException(String message) {
        super(message);
    }
}
