package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection connection;

    public MessageDAO(Connection connection) {
        this.connection = connection;
    }

    // Création d'un message
    public void createMessage(String contenu, int senderId, int channelId) throws SQLException {
        String insertMessageQuery = "INSERT INTO Message (contenu) VALUES (?)";
        String insertAEnvoyerQuery = "INSERT INTO aEnvoyer (uid, mid) VALUES (?, ?)";
        String insertContientQuery = "INSERT INTO contient (cid, mid) VALUES (?, ?)";

        try (PreparedStatement messageStmt = connection.prepareStatement(insertMessageQuery, Statement.RETURN_GENERATED_KEYS)) {
            messageStmt.setString(1, contenu);
            messageStmt.executeUpdate();

            ResultSet generatedKeys = messageStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int mid = generatedKeys.getInt(1);

                try (PreparedStatement aEnvoyerStmt = connection.prepareStatement(insertAEnvoyerQuery)) {
                    aEnvoyerStmt.setInt(1, senderId);
                    aEnvoyerStmt.setInt(2, mid);
                    aEnvoyerStmt.executeUpdate();
                }

                try (PreparedStatement contientStmt = connection.prepareStatement(insertContientQuery)) {
                    contientStmt.setInt(1, channelId);
                    contientStmt.setInt(2, mid);
                    contientStmt.executeUpdate();
                }
            }
        }
    }

    // Récupération d'un message par son ID
    public Message getMessageById(int mid) throws SQLException {
        String query = "SELECT contenu, (SELECT uid FROM aEnvoyer WHERE mid = ?) AS senderId, (SELECT cid FROM contient WHERE mid = ?) AS channelId FROM Message WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, mid);
            stmt.setInt(3, mid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String contenu = rs.getString("contenu");
                int senderId = rs.getInt("senderId");
                int channelId = rs.getInt("channelId");
                return new Message(mid, contenu, senderId, channelId);
            }
        }
        return null; // Retourne null si le message n'est pas trouvé
    }

    // Suppression d'un message par son ID
    public void deleteMessage(int mid) throws SQLException {
        String query = "DELETE FROM Message WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.executeUpdate();
        }
    }

    // Mise à jour du contenu d'un message
    public void updateMessage(int mid, String newContenu) throws SQLException {
        String query = "UPDATE Message SET contenu = ? WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newContenu);
            stmt.setInt(2, mid);
            stmt.executeUpdate();
        }
    }

    // Récupération de tous les messages d'un canal
    public List<Message> getMessagesByChannelId(int channelId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.mid, m.contenu, a.uid AS senderId FROM Message m JOIN contient c ON m.mid = c.mid JOIN aEnvoyer a ON m.mid = a.mid WHERE c.cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, channelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mid = rs.getInt("mid");
                String contenu = rs.getString("contenu");
                int senderId = rs.getInt("senderId");
                messages.add(new Message(mid, contenu, senderId, channelId));
            }
        }
        return messages;
    }
}
