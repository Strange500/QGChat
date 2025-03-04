package fr.univ.lille.s4a021.model.bdd;

import jakarta.servlet.http.HttpSession;

public class Util {

    public static boolean userIsConnected(HttpSession session) {
        return session.getAttribute("id") != null;
    }

    public static int getUid(HttpSession session) {
        return (int) session.getAttribute("id");
    }
}
