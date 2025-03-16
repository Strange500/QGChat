package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.exception.MyDiscordException;
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

    private static final String ACTION_VIEW = "view";
    private static final String ACTION_VIEW_FRIEND = "viewFriend";
    private static final String ACTION_LOGOUT = "logout";

    public static void main(String[] args) {
        System.out.println("Hello World");
    }

    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        if (action == null || action.isEmpty()) {
            forwardToJSP(req, res, JSP.HOME);
            return;
        }

        switch (action) {
            case ACTION_LOGOUT:
                handleLogout(req, res);
                break;
            case ACTION_VIEW:
                forwardToJSP(req, res, JSP.HOME);
                break;
            case ACTION_VIEW_FRIEND:
                forwardToJSP(req, res, JSP.HOME);
                break;
            default:
                handleError(new NotFoundException("Action not found"), req, res);
                break;
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        req.getSession().invalidate();
        req.removeAttribute("id");
        forwardToJSP(req, res, JSP.LOGIN);
    }

}