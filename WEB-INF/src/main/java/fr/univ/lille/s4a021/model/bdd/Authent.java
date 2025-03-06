package fr.univ.lille.s4a021.model.bdd;

import fr.univ.lille.s4a021.dto.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authent {

    // Méthode pour vérifier les identifiants de l'utilisateur
    public static boolean authenticateUser(String usernameOrEmail, String password) {
        String query = "SELECT * FROM Utilisateur WHERE (username = ? OR mail = ?) AND password = MD5(?)";

        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);

            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Retourne vrai si un utilisateur correspondant est trouvé

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // Méthode pour obtenir les informations de l'utilisateur authentifié
    public static User getUser(String usernameOrEmail, String password) {
        String query = "SELECT * FROM Utilisateur WHERE (username = ? OR mail = ?) AND password = MD5(?)";


        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, password);



            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int uid = rs.getInt("uid");
                String username = rs.getString("username");
                String mail = rs.getString("mail");
                String pwd = rs.getString("password");
                return new User(uid, username, mail, pwd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
