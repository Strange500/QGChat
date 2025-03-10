package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.*;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageCreationException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageUpdateException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionCreationException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionUpdateException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.Base64;

@MultipartConfig
@WebServlet("/message")
public class MessageController extends AbstractController {

    private static final String MODIFY_CHANNEL = "ModifChannel.jsp";
    private static final String CREATE_CHANNEL = "createChannel.jsp";
    private static final String SHARE = "share.jsp";
    private static final String INVITE = "join.jsp";

    private static final String ACTION_SEND = "send";
    private static final String ACTION_LIKE = "like";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_EDIT = "edit";

    private ChannelDAO channelDAO;
    private MessageDAO messageDAO;
    private SubscriptionDAO subscriptionDAO;
    private ReactionDAO reactionDAO;
    private AdminsDAO adminDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.channelDAO = Config.getConfig().getChannelDAO();
            this.messageDAO = Config.getConfig().getMessageDAO();
            this.subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            this.reactionDAO = Config.getConfig().getReactionDAO();
            this.adminDAO = Config.getConfig().getAdminsDAO();
        } catch (ConfigErrorException e) {
            throw new ServletException("Failed to initialize DAOs", e);
        }
    }

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        int uid = Util.getUid(req.getSession());
        switch (action) {
            case ACTION_SEND:
                handleSendMessage(req, res, uid);
                break;
            case ACTION_LIKE:
                handleLikeMessage(req, res, uid);
                break;
            case ACTION_DELETE:
                handleDeleteMessage(req, res, uid);
                break;
            case ACTION_EDIT:
                handleEditMessage(req, res, uid);
                break;
            default:
                res.sendRedirect("home");
        }
    }

    private void handleSendMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws ServletException, IOException, MessageCreationException, ChannelNotFoundException, DataAccessException {
        String channelID = req.getParameter("channelID");
        if (!checkSubscription(uid, channelID, req, res)) return;

        sendMessage(req, channelID);
        res.sendRedirect("home?action=view&channelID=" + channelID);

    }

    private void sendMessage(HttpServletRequest req, String channelID) throws ChannelNotFoundException, MessageCreationException, IOException, ServletException, DataAccessException {
        Part imgPart = req.getPart("img");
        String msg = formatMessage(req.getParameter("message"), imgPart);
        Channel channel = channelDAO.getChannelById(Integer.parseInt(channelID));
        int usr = (int) req.getSession().getAttribute("id");
        messageDAO.createMessage(msg, usr, channel.getCid());
    }

    private String formatMessage(String msg, Part imgPart) throws IOException {
        if (imgPart.getSize() > 0) {
            String imgBase64 = Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
            return "img:" + imgBase64;
        }
        return StringEscapeUtils.escapeHtml4(msg);
    }

    private void handleLikeMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws ServletException, IOException, UserNotFoundException, MessageNotFoundException, ReactionUpdateException, ReactionCreationException, ReactionNotFoundException, DataAccessException {
        int mid = Integer.parseInt(req.getParameter("mid"));
        String emoji = req.getParameter("emoji");

        if (!isSubscribedToMessage(uid, mid, req, res)) return;
        likeMessage(mid, uid, emoji);
        int channelId = messageDAO.getMessageById(mid).getChannelId();
        res.sendRedirect("home?action=view&channelID=" + channelId);

    }

    private void likeMessage(int mid, int uid, String emoji) throws ReactionNotFoundException, UserNotFoundException, ReactionCreationException, DataAccessException, ReactionUpdateException, MessageNotFoundException {
        ReactionDAO.Reaction reaction = ReactionDAO.Reaction.getReactionFromEmoji(emoji);
        ReactionDAO.Reaction currentReaction;
        try {
            currentReaction = getCurrentUserReaction(mid, uid);
        } catch (ReactionNotFoundException e) {
            currentReaction = null;
        }

        if (currentReaction == null) {
            reactionDAO.createReactionForMessage(mid, uid, reaction);
        } else if (currentReaction == reaction) {
            reactionDAO.deleteReactionForMessage(mid, uid);
        } else {
            reactionDAO.updateUserReactionForMessage(mid, uid, reaction);
        }
    }

    private ReactionDAO.Reaction getCurrentUserReaction(int mid, int uid) throws MessageNotFoundException, UserNotFoundException, ReactionNotFoundException, DataAccessException {
        return reactionDAO.getUserReactionForMessage(mid, uid);

    }

    private void handleDeleteMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws ServletException, IOException, MessageNotFoundException, DataAccessException, UserNotFoundException, ChannelNotFoundException, UnauthorizedException {
        int mid = Integer.parseInt(req.getParameter("mid"));
        Message message = messageDAO.getMessageById(mid);
        if (!hasDeletePermission(uid, message, req, res)) return;
        messageDAO.deleteMessage(mid);
        int channelId = message.getChannelId();
        res.sendRedirect("home?action=view&channelID=" + channelId);

    }

    private boolean hasDeletePermission(int uid, Message message, HttpServletRequest req, HttpServletResponse res) throws UserNotFoundException, ChannelNotFoundException, DataAccessException, UnauthorizedException {
        if (!subscriptionDAO.isSubscribedTo(uid, message.getChannelId())) {
            return false;
        }

        return message.getSenderId() == uid || adminDAO.userIsAdmin(uid, message.getChannelId());
    }

    private void handleEditMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws ServletException, IOException, MessageNotFoundException, DataAccessException, UserNotFoundException, ChannelNotFoundException, MessageUpdateException {
        int mid = Integer.parseInt(req.getParameter("mid"));
        String newMessageContent = req.getParameter("message");
        Message message = messageDAO.getMessageById(mid);
        if (!hasEditPermission(uid, message, req, res)) return;

        message.setContenu(StringEscapeUtils.escapeHtml4(newMessageContent));
        messageDAO.updateMessage(mid, message.getContenu());
        int channelId = message.getChannelId();
        res.sendRedirect("home?action=view&channelID=" + channelId);

    }

    private boolean hasEditPermission(int uid, Message message, HttpServletRequest req, HttpServletResponse res) throws UserNotFoundException, ChannelNotFoundException, DataAccessException {
        if (!subscriptionDAO.isSubscribedTo(uid, message.getChannelId())) {
            return false;
        }

        return message.getSenderId() == uid;
    }

    private boolean checkSubscription(int uid, String channelID, HttpServletRequest req, HttpServletResponse res) {
        try {
            if (!subscriptionDAO.isSubscribedTo(uid, Integer.parseInt(channelID))) {
                return false;
            }
        } catch (UserNotFoundException | ChannelNotFoundException | DataAccessException e) {
            return false;
        }
        return true;
    }

    private boolean isSubscribedToMessage(int uid, int mid, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            int channelId = messageDAO.getMessageById(mid).getChannelId();
            return checkSubscription(uid, String.valueOf(channelId), req, res);
        } catch (MessageNotFoundException | DataAccessException e) {
            handleError(e, req, res);
            return false;
        }
    }
}