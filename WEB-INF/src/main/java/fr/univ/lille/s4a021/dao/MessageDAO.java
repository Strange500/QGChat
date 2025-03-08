package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.ImgMessage;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Connect;
import fr.univ.lille.s4a021.util.Pair;

import java.sql.*;
import java.util.*;

public class MessageDAO {
    private Connection connection;

    public MessageDAO() throws SQLException {
        this.connection = Connect.getConnection();
    }


    public enum Reaction {
        EMPTY(""),
        FIRE("🔥"),
        HEART("❤️"),
        LAUGH("😂"),
        SAD("😢"),
        ANGRY("😡");


        private final String emoji;

        Reaction(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }

        public static Reaction getReactionFromEmoji(String emoji) {
            for (Reaction r : Reaction.values()) {
                if (r.emoji.equals(emoji)) {
                    return r;
                }
            }
            return EMPTY;
        }

    }

    public List<String> getWhoLiked(int mid) throws SQLException {
        List<String> users = new ArrayList<>();
        String query = "SELECT u.username FROM likes l JOIN utilisateur u ON l.uid = u.uid WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                users.add(username);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean isLikedByUser(int mid, int uid) throws SQLException {
        String query = "SELECT * FROM likes WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<Reaction, Set<Integer>> getReactions(int mid) throws SQLException {
        Map<Reaction, Set<Integer>> reactions = new HashMap<>();
        String query = "SELECT u.uid, l.emoji FROM likes l JOIN utilisateur u ON l.uid = u.uid WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int uid = rs.getInt("uid");
                String reaction = rs.getString("emoji");
                Reaction react = Reaction.getReactionFromEmoji(reaction);
                reactions.put(react, reactions.getOrDefault(react, new HashSet<>()));
                reactions.get(react).add(uid);
            }
        }
        return reactions;
    }

    public Reaction getUserReaction(int mid, int uid) throws SQLException {
        String query = "SELECT emoji FROM likes WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String reaction = rs.getString("emoji");
                return Reaction.getReactionFromEmoji(reaction);
            }
        }
        return null;
    }

    public void updateReaction(int mid, int uid, Reaction emoji) throws SQLException {
        String query = "UPDATE likes SET emoji = ? WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, emoji.getEmoji());
            stmt.setInt(2, mid);
            stmt.setInt(3, uid);
            stmt.executeUpdate();
        }
    }

    public void likeMessage(int mid, int uid) throws SQLException {
        this.likeMessage(mid, uid, Reaction.HEART);
    }

    public void likeMessage(int mid, int uid, Reaction emoji) throws SQLException {
        String query = "INSERT INTO likes (mid, uid, emoji) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            stmt.setString(3, emoji.getEmoji());
            stmt.executeUpdate();
        }
    }

    public void unlikeMessage(int mid, int uid) throws SQLException {
        String query = "DELETE FROM likes WHERE mid = ? AND uid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, uid);
            stmt.executeUpdate();
        }
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
                }catch (SQLException e) {
                    e.printStackTrace();
                }

                try (PreparedStatement contientStmt = connection.prepareStatement(insertContientQuery)) {
                    contientStmt.setInt(1, channelId);
                    contientStmt.setInt(2, mid);
                    contientStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getChannelByMessageId(int mid) throws SQLException {
        String query = "SELECT cid FROM contient WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int channelId = rs.getInt("cid");
                return channelId;
            }
        }
        return -1;

    }

    // Récupération d'un message par son ID
    public Message getMessageById(int mid) throws SQLException {
        String query = "SELECT contenu, timestamp , (SELECT uid FROM aEnvoyer WHERE mid = ?) AS senderId, (SELECT cid FROM contient WHERE mid = ?) AS channelId FROM Message WHERE mid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, mid);
            stmt.setInt(2, mid);
            stmt.setInt(3, mid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String contenu = rs.getString("contenu");
                int senderId = rs.getInt("senderId");
                int channelId = rs.getInt("channelId");
                String timestamp = rs.getString("timestamp");
                return new Message(mid, contenu, senderId, channelId,timestamp);
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
        String query = "SELECT m.mid, m.contenu, timestamp , a.uid AS senderId FROM Message m JOIN contient c ON m.mid = c.mid JOIN aEnvoyer a ON m.mid = a.mid WHERE c.cid = ? ORDER BY timestamp";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, channelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mid = rs.getInt("mid");
                String contenu = rs.getString("contenu");
                int senderId = rs.getInt("senderId");
                String timestamp = rs.getString("timestamp");
                messages.add(new Message(mid, contenu, senderId, channelId, timestamp));
            }
        }
        return messages;
    }

    public Pair<List<ImgMessage>,List<Message>> separateImgFromMessage(List<Message> listMessage) {
        List<Message> messages = new ArrayList<>(listMessage);
        List<Message> lsToRemove = new ArrayList<>();
        List<ImgMessage> imgMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getContenu().startsWith("img:")) {
                String[] parts = message.getContenu().split(":");
                String content = parts[1];
                imgMessages.add(new ImgMessage(message, content));
                lsToRemove.add(message);
            }
        }
        messages.removeAll(lsToRemove);
        return new Pair<>(imgMessages, messages);
    }

}
