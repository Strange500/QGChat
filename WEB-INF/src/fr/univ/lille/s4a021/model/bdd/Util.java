package fr.univ.lille.s4a021.model.bdd;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import jakarta.servlet.http.HttpSession;

public class Util {

    public static boolean userIsConnected(HttpSession session) throws ConfigErrorException {
        Object uidObj = session.getAttribute("id");
        if (uidObj == null) {
            session.invalidate();
            return false;
        }

        try {
            int uid = (int) uidObj;
            if (Config.getConfig().getUserDAO().getUserById(uid) == null) {
                session.invalidate();
                return false;
            }
        } catch (ClassCastException | UserNotFoundException | DataAccessException e) {
            session.invalidate();
            return false;
        }

        return true;
    }

    public static int getUid(HttpSession session) {
        return (int) session.getAttribute("id");
    }

}
