package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.*;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.CreationException;
import fr.univ.lille.s4a021.exception.dao.NotFoundException;
import fr.univ.lille.s4a021.exception.dao.UpdateException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


public abstract class AbstractController extends HttpServlet {

    protected ChannelDAO channelDAO;
    protected MessageDAO messageDAO;
    protected SubscriptionDAO subscriptionDAO;
    protected ReactionDAO reactionDAO;
    protected AdminsDAO adminDAO;
    protected UserDAO userDAO;
    protected FriendDAO friendDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.channelDAO = Config.getConfig().getChannelDAO();
            this.messageDAO = Config.getConfig().getMessageDAO();
            this.subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            this.reactionDAO = Config.getConfig().getReactionDAO();
            this.adminDAO = Config.getConfig().getAdminsDAO();
            this.userDAO = Config.getConfig().getUserDAO();
            this.friendDAO = Config.getConfig().getFriendDAO();
        } catch (ConfigErrorException e) {
            throw new ServletException("Failed to initialize DAOs", e);
        }
    }

    public static void handleError(Exception exception, HttpServletRequest req, HttpServletResponse res) {
        int errorCode = getErrorCode(exception);
        String message = exception.getMessage();

        if (Config.DEBUG) {
            req.setAttribute("exception", exception);
        }
        try {
            sendErrorPage(errorCode, message, req, res);
        } catch (ServletException | IOException | MyDiscordException e) {
            e.printStackTrace();
        }
    }

    public static int getErrorCode(Exception exception) {
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

    private static void sendErrorPage(int errorCode, String message, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        req.setAttribute("errorCode", errorCode);
        req.setAttribute("message", message);
        forwardToJSP(req, res, JSP.ERROR);
    }

    public static void forwardToJSP(HttpServletRequest req, HttpServletResponse res, JSP jsp) throws ServletException, IOException, MyDiscordException {
        jsp.launch(req, res);
    }



    protected void service(HttpServletRequest req, HttpServletResponse res) {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        try {

            processNoAuthAction(action, req, res);

            if (res.isCommitted()) {
                return;
            }

            if (!Util.userIsConnected(session)) {
                forwardToJSP(req, res, JSP.LOGIN);
                return;
            }

            req.setAttribute("id", Util.getUid(session));

            processAction(action, req, res);

        } catch (Exception e) {
            handleError(e, req, res);
        }
    }

    protected abstract void processAction(String action, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, MyDiscordException;

    protected void processNoAuthAction(String action, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException, MyDiscordException {
    }
}