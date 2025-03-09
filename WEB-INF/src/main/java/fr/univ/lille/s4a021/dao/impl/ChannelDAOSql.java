package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelUpdateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAOSql extends DaoSql implements ChannelDAO {
    private final UserDAO userDAO;
    private final MessageDAO messageDAO;

    public ChannelDAOSql(Connection con) throws SQLException, ConfigErrorException {
        super(con);
        this.userDAO = Config.getConfig().getUserDAO();
        this.messageDAO = Config.getConfig().getMessageDAO();

    }


    public Channel createChannel(String name) throws ChannelCreationException, DataAccessException {
        if (name == null || name.isEmpty()) {
            throw new ChannelCreationException("Channel name cannot be empty");
        }
        String query = "INSERT INTO Channel (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (!rs.next()) {
                throw new ChannelCreationException("Error while creating channel");
            }
            try {
                return getChannelById(rs.getInt(1));
            } catch (ChannelNotFoundException e) {
                throw new ChannelCreationException("Error while creating channel: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating channel: " + e.getMessage());
        }

    }

    public Channel getChannelByName(String name) throws ChannelNotFoundException, DataAccessException {
        String query = "SELECT cid FROM Channel WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int cid = rs.getInt("cid");
                return new Channel(cid, name);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting channel by name: " + e.getMessage());
        }
        throw new ChannelNotFoundException("Channel not found");
    }

    public Channel getChannelById(int cid) throws ChannelNotFoundException, DataAccessException {
        String query = "SELECT name FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                return new Channel(cid, name);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting channel by id: " + e.getMessage());
        }
        throw new ChannelNotFoundException("Channel not found");
    }

    // Suppression d'un canal par son ID
    public void deleteChannel(int cid) throws ChannelNotFoundException, DataAccessException {
        String query = "DELETE FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new ChannelNotFoundException("Channel not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting channel: " + e.getMessage());
        }

    }

    // Mise à jour des informations d'un canal
    public void updateChannel(int cid, String newName) throws ChannelNotFoundException, ChannelUpdateException, DataAccessException {
        if (newName == null || newName.isEmpty()) {
            throw new ChannelUpdateException("Channel name cannot be empty");
        }
        String query = "UPDATE Channel SET name = ? WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setInt(2, cid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new ChannelNotFoundException("Channel not found");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error while updating channel: " + e.getMessage());
        }
    }

    // Récupération de tous les canaux
    public List<Channel> getAllChannels() throws DataAccessException {
        List<Channel> channels = new ArrayList<>();
        String query = "SELECT * FROM Channel";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                channels.add(new Channel(cid, name));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting all channels: " + e.getMessage());
        }
        return channels;
    }

    @Override
    public boolean channelExists(int cid) throws DataAccessException {
        String query = "SELECT 1 FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DataAccessException("Error while checking if channel exists: " + e.getMessage());
        }
    }

    public void abonneUsers(Channel ch, List<String> users) {
        try {
            String query = "INSERT INTO estAbonne (uid, cid) VALUES (?, ?)";

            for (String user : users) {
                int uid = Integer.parseInt(user);
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setInt(1, uid);
                    stmt.setInt(2, ch.getCid());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) throws ConfigErrorException, ChannelCreationException, DataAccessException, ChannelNotFoundException {
        ChannelDAO dao = Config.getConfig().getChannelDAO();
        dao.createChannel("test");
        System.out.println(dao.getChannelById(1));
        dao.deleteChannel(1);
        System.out.println(dao.getAllChannels());
    }
}