package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.exception.dao.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@MultipartConfig
@WebServlet("/home")
public class MainController extends AbstractController {

    public static final String HOME_JSP = "home.jsp";
    public static final String FRIEND_JSP = "friend.jsp";




    public static void main(String[] args) {
        System.out.println(getJSPPath("login.jsp"));
    }

    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (action == null) {
            forwardToJSP(req, res, HOME_JSP);
            return;
        }

        switch (action) {
            case "logout":
                handleLogout(req, res);
                break;
            case "view":
                forwardToJSP(req, res, HOME_JSP);
                break;
            default:
                handleError(new NotFoundException("Action not found"), req, res);
                break;
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getSession().invalidate();
        forwardToJSP(req, res, LOGIN_JSP);
    }

}