package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.FriendDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static fr.univ.lille.s4a021.dao.impl.SubscriptionDAOSql.buildUsersFromResultSet;

public class FriendDAOSql extends DaoSql implements FriendDAO {

    private UserDAO userDAO;

    public FriendDAOSql(Connection con) throws ConfigErrorException {
        super(con);
        this.userDAO = Config.getConfig().getUserDAO();
    }

    @Override
    public void addFriend(int uid, int friendId) throws UserNotFoundException, DataAccessException {
        if (isFriend(uid, friendId)) {
            return;
        }
        int cid = createFriendChannel();
        String query = "INSERT INTO isFriend (uid1, uid2, cid) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, friendId);
            stmt.setInt(3, cid);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            deleteFriendChannel(uid, friendId);
            throw new UserNotFoundException("one user does not exist");
        } catch (SQLException e) {
            throw new DataAccessException("Error while adding friend: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFriend(int uid, int friendId) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(uid) || !userDAO.userExists(friendId)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "SELECT 1 FROM isFriend WHERE uid1 = ? AND uid2 = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, friendId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking friendship: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pair<User, Channel>> getFriendChannels(int uid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        List<Pair<User, Channel>> friends = new ArrayList<>();
        String query = """
                SELECT *
                FROM isFriend
                JOIN Channel C ON C.cid = isFriend.cid
                WHERE uid1 = ? OR uid2 = ?;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, uid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int friendId = rs.getInt("uid1") == uid ? rs.getInt("uid2") : rs.getInt("uid1");
                User friend = userDAO.getUserById(friendId);
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                int minuteBeforeExpiration = rs.getInt("minuteBeforeExpiration");
                Channel channel = new Channel(cid, name, minuteBeforeExpiration);
                friends.add(new Pair<>(friend, channel));
            }
            return friends;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting friend channels: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getFriendRequests(int uid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        List<User> friends = new ArrayList<>();
        String query = """
                SELECT u.uid, u.username, u.mail, u.password
                FROM Utilisateur u
                JOIN friendrequest f ON u.uid = f.senderuid
                WHERE f.receiveruid = ?;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            return buildUsersFromResultSet(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting friend requests: " + e.getMessage(), e);
        }
    }

    @Override
    public void acceptFriendRequest(int senderuid, int receiveruid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(senderuid) || !userDAO.userExists(receiveruid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "DELETE FROM friendrequest WHERE senderuid = ? AND receiveruid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, senderuid);
            stmt.setInt(2, receiveruid);
            int r = stmt.executeUpdate();
            if (r == 1) {
                addFriend(senderuid, receiveruid);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while accepting friend request: " + e.getMessage(), e);
        }

    }

    @Override
    public void declineFriendRequest(int senderid, int receiverid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(senderid) || !userDAO.userExists(receiverid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "DELETE FROM friendrequest WHERE senderuid = ? AND receiveruid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            System.out.println(stmt);
            stmt.setInt(1, senderid);
            stmt.setInt(2, receiverid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while declining friend request: " + e.getMessage(), e);
        }

    }

    @Override
    public void sendFriendRequest(int senderuid, int receiveruid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(senderuid) || !userDAO.userExists(receiveruid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "INSERT INTO friendrequest (senderuid, receiveruid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, senderuid);
            stmt.setInt(2, receiveruid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while sending friend request: " + e.getMessage(), e);
        }

    }

    @Override
    public List<User> getNotFriends(int uid) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        List<User> notFriends = new ArrayList<>();
        String query = """
                SELECT u.uid, u.username, u.mail, u.password
                FROM Utilisateur u
                WHERE u.uid NOT IN (
                    SELECT u.uid
                    FROM Utilisateur u
                    JOIN isFriend f ON u.uid = f.uid2
                    WHERE f.uid1 = ?
                    UNION 
                    SELECT u.uid
                    FROM Utilisateur u
                    JOIN isFriend f ON u.uid = f.uid1
                    WHERE f.uid2 = ?
                
                )
                AND u.uid NOT IN (
                    SELECT u.uid
                    FROM Utilisateur u
                    JOIN friendrequest f ON u.uid = f.senderuid
                    WHERE f.receiveruid = ?
                    UNION 
                    SELECT u.uid
                    FROM Utilisateur u
                    JOIN friendrequest f ON u.uid = f.receiveruid
                    WHERE f.senderuid = ?
                )
                AND u.uid != ?;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, uid);
            stmt.setInt(3, uid);
            stmt.setInt(4, uid);
            stmt.setInt(5, uid);

            ResultSet rs = stmt.executeQuery();
            return buildUsersFromResultSet(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting not friends: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFriendChannel(int uid, int channelId) throws UserNotFoundException, DataAccessException {
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        String query = "SELECT 1 FROM isFriend WHERE (uid1 = ? OR uid2 = ?) AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, uid);
            stmt.setInt(3, channelId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking friend channel: " + e.getMessage(), e);
        }

    }

    @Override
    public User getFriendForChannel(int channelId, int myUid) throws ChannelNotFoundException, DataAccessException {
        String query = """
                WITH t(cid,uid) AS (
                    SELECT cid, uid1 FROM isFriend
                    UNION
                    SELECT cid, uid2 FROM isFriend
                )
                SELECT u.uid, u.username, u.mail, u.password
                FROM Utilisateur u
                JOIN t ON u.uid = t.uid
                WHERE t.cid = ? AND u.uid != ?;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, channelId);
            stmt.setInt(2, myUid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("uid"), rs.getString("username"), rs.getString("mail"), rs.getString("password"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting friend for channel: " + e.getMessage(), e);
        }
        throw new ChannelNotFoundException("Channel not found");
    }

    private void deleteFriendChannel(int uid, int friendId) throws UserNotFoundException, DataAccessException {
        String query = "DELETE FROM Channel WHERE cid = (SELECT cid FROM isFriend WHERE uid1 = ? AND uid2 = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, friendId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting friend channel: " + e.getMessage(), e);
        }

    }

    private int createFriendChannel() throws UserNotFoundException, DataAccessException {
        String query = "INSERT INTO Channel (minuteBeforeExpiration, name) VALUES (-1, 'no name')";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (!rs.next()) {
                throw new DataAccessException("Error while creating friend channel");
            }
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating friend channel: " + e.getMessage(), e);
        }
    }
}
