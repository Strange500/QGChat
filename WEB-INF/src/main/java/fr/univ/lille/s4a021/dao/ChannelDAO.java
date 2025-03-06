package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Connect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAO {
    private Connection connection;

    public ChannelDAO() throws SQLException {
        this.connection = Connect.getConnection();
    }

    public List<User> getAbonnes(int cid) {
        List<User> users = new ArrayList<>();
        String query = "SELECT uid FROM estAbonne WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int uid = rs.getInt("uid");
                users.add(new UserDAO().getUserById(uid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void clearAbonnes(int cid) {
        String query = "DELETE FROM estAbonne WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Création d'un canal
    public Channel createChannel(String name) throws SQLException {
        String query = "INSERT INTO Channel (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }

        return getChannelByName(name);
    }

    public void setAdmin(Channel ch, int uid) {
        String query = "INSERT INTO isAdmin (uid, cid) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, ch.getCid());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearAdmins(int cid) {
        String query = "DELETE FROM isAdmin WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAdmins(Channel ch, List<String> users) {
        try {
            String query = "INSERT INTO isAdmin (uid, cid) VALUES (?, ?)";

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

    public Channel getChannelByName(String name) throws SQLException {
        String query = "SELECT cid FROM Channel WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int cid = rs.getInt("cid");
                Channel ch = new Channel(cid, name);
                ch.setMessages(new MessageDAO().getMessagesByChannelId(cid));
                return ch;
            }
        }
        return null; // Retourne null si le canal n'est pas trouvé
    }

    public void unsubscribeUser(int uid, int cid) throws SQLException {
        String query = "DELETE FROM estAbonne WHERE uid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, cid);
            stmt.executeUpdate();
        }
    }

    public boolean userIsAdmin(int uid, int cid) throws SQLException {
        String query = "SELECT * FROM isAdmin WHERE uid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, cid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public List<User> getAdmins(int cid) {
        List<User> users = new ArrayList<>();
        String query = "SELECT uid FROM isAdmin WHERE cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int uid = rs.getInt("uid");
                users.add(new UserDAO().getUserById(uid));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean isAbonne(int uid, int cid) throws SQLException {
        String query = "SELECT * FROM estAbonne WHERE uid = ? AND cid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, uid);
            stmt.setInt(2, cid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
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

    public static void main(String[] args) throws SQLException {
        ChannelDAO dao = new ChannelDAO();
        dao.createChannel("test");
        System.out.println(dao.getChannelById(1));
        dao.deleteChannel(1);
        System.out.println(dao.getAllChannels());
    }
}