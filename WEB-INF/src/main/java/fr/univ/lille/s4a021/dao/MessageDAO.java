package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.ImgMessage;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageCreationException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageUpdateException;
import fr.univ.lille.s4a021.util.Pair;

import java.util.ArrayList;
import java.util.List;

public interface MessageDAO {


    public Message createMessage(String contenu, int senderId, int channelId) throws MessageCreationException, DataAccessException;

    public List<Message> getMessageByChannelId(int cid) throws ChannelNotFoundException, DataAccessException;

    public Message getMessageById(int mid) throws MessageNotFoundException, DataAccessException;

    public void deleteMessage(int mid) throws MessageNotFoundException, DataAccessException;

    public void updateMessage(int mid, String newContent) throws MessageNotFoundException, MessageUpdateException, DataAccessException;

    public default Pair<List<ImgMessage>,List<Message>> separateImgFromMessage(List<Message> listMessage) {
        List<Message> messages = new ArrayList<>(listMessage);
        List<Message> lsToRemove = new ArrayList<>();
        List<ImgMessage> imgMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getContenu().startsWith("img:")) {
                String[] parts = message.getContenu().split(":");
                String content = parts[1];
                imgMessages.add(new ImgMessage(message, content));
                lsToRemove.add(message);
            }
        }
        messages.removeAll(lsToRemove);
        return new Pair<>(imgMessages, messages);
    }

    public boolean messageExists(int mid) throws DataAccessException;
}
