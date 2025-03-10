package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.AdminsDAO;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminCreationException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class AdminsDAOSql extends DaoSql implements AdminsDAO {

    private final UserDAO userDAO;
    private final ChannelDAO channelDAO;

    public AdminsDAOSql(Connection con, UserDAO usrDAO,ChannelDAO chDAO) throws ConfigErrorException {
        super(con);
        this.userDAO = usrDAO;
        this.channelDAO = chDAO;

    }


    @Override
    public void setAdmin(int cid, int uid) throws UserNotFoundException, ChannelNotFoundException, AdminCreationException, DataAccessException {
        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }

        String query = "INSERT INTO isAdmin (cid, uid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.setInt(2, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new DataAccessException("Error while setting admin");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AdminCreationException("Error while setting admin: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new DataAccessException("Error while setting admin: " + e.getMessage(), e);
        }

    }

    @Override
    public void clearAdmins(int cid) throws ChannelNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        String query = "DELETE FROM isAdmin WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while clearing admins: " + e.getMessage(), e);
        }

    }

    @Override
    public void setAdmins(int cid, List<Integer> users) throws UserNotFoundException, ChannelNotFoundException, AdminCreationException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }
        if (users == null || users.isEmpty()) {
            return;
        } else if (!userDAO.userAllExists(users)) {
            throw new UserNotFoundException("One or more users does not exist");
        }


        StringBuilder query = new StringBuilder("INSERT INTO isAdmin (cid, uid) VALUES ");
        for (int i = 0; i < users.size(); i++) {
            query.append("(?, ?)");
            if (i < users.size() - 1) {
                query.append(", ");
            }
        }
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (int uid : users) {
                stmt.setInt(i++, cid);
                stmt.setInt(i++, uid);
            }
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AdminCreationException("Error while setting admins: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new DataAccessException("Error while setting admins: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean userIsAdmin(int uid, int cid) throws UserNotFoundException, ChannelNotFoundException, DataAccessException {

        if (!userDAO.userExists(uid)) {
            throw new UserNotFoundException("User not found");
        }
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }

        String query = "SELECT * FROM isAdmin WHERE cid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.setInt(2, uid);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking if user is admin: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAdmins(int cid) throws ChannelNotFoundException, DataAccessException {
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }

        List<User> admins = new ArrayList<>();
        String query = "SELECT uid, username, mail, password FROM Utilisateur WHERE uid IN (SELECT uid FROM isAdmin WHERE cid = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ((UserDAOSql) userDAO).buildUsers(admins, stmt.executeQuery());
            return admins;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting admins: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeAdmin(int uid, int cid) throws AdminNotFoundException, ChannelNotFoundException, DataAccessException {
        if (userDAO.userExists(uid)) {
            throw new AdminNotFoundException("Admin not found");
        }
        if (!channelDAO.channelExists(cid)) {
            throw new ChannelNotFoundException("Channel not found");
        }

        String query = "DELETE FROM isAdmin WHERE cid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.setInt(2, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new AdminNotFoundException("Admin not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while removing admin: " + e.getMessage(), e);
        }
    }


}
