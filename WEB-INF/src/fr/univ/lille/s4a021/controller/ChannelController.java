package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelUpdateException;
import fr.univ.lille.s4a021.exception.dao.subscription.SubscriptionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import fr.univ.lille.s4a021.util.JwtManager;
import fr.univ.lille.s4a021.util.Pair;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/channel")
public class ChannelController extends AbstractController {

    private static final String ACTION_CREATE = "create";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_MODIFCHANNEL = "modifchannel";
    private static final String ACTION_CREATECHANNEL = "createchannel";
    private static final String ACTION_SHARE = "share";
    private static final String ACTION_ACCEPT_INVITE = "acceptInvite";
    private static final String ACTION_JOIN = "join";
    private static final String ACTION_QUIT = "quit";

    private void handleCreateChannel(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, BadParameterException, ChannelCreationException, DataAccessException, UserNotFoundException, ChannelNotFoundException, AdminCreationException {
        String name = this.getEscapedParameter(req, "name");
        if (name == null || name.isEmpty()) {
            throw new BadParameterException("Channel name cannot be empty");
        }
        List<Integer> subscribers = extractUserIds(this.getEscapedParameterValues(req, "users"));
        subscribers.add(Util.getUid(req.getSession()));

        Channel channel = channelDAO.createChannel(name);
        subscriptionDAO.subscribeUsersTo(channel, subscribers);
        adminDAO.setAdmin(channel.getCid(), uid);
        res.sendRedirect("home?action=view&channelID=" + channel.getCid());

    }

    private void handleDeleteChannel(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, DataAccessException, ChannelNotFoundException, UnauthorizedException {
        int cid = Integer.parseInt(this.getEscapedParameter(req, "channelID"));
        if (!isAuthorized(uid, cid)) {
            throw new UnauthorizedException("Unauthorized");
        }
        channelDAO.deleteChannel(cid);
        res.sendRedirect("home");

    }

    private void handleUpdateChannel(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, BadParameterException, UnauthorizedException, DataAccessException, ChannelNotFoundException, UserNotFoundException, ChannelUpdateException, AdminCreationException {
        int cid = Integer.parseInt(this.getEscapedParameter(req, "channelID"));
        if (!isAuthorized(uid, cid)) {
            throw new UnauthorizedException("Unauthorized");
        }
        String newName = this.getEscapedParameter(req, "name");
        int expiration = Integer.parseInt(this.getEscapedParameter(req, "expiration"));
        List<Integer> subscribers = extractUserIds(this.getEscapedParameterValues(req, "users"));
        subscribers.add(uid);

        List<Integer> admins = extractUserIds(this.getEscapedParameterValues(req, "admins"));
        if (!areAllAdminsSubscribers(subscribers, admins)) {
            return;
        }
        admins.add(uid);

        Channel channel = channelDAO.getChannelById(cid);
        channelDAO.updateChannel(cid, newName, expiration);
        subscriptionDAO.clearSubscriptions(cid);
        subscriptionDAO.subscribeUsersTo(channel, subscribers);
        adminDAO.clearAdmins(cid);
        adminDAO.setAdmins(channel.getCid(), admins);
        res.sendRedirect("home?action=view&channelID=" + cid);

    }

    private boolean areAllAdminsSubscribers(List<Integer> subscribers, List<Integer> admins) throws BadParameterException {
        for (int adminUid : admins) {
            if (!subscribers.contains(adminUid)) {
                throw new BadParameterException("An admin must be a subscriber");
            }
        }
        return true;
    }

    private void handleShareChannel(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        String channelID = this.getEscapedParameter(req, "channelID");
        req.setAttribute("channelID", channelID);
        forwardToJSP(req, res, JSP.SHARE_CHANNEL);
    }

    private void handleUnsubscribeUser(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, UserNotFoundException, ChannelNotFoundException, SubscriptionNotFoundException, DataAccessException {
        int cid = Integer.parseInt(this.getEscapedParameter(req, "channelID"));
        subscriptionDAO.unsubscribeUser(uid, cid);
        res.sendRedirect("home");

    }

    private boolean isAuthorized(int uid, int channelId) throws DataAccessException {
        try {
            return adminDAO.userIsAdmin(uid, channelId);
        } catch (UserNotFoundException | ChannelNotFoundException e) {
            return false;
        }
    }

    private List<Integer> extractUserIds(String[] params) throws BadParameterException {
        List<Integer> userIds = new ArrayList<>();
        if (params != null) {
            for (String param : params) {
                try {
                    userIds.add(Integer.parseInt(param));
                } catch (NumberFormatException e) {
                    throw new BadParameterException("Invalid user ID");
                }
            }
        }
        return userIds;
    }

    private void handleAcceptInvite(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, DataAccessException, ChannelNotFoundException, UserNotFoundException, SubscriptionNotFoundException, UnauthorizedException {
        String token = this.getEscapedParameter(req, "token");
        Pair<Integer, Integer> uidAndCid ;
        try {
            uidAndCid = new JwtManager().getUidAndCidFromChannelInviteToken(token);
            if (uidAndCid == null) {
                throw new UnauthorizedException("Invalid token");
            }
        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid token");
        }
        User user = userDAO.getUserById(uid);
        Channel channel = channelDAO.getChannelById(uidAndCid.getSecond());
        if (subscriptionDAO.isSubscribedTo(user.getUid(), channel.getCid())) {
            throw new SubscriptionNotFoundException("You are already subscribed to this channel");
        }
        if (!subscriptionDAO.isSubscribedTo(uidAndCid.getFirst(), channel.getCid())) {
            throw new SubscriptionNotFoundException("The user who invited you is not subscribed to this channel");
        }
        subscriptionDAO.subscribeUsersTo(channel, List.of(user.getUid()));

        res.sendRedirect("home?action=view&channelID=" + channel.getCid());

    }

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        if (action == null || action.isEmpty()) {
            res.sendRedirect("home");
            return;
        }
        int uid = Util.getUid(req.getSession());
        switch (action) {
            case ACTION_CREATE:
                try {
                    handleCreateChannel(req, res, uid);
                } catch (BadParameterException e) {
                    req.setAttribute("channelNameError", e.getMessage());
                    forwardToJSP(req, res, JSP.CREATE_CHANNEL);
                }
                break;
            case ACTION_DELETE:
                handleDeleteChannel(req, res, uid);
                break;
            case ACTION_UPDATE:
                handleUpdateChannel(req, res, uid);
                break;
            case ACTION_MODIFCHANNEL:
                forwardToJSP(req, res, JSP.EDIT_CHANNEL);
                break;
            case ACTION_CREATECHANNEL:
                forwardToJSP(req, res, JSP.CREATE_CHANNEL);
                break;
            case ACTION_SHARE:
                handleShareChannel(req, res);
                break;
            case ACTION_ACCEPT_INVITE:
                handleAcceptInvite(req, res, uid);
                break;
            case ACTION_JOIN:
                forwardToJSP(req, res, JSP.JOIN);
                break;
            case ACTION_QUIT:
                handleUnsubscribeUser(req, res, uid);
                break;
            default:
                res.sendRedirect("home");
        }

    }
}