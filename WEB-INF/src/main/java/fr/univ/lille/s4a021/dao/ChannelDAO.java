package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {
    private Connection connection;

    public ChannelDAO(Connection connection) {
        this.connection = connection;
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
                return new Channel(cid, name);
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
}