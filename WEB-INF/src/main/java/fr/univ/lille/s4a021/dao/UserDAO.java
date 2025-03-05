package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Connect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() throws SQLException {
        this.connection = Connect.getConnection();
    }

    // Création d'un utilisateur
    public int createUser(String username, String mail, String password) throws SQLException {
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
            return -1;
        }
    }

    // Authentification d'un utilisateur
    public boolean authenticateUser(String mail, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM Utilisateur WHERE mail = ? AND password = MD5(?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mail);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean estAbonne(int uid, int cid) throws SQLException {
        String query = "SELECT COUNT(*) FROM estAbonne WHERE uid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, cid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Récupération d'un utilisateur par son mail, return -1 si pas de User trouvé
    public int getUserIdByMail(String mail) throws SQLException {
        String query = "SELECT uid FROM Utilisateur WHERE mail = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("uid");
            }
        }
        return -1;
    }

    // Récupération des informations d'un utilisateur par son ID
    public User getUserById(int uid) throws SQLException {
        String query = "SELECT username, mail, password FROM Utilisateur WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String mail = rs.getString("mail");
                String password = rs.getString("password");
                return new User(uid, username, mail, password);
            }
        }
        return null; // Retourne null si l'utilisateur n'est pas trouvé
    }

    // Suppression d'un utilisateur par son ID
    public void deleteUser(int uid) throws SQLException {
        String query = "DELETE FROM Utilisateur WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.executeUpdate();
        }
    }

    // Mise à jour des informations d'un utilisateur
    public void updateUser(int uid, String newUsername, String newMail) throws SQLException {
        String query = "UPDATE Utilisateur SET username = ?, mail = ? WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, newMail);
            stmt.setInt(3, uid);
            stmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String query = "SELECT * FROM Utilisateur";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
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
    }


    public void setUserProfilePicture(String base64Image, int uid) throws SQLException {
        String query = "UPDATE Utilisateur SET profile_picture = ? WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, base64Image);
            stmt.setInt(2, uid);
            stmt.executeUpdate();
        }
    }

    public String getUserProfilePicture(int uid) throws SQLException {
        String query = "SELECT profile_picture FROM Utilisateur WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("profile_picture");
            }
        }
        return null;
    }
}