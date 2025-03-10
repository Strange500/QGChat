package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.util.List;

public interface SentDAO {

    public List<Message> getChannelMessages(int cid) throws ChannelNotFoundException, DataAccessException;



}
