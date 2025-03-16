package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.user.UserCreationException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserUpdateException;

import java.sql.*;
import java.util.*;

public class UserDAOSql extends DaoSql implements UserDAO {

    public UserDAOSql(Connection con) {
        super(con);
    }

    /**
     * User creation
     *
     * @param username the username of the user
     * @param mail    the mail of the user
     * @param password the password of the user
     * @return the id of the created user
     * @throws UserCreationException if the username, mail or password is null
     * @throws DataAccessException  if an error occurs while creating the user
     */
    public int createUser(String username, String mail, String password) throws UserCreationException, DataAccessException {
        if (username == null || mail == null || password == null) {
            throw new UserCreationException("Username, mail and password cannot be null");
        }
        String query = "INSERT INTO Utilisateur (username, mail, password) VALUES (?, ?, MD5(?))";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, mail);
            stmt.setString(3, password);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new UserCreationException("Error while creating user");
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating user: " + e.getMessage(), e);
        }
    }

    public boolean authenticateUser(String mail, String password) throws DataAccessException {
        if (mail == null || password == null || mail.isEmpty() || password.isEmpty()) {
            return false;
        }
        String query = "SELECT COUNT(*) FROM Utilisateur WHERE mail = ? AND password = MD5(?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mail);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while authenticating user: " + e.getMessage(), e);
        }
        return false;
    }

    public User getUserByMail(String mail) throws UserNotFoundException, DataAccessException {
        if (mail == null || mail.isEmpty()) {
            throw new UserNotFoundException("Mail cannot be empty");
        }
        String query = "SELECT uid, username, mail, password FROM Utilisateur WHERE mail = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("uid"), rs.getString("username"), rs.getString("mail"), rs.getString("password"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting user by mail: " + e.getMessage(), e);
        }
        throw new UserNotFoundException("User not found");
        }



    public List<User> getUserByIds(Collection<Integer> uids) throws DataAccessException, UserNotFoundException {
        if (uids.isEmpty()) {
            return new ArrayList<>();
        }
        List<User> users = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT uid, username, mail, password FROM Utilisateur WHERE uid IN (");
        for (int i = 0; i < uids.size(); i++) {
            query.append("?");
            if (i < uids.size() - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (int uid : uids) {
                stmt.setInt(i++, uid);
            }
            ResultSet rs = stmt.executeQuery();
            buildUsers(users, rs);
            if (users.size() != uids.size()) {
                throw new UserNotFoundException("One or more users not found");
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting users by ids: " + e.getMessage(), e);
        }
    }

    public User getUserById(int uid) throws UserNotFoundException, DataAccessException {
        List<User> users = getUserByIds(List.of(uid));
        if (users.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return users.getFirst();
    }

    void buildUsers(List<User> users, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int uid = rs.getInt("uid");
            String username = rs.getString("username");
            String mail = rs.getString("mail");
            String password = rs.getString("password");
            users.add(new User(uid, username, mail, password));
        }
    }

    public void deleteUser(int uid) throws UserNotFoundException, DataAccessException {
        String query = "DELETE FROM Utilisateur WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new UserNotFoundException("User not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting user: " + e.getMessage(), e);
        }

    }

    public void updateUser(int uid, String newUsername, String newMail) throws UserNotFoundException, UserUpdateException, DataAccessException {
        if (newUsername == null || newMail == null || newUsername.isEmpty() || newMail.isEmpty()) {
            throw new UserUpdateException("Username and mail cannot be empty");
        }
        String query = "UPDATE Utilisateur SET username = ?, mail = ? WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, newMail);
            stmt.setInt(3, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new UserNotFoundException("User not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating user: " + e.getMessage(), e);
        }
    }

    public List<User> getAllUsers() throws DataAccessException {
        String query = "SELECT  uid, username, mail, password FROM Utilisateur";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            List<User> users = new ArrayList<>();
            buildUsers(users, rs);
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting all users: " + e.getMessage(), e);
        }
    }


    public void setUserProfilePicture(String base64Image, int uid) throws UserNotFoundException, UserUpdateException, DataAccessException {
        if (base64Image == null || base64Image.isEmpty()) {
            throw new UserUpdateException("Profile picture cannot be empty");
        }
        String query = "UPDATE Utilisateur SET profile_picture = ? WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, base64Image);
            stmt.setInt(2, uid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new UserNotFoundException("User not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating user profile picture: " + e.getMessage(), e);
        }

    }

    public String getUserProfilePicture(int uid) throws UserNotFoundException, DataAccessException {
        String query = "SELECT profile_picture FROM Utilisateur WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_picture");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting user profile picture: " + e.getMessage(), e);
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public Map<Integer, String> getUserProfilePictures(Collection<Integer> uids) throws UserNotFoundException, DataAccessException {
        if (uids.isEmpty()) {
            return new HashMap<>();
        }
        Map<Integer, String> profilePictures = new HashMap<>();
        StringBuilder query = new StringBuilder("SELECT uid, profile_picture FROM Utilisateur WHERE uid IN (");
        for (int i = 0; i < uids.size(); i++) {
            query.append("?");
            if (i < uids.size() - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (int uid : uids) {
                stmt.setInt(i++, uid);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                profilePictures.put(rs.getInt("uid"), rs.getString("profile_picture"));
            }
            if (profilePictures.size() != uids.size()) {
                throw new UserNotFoundException("One or more users not found");
            }
            return profilePictures;
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting user profile pictures: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean userExists(int uid) throws DataAccessException {
        String query = "SELECT COUNT(*) FROM Utilisateur WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking if user exists: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean userAllExists(Collection<Integer> uids) throws DataAccessException {
        Set<Integer> uniqueUids = Set.copyOf(uids);
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM Utilisateur WHERE uid IN (");
        for (int i = 0; i < uniqueUids.size(); i++) {
            query.append("?");
            if (i < uniqueUids.size() - 1) {
                query.append(", ");
            }
        }
        query.append(")");
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int i = 1;
            for (int uid : uniqueUids) {
                stmt.setInt(i++, uid);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == uniqueUids.size();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking if users exist: " + e.getMessage(), e);
        }
        return false;
    }
}