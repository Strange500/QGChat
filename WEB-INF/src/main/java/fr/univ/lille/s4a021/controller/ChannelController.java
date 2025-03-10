package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.AdminsDAO;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.SubscriptionDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.DaoException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.univ.lille.s4a021.model.bdd.Util.forwardToJSP;

@WebServlet("/channel")
public class ChannelController extends jakarta.servlet.http.HttpServlet {
    private static final String MODIFY_CHANNEL_JSP = "ModifChannel.jsp";
    private static final String CREATE_CHANNEL_JSP = "createChannel.jsp";
    private static final String SHARE_JSP = "share.jsp";
    private static final String INVITE_JSP = "join.jsp";

    private ChannelDAO channelDAO;
    private SubscriptionDAO subscriptionDAO;
    private AdminsDAO adminsDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            channelDAO = Config.getConfig().getChannelDAO();
            subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            adminsDAO = Config.getConfig().getAdminsDAO();
        } catch (ConfigErrorException e) {
            throw new ServletException("Failed to initialize DAOs", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (ConfigErrorException e) {
            MainController.handleError(e, req, res);
            return;
        }

        if (action == null) {
            res.sendRedirect("home");
            return;
        }

        int uid = Util.getUid(session);
        switch (action) {
            case "create":
                handleCreateChannel(req, res, uid);
                break;
            case "delete":
                handleDeleteChannel(req, res, uid);
                break;
            case "update":
                handleUpdateChannel(req, res, uid);
                break;
            case "modifchannel":
                forwardToJSP(req, res, MODIFY_CHANNEL_JSP);
                break;
            case "createchannel":
                forwardToJSP(req, res, CREATE_CHANNEL_JSP);
                break;
            case "share":
                handleShareChannel(req, res);
                break;
            case "join":
                forwardToJSP(req, res, INVITE_JSP);
                break;
            case "quit":
                handleUnsubscribeUser(req, res, uid);
                break;
            default:
                res.sendRedirect("home");
        }

    }

    private void handleCreateChannel(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, ServletException {
        String name = req.getParameter("name");
        List<Integer> subscribers = extractUserIds(req.getParameterValues("users"));
        subscribers.add(Util.getUid(req.getSession()));

        try {
            Channel channel = channelDAO.createChannel(name);
            subscriptionDAO.subscribeUsersTo(channel, subscribers);
            adminsDAO.setAdmin(channel.getCid(), uid);
            res.sendRedirect("home?action=view&channelID=" + channel.getCid());
        } catch (DaoException e) {
            MainController.handleError(e, req, res);
        }
    }

    private void handleDeleteChannel(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, ServletException {
        int cid = Integer.parseInt(req.getParameter("channelID"));
        try {
            if (!isAuthorized(uid, cid)) {
                MainController.handleError(new UnauthorizedException("Unauthorized"), req, res);
                return;
            }
            channelDAO.deleteChannel(cid);
            res.sendRedirect("home");
        } catch (DaoException e) {
            MainController.handleError(e, req, res);
        }
    }

    private void handleUpdateChannel(HttpServletRequest req, HttpServletResponse res, int uid) throws ServletException, IOException {
        int cid = Integer.parseInt(req.getParameter("channelID"));
        try {
            if (!isAuthorized(uid, cid)) {
                MainController.handleError(new UnauthorizedException("Unauthorized"), req, res);
                return;
            }
        } catch (DataAccessException e) {
            MainController.handleError(e, req, res);
            return;
        }

        String newName = req.getParameter("name");
        List<Integer> subscribers = extractUserIds(req.getParameterValues("users"));
        subscribers.add(uid);

        List<Integer> admins = extractUserIds(req.getParameterValues("admins"));
        if (!areAllAdminsSubscribers(subscribers, admins, req, res)) {
            return;
        }
        admins.add(uid);

        try {
            Channel channel = channelDAO.getChannelById(cid);
            channelDAO.updateChannel(cid, newName);
            subscriptionDAO.clearSubscriptions(cid);
            subscriptionDAO.subscribeUsersTo(channel, subscribers);
            adminsDAO.clearAdmins(cid);
            adminsDAO.setAdmins(channel.getCid(), admins);
            res.sendRedirect("home?action=view&channelID=" + cid);
        } catch (DaoException e) {
            MainController.handleError(e, req, res);
        }
    }

    private boolean areAllAdminsSubscribers(List<Integer> subscribers, List<Integer> admins, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        for (int adminUid : admins) {
            if (!subscribers.contains(adminUid)) {
                MainController.handleError(new BadParameterException("An admin must be a subscriber"), req, res);
                return false;
            }
        }
        return true;
    }

    private void handleShareChannel(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String channelID = req.getParameter("channelID");
        req.setAttribute("channelID", channelID);
        forwardToJSP(req, res, SHARE_JSP);
    }

    private void handleUnsubscribeUser(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, ServletException {
        int cid = Integer.parseInt(req.getParameter("channelID"));
        try {
            subscriptionDAO.unsubscribeUser(uid, cid);
            res.sendRedirect("home");
        } catch (DaoException e) {
            MainController.handleError(e, req, res);
        }
    }

    private boolean isAuthorized(int uid, int channelId) throws DataAccessException {
        try {
            return adminsDAO.userIsAdmin(uid, channelId);
        } catch (UserNotFoundException | ChannelNotFoundException e) {
            return false;
        }
    }

    private List<Integer> extractUserIds(String[] params) {
        List<Integer> userIds = new ArrayList<>();
        if (params != null) {
            for (String param : params) {
                try {
                    userIds.add(Integer.parseInt(param));
                } catch (NumberFormatException e) {
                    // Log or handle the error here as needed
                }
            }
        }
        return userIds;
    }

}