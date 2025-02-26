package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Connect;

import java.sql.*;

public class UserDAO {
    private Connection connection;

    public UserDAO() throws SQLException {
        this.connection = Connect.getConnection();
    }

    // Création d'un utilisateur
    public void createUser(String username, String mail, String password) throws SQLException {
        String query = "INSERT INTO Utilisateur (username, mail, password) VALUES (?, ?, MD5(?))";
        System.out.println("Creating user: " + username + " " + mail + " " + password);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, mail);
            stmt.setString(3, password);
            stmt.executeUpdate();
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
    public void updateUser(int uid, String newUsername, String newMail, String newPassword) throws SQLException {
        String query = "UPDATE Utilisateur SET username = ?, mail = ?, password = MD5(?) WHERE uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, newMail);
            stmt.setString(3, newPassword);
            stmt.setInt(4, uid);
            stmt.executeUpdate();
        }
    }
}