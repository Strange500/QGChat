package fr.univ.lille.s4a021.dao.impl;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.ChannelDAO;
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

    public ChannelDAOSql(Connection con) {
        super(con);

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
                throw new ChannelCreationException("Error while creating channel: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while creating channel: " + e.getMessage(), e);
        }

    }

    public Channel getChannelByName(String name) throws ChannelNotFoundException, DataAccessException {
        String query = "SELECT cid, minuteBeforeExpiration FROM Channel WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int cid = rs.getInt("cid");
                int minuteBeforeExpiration = rs.getInt("minuteBeforeExpiration");
                return new Channel(cid, name, minuteBeforeExpiration);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting channel by name: " + e.getMessage(), e);
        }
        throw new ChannelNotFoundException("Channel not found");
    }

    public Channel getChannelById(int cid) throws ChannelNotFoundException, DataAccessException {
        String query = "SELECT name, minuteBeforeExpiration FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int minuteBeforeExpiration = rs.getInt("minuteBeforeExpiration");
                return new Channel(cid, name, minuteBeforeExpiration);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting channel by id: " + e.getMessage(), e);
        }
        throw new ChannelNotFoundException("Channel not found");
    }

    public void deleteChannel(int cid) throws ChannelNotFoundException, DataAccessException {
        String query = "DELETE FROM Channel WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new ChannelNotFoundException("Channel not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while deleting channel: " + e.getMessage(), e);
        }

    }

    public void updateChannel(int cid, String newName, int expiration) throws ChannelNotFoundException, ChannelUpdateException, DataAccessException {
        if (newName == null || newName.isEmpty()) {
            throw new ChannelUpdateException("Channel name cannot be empty");
        }
        String query = "UPDATE Channel SET name = ?, minuteBeforeExpiration = ? WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setInt(2, expiration);
            stmt.setInt(3, cid);
            int r = stmt.executeUpdate();
            if (r == 0) {
                throw new ChannelNotFoundException("Channel not found");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error while updating channel: " + e.getMessage(), e);
        }
    }

    public List<Channel> getAllChannels() throws DataAccessException {
        List<Channel> channels = new ArrayList<>();
        String query = "SELECT * FROM Channel";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                int minuteBeforeExpiration = rs.getInt("minuteBeforeExpiration");
                channels.add(new Channel(cid, name, minuteBeforeExpiration));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error while getting all channels: " + e.getMessage(), e);
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
            throw new DataAccessException("Error while checking if channel exists: " + e.getMessage(), e);
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