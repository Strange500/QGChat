package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.ReactionDAO;
import fr.univ.lille.s4a021.dao.SubscriptionDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.subscription.SubscriptionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAOSql extends DaoSql implements SubscriptionDAO {
    private final ChannelDAO channelDAO;
    private final UserDAO userDAO;

    public SubscriptionDAOSql(Connection con, ChannelDAO chDAO, UserDAO usrDAO) throws ConfigErrorException {
        super(con);
        this.channelDAO = chDAO;
        this.userDAO = usrDAO;
    }

    @Override
    public List<User> getSubscribedUsers(int cid) throws ChannelNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        String query = "SELECT uid, username, mail, password FROM Utilisateur u JOIN estAbonne e ON u.uid = e.uid WHERE e.cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            return buildUsersFromResultSet(rs);
        } catch (Exception e) {
            throw new DataAccessException("Error while getting subscribed users: " + e.getMessage());
        }
    }

    static List<User> buildUsersFromResultSet(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            int uid = rs.getInt("uid");
            String username = rs.getString("username");
            String mail = rs.getString("mail");
            String password = rs.getString("password");
            users.add(new User(uid, username, mail, password));
        }
        return users;
    }

    @Override
    public void clearSubscriptions(int cid) throws ChannelNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        String query = "DELETE FROM estAbonne WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while clearing subscriptions: " + e.getMessage());
        }
    }

    @Override
    public void unsubscribeUser(int uid, int cid) throws UserNotFoundException, ChannelNotFoundException, SubscriptionNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "DELETE FROM estAbonne WHERE uid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, cid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new SubscriptionNotFoundException("user not subscribed to channel");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while unsubscribing user: " + e.getMessage());
        }
    }

    @Override
    public boolean isSubscribedTo(int uid, int cid) throws UserNotFoundException, ChannelNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "SELECT 1 FROM estAbonne WHERE uid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, cid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking subscription: " + e.getMessage());
        }
    }

    @Override
    public void subscribeUsersTo(Channel ch, List<Integer> Uids) throws ChannelNotFoundException, UserNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(ch.getCid())) {
            throw new ChannelNotFoundException("Channel not found");
        }
        for (int uid : Uids) {
            if (!userDAO.userExists(uid)) {
                throw new UserNotFoundException("User not found");
            }
        }
        String query = "INSERT INTO estAbonne (uid, cid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int uid : Uids) {
                stmt.setInt(1, uid);
                stmt.setInt(2, ch.getCid());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Error while subscribing users: " + e.getMessage());
        }
    }
}
