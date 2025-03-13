package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.dto.MsgType;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageCreationException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageUpdateException;

import java.util.List;

public interface MessageDAO {


    Message createMessage(String contenu, int senderId, int channelId, MsgType type) throws MessageCreationException, DataAccessException;

    List<Message> getMessageByChannelId(int cid) throws ChannelNotFoundException, DataAccessException;

    Message getMessageById(int mid) throws MessageNotFoundException, DataAccessException;

    void deleteMessage(int mid) throws MessageNotFoundException, DataAccessException;

    void updateMessage(int mid, String newContent) throws MessageNotFoundException, MessageUpdateException, DataAccessException;


    boolean messageExists(int mid) throws DataAccessException;

    void deleteExpiredMessages() throws DataAccessException;

}
