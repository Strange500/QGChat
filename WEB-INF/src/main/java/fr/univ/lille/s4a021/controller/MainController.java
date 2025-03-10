package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.CreationException;
import fr.univ.lille.s4a021.exception.dao.NotFoundException;
import fr.univ.lille.s4a021.exception.dao.UpdateException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import static fr.univ.lille.s4a021.model.bdd.Util.forwardToJSP;

@MultipartConfig
@WebServlet("/home")
public class MainController extends HttpServlet {

    public static final String LOGIN_JSP = "login.jsp";
    public static final String HOME_JSP = "home.jsp";
    public static final String ERROR_JSP = "error.jsp";
    public static final String FRIEND_JSP = "friend.jsp";


    public static String getJSPPath(String jsp) {
        return "/WEB-INF/" + jsp;
    }

    public static void handleError(Exception exception, HttpServletRequest req, HttpServletResponse res) {
        int errorCode = getErrorCode(exception);
        String message = exception.getMessage();

        if (Config.DEBUG) {
            req.setAttribute("exception", exception);
        }
        try {
            sendErrorPage(errorCode, message, req, res);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private static int getErrorCode(Exception exception) {
        if (exception instanceof MyDiscordException) {
            return switch (exception) {
                case NotFoundException e -> HttpServletResponse.SC_NOT_FOUND;
                case CreationException e1 -> HttpServletResponse.SC_BAD_REQUEST;
                case BadParameterException e3 -> HttpServletResponse.SC_BAD_REQUEST;
                case UpdateException e2 -> HttpServletResponse.SC_BAD_REQUEST;
                case UnauthorizedException e -> HttpServletResponse.SC_UNAUTHORIZED;
                default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            };
        }
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    private static void sendErrorPage(int errorCode, String message, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("errorCode", errorCode);
        req.setAttribute("message", message);
        forwardToJSP(req, res, ERROR_JSP);
    }

    public static void main(String[] args) {
        System.out.println(getJSPPath("login.jsp"));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        try {
            if (!Util.userIsConnected(session)) {
                forwardToJSP(req, res, LOGIN_JSP);
                return;
            }

            processAction(action, req, res);
        } catch (Exception e) {
            handleError(e, req, res);
        }
    }

    private void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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
                sendError(req, res, 404, "Action not found");
                break;
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getSession().invalidate();
        forwardToJSP(req, res, LOGIN_JSP);
    }

    private void sendError(HttpServletRequest req, HttpServletResponse res, int errorCode, String message) throws ServletException, IOException {
        req.setAttribute("errorCode", errorCode);
        req.setAttribute("message", message);
        forwardToJSP(req, res, ERROR_JSP);
    }
}