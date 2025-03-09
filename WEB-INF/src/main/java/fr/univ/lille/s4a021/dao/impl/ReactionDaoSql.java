package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dao.ReactionDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionCreationException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionUpdateException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReactionDaoSql extends DaoSql implements ReactionDAO {

    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    public ReactionDaoSql(Connection con) throws ConfigErrorException {
        super(con);
        this.messageDAO = Config.getConfig().getMessageDAO();
        this.userDAO = Config.getConfig().getUserDAO();

    }

    @Override
    public Map<Reaction, Set<Integer>> getReactionsForMessage(int mid) throws MessageNotFoundException, DataAccessException {
        if (!messageDAO.messageExists(mid)) {
            throw new MessageNotFoundException("Message not found");
        }
        String query = "SELECT emoji, uid FROM likes WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            ResultSet rs = stmt.executeQuery();
            Map<Reaction, Set<Integer>> reactions = new HashMap<>();
            while (rs.next()) {
                Reaction r = Reaction.getReactionFromEmoji(rs.getString("emoji"));
                int uid = rs.getInt("uid");
                if (!reactions.containsKey(r)) {
                    reactions.put(r, new HashSet<>());
                }
                reactions.get(r).add(uid);
            }
            return reactions;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting reactions: " + e.getMessage());
        }
    }

    @Override
    public Reaction getUserReactionForMessage(int mid, int uid) throws MessageNotFoundException, UserNotFoundException, ReactionNotFoundException, DataAccessException {
        if (!messageDAO.messageExists(mid)) {
            throw new MessageNotFoundException("Message not found");
        }
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "SELECT emoji FROM likes WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Reaction.getReactionFromEmoji(rs.getString("emoji"));
            }
            throw new ReactionNotFoundException("Reaction not found");
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting user reaction: " + e.getMessage());
        }
    }

    @Override
    public void updateUserReactionForMessage(int mid, int uid, Reaction emoji) throws MessageNotFoundException, UserNotFoundException, ReactionUpdateException, ReactionNotFoundException, DataAccessException {
        if (!messageDAO.messageExists(mid)) {
            throw new MessageNotFoundException("Message not found");
        }
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        if (emoji == Reaction.EMPTY) {
            throw new ReactionUpdateException("Cannot update reaction to empty");
        }
        String query = "UPDATE likes SET emoji = ? WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, emoji.getEmoji());
            stmt.setInt(2, mid);
            stmt.setInt(3, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new ReactionNotFoundException("Reaction not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating user reaction: " + e.getMessage());
        }
    }

    @Override
    public void createReactionForMessage(int mid, int uid, Reaction emoji) throws MessageNotFoundException, UserNotFoundException, ReactionCreationException, DataAccessException {
        if (!messageDAO.messageExists(mid)) {
            throw new MessageNotFoundException("Message not found");
        }
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        if (emoji == Reaction.EMPTY) {
            throw new ReactionCreationException("Cannot create reaction empty");
        }
        String query = "INSERT INTO likes (mid, uid, emoji) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            stmt.setString(3, emoji.getEmoji());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating reaction: " + e.getMessage());
        }
    }

    @Override
    public void deleteReactionForMessage(int mid, int uid) throws MessageNotFoundException, UserNotFoundException, ReactionNotFoundException, DataAccessException {
        if (!messageDAO.messageExists(mid)) {
            throw new MessageNotFoundException("Message not found");
        }
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "DELETE FROM likes WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new ReactionNotFoundException("Reaction not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting reaction: " + e.getMessage());
        }
    }


}
