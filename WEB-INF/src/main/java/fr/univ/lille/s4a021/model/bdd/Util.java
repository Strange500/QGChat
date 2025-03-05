package fr.univ.lille.s4a021.model.bdd;

import fr.univ.lille.s4a021.dao.UserDAO;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;

public class Util {

    public static boolean userIsConnected(HttpSession session) throws SQLException {
        Object uidObj = session.getAttribute("id");
        if (uidObj == null) {
            session.invalidate();
            return false;
        }

        try {
            int uid = (int) uidObj;
            if (new UserDAO().getUserById(uid) == null) {
                session.invalidate();
                return false;
            }
        } catch (ClassCastException e) {
            session.invalidate();
            return false;
        }

        return true;
    }

    public static int getUid(HttpSession session) {
        return (int) session.getAttribute("id");
    }
}
