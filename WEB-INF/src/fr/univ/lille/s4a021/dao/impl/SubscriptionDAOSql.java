package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.SubscriptionDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
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

    public SubscriptionDAOSql(Connection con, ChannelDAO chDAO, UserDAO usrDAO) {
        super(con);
        this.channelDAO = chDAO;
        this.userDAO = usrDAO;
    }

    @Override
    public List<User> getSubscribedUsers(int cid) throws ChannelNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        String query = "SELECT u.uid, u.username, u.mail, u.password FROM Utilisateur u JOIN estAbonne e ON u.uid = e.uid WHERE e.cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            return buildUsersFromResultSet(rs);
        } catch (Exception e) {
            throw new DataAccessException("Error while getting subscribed users: " + e.getMessage(), e);
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
            throw new DataAccessException("Error while clearing subscriptions: " + e.getMessage(), e);
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
            throw new DataAccessException("Error while unsubscribing user: " + e.getMessage(), e);
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
            throw new DataAccessException("Error while checking subscription: " + e.getMessage(), e);
        }
    }

    @Override
    public void subscribeUsersTo(Channel ch, List<Integer> Uids) throws ChannelNotFoundException, UserNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(ch.getCid())) {
            throw new ChannelNotFoundException("Channel not found");
        }
        if (!userDAO.userAllExists(Uids)) {
            throw new UserNotFoundException("One or more users not found");
        }
        StringBuilder query = new StringBuilder("INSERT INTO estAbonne (uid, cid) VALUES ");
        for (int i = 0; i < Uids.size(); i++) {
            query.append("(?, ?)");
            if (i < Uids.size() - 1) {
                query.append(", ");
            }
        }
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (int uid : Uids) {
                stmt.setInt(i++, uid);
                stmt.setInt(i++, ch.getCid());
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while subscribing users: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Channel> getSubscribedChannels(int uid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "SELECT c.cid, c.name, c.minuteBeforeExpiration FROM Channel c JOIN estAbonne e ON c.cid = e.cid WHERE e.uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            List<Channel> channels = new ArrayList<>();
            while (rs.next()) {
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                int minuteBeforeExpiration = rs.getInt("minuteBeforeExpiration");
                channels.add(new Channel(cid, name, minuteBeforeExpiration));
            }
            return channels;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting subscribed channels: " + e.getMessage(), e);
        }
    }
}
