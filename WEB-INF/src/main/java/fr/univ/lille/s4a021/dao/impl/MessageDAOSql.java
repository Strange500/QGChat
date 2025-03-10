package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dao.ReactionDAO;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageCreationException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageUpdateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOSql extends DaoSql implements MessageDAO {

    public MessageDAOSql(Connection con) throws ConfigErrorException {
        super(con);
    }


    // Création d'un message
    public Message createMessage(String contenu, int senderId, int channelId) throws MessageCreationException, DataAccessException {
        String insertMessageQuery = "INSERT INTO Message (contenu,uid,cid) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertMessageQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, contenu);
            stmt.setInt(2, senderId);
            stmt.setInt(3, channelId);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new MessageCreationException("Message not created");
            }
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int mid = rs.getInt(1);
                return getMessageById(mid);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating message: " + e.getMessage(), e);
        } catch (MessageNotFoundException e) {
            throw new MessageCreationException("Error while creating message: " + e.getMessage(), e);
        }
        throw new MessageCreationException("Error while creating message");
    }

    @Override
    public List<Message> getMessageByChannelId(int cid) throws ChannelNotFoundException, DataAccessException {
        String query = "SELECT mid, contenu, uid, cid, timestamp FROM Message WHERE cid = ? ORDER BY timestamp";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            return buildMessagesFromResultSet(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting messages by channel id: " + e.getMessage(), e);
        }

    }

    private List<Message> buildMessagesFromResultSet(ResultSet rs) throws DataAccessException{
        List<Message> messages = new ArrayList<>();
        try {
            while (rs.next()) {
                int mid = rs.getInt("mid");
                int uid = rs.getInt("uid");
                int cid = rs.getInt("cid");
                String contenu = rs.getString("contenu");
                String timestamp = rs.getString("timestamp");
                messages.add(new Message(mid, contenu, uid, cid, timestamp));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while building messages from result set: " + e.getMessage(), e);
        }
        return messages;
    }

    public Message getMessageById(int mid) throws MessageNotFoundException, DataAccessException {
        String query = "SELECT mid, contenu, uid, cid, timestamp FROM Message WHERE mid = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            ResultSet rs = stmt.executeQuery();
            List<Message>r =  buildMessagesFromResultSet(rs);
            if (!r.isEmpty()) {
                return r.getFirst();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting message by id: " + e.getMessage(), e);
        }
        throw new MessageNotFoundException("Message not found");
    }

    public void deleteMessage(int mid) throws MessageNotFoundException, DataAccessException {
        String query = "DELETE FROM Message WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new MessageNotFoundException("Message not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting message: " + e.getMessage(), e);
        }
    }

    public void updateMessage(int mid, String newContenu) throws MessageNotFoundException, MessageUpdateException, DataAccessException {
        if (newContenu == null || newContenu.isEmpty()) {
            throw new MessageUpdateException("Message content cannot be empty");
        }
        String query = "UPDATE Message SET contenu = ? WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newContenu);
            stmt.setInt(2, mid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new MessageNotFoundException("Message not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating message: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean messageExists(int mid) throws DataAccessException {
        String query = "SELECT 1 FROM Message WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking if message exists: " + e.getMessage(), e);
        }
    }
}
