package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.model.bdd.Connect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {
    private Connection connection;

    public ChannelDAO() throws SQLException {
        this.connection = Connect.getConnection();
    }

    // Création d'un canal
    public void createChannel(String name) throws SQLException {
        String query = "INSERT INTO Channel (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    // Récupération d'un canal par son ID
    public Channel getChannelById(int cid) throws SQLException {
        String query = "SELECT name FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                Channel ch = new Channel(cid, name);
                ch.setMessages(new MessageDAO().getMessagesByChannelId(cid));
                return ch;
            }
        }
        return null; // Retourne null si le canal n'est pas trouvé
    }

    // Suppression d'un canal par son ID
    public void deleteChannel(int cid) throws SQLException {
        String query = "DELETE FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.executeUpdate();
        }
    }

    // Mise à jour des informations d'un canal
    public void updateChannel(int cid, String newName) throws SQLException {
        String query = "UPDATE Channel SET name = ? WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setInt(2, cid);
            stmt.executeUpdate();
        }
    }

    // Récupération de tous les canaux
    public List<Channel> getAllChannels() throws SQLException {
        List<Channel> channels = new ArrayList<>();
        String query = "SELECT * FROM Channel";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                channels.add(new Channel(cid, name));
            }
        }
        return channels;
    }

    public static void main(String[] args) throws SQLException {
        ChannelDAO dao = new ChannelDAO();
        List<Message> messages = dao.getChannelById(2).getMessages();
        for (Message message : messages) {
            System.out.println(message);
        }
    }
}