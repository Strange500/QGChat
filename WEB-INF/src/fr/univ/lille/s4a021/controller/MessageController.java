package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.ReactionDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.dto.MsgType;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
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
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Collection;

@MultipartConfig
@WebServlet("/message")
public class MessageController extends AbstractController {

    private static final String ACTION_SEND = "send";
    private static final String ACTION_LIKE = "like";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_EDIT = "edit";

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        int uid = Util.getUid(req.getSession());
        if (action == null || action.isEmpty()) {
            res.sendRedirect("home");
            return;
        }
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

    private void handleSendMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws ServletException, IOException, MessageCreationException, ChannelNotFoundException, DataAccessException, BadParameterException, MessageNotFoundException, MessageUpdateException {
        String channelID = req.getParameter("channelID");
        if (checkSubscription(uid, channelID)) {
            sendMessage(req, channelID);
            res.sendRedirect("home?action=view&channelID=" + channelID);
            return;
        } else if (checkFriendChannel(uid, channelID)) {
            sendMessage(req, channelID);
            res.sendRedirect("home?action=viewFriend&channelID=" + channelID);
            return;
        }
        handleError(new ChannelNotFoundException("Channel not found"), req, res);
    }

    private boolean checkFriendChannel(int uid, String channelID) {
        try {
            if (!friendDAO.isFriendChannel(uid, Integer.parseInt(channelID))) {
                return false;
            }
        } catch (UserNotFoundException | DataAccessException e) {
            return false;
        }
        return true;
    }

    private static MsgType getMsgType(Part imgPart) throws BadParameterException {
        MsgType type = MsgType.TEXT;
        if (imgPart.getSize() > 0) {
            if (imgPart.getContentType().startsWith("image/")) {
                type = MsgType.IMAGE;
            } else if (imgPart.getContentType().startsWith("video/")) {
                type = MsgType.VIDEO;
            } else if (imgPart.getContentType().startsWith("audio/")) {
                type = MsgType.AUDIO;
            } else {
                throw new BadParameterException("Invalid file type");
            }
        }
        return type;
    }

    private void sendMessage(HttpServletRequest req, String channelID) throws ChannelNotFoundException, MessageCreationException, IOException, ServletException, DataAccessException, BadParameterException, MessageNotFoundException, MessageUpdateException {
        Collection<Part> parts = req.getParts();
        boolean done = false;
        Channel channel = channelDAO.getChannelById(Integer.parseInt(channelID));
        int usr = (int) req.getSession().getAttribute("id");
        Message lastMessage = messageDAO.getLastMessageByUserInChannel(usr, channel.getCid());

        for (Part part : parts) {
            if (part.getName().equals("img")) {
                if (part.getSize() == 0) continue;
                String newMsg = formatMessage(req.getParameter("message"), part);
                MsgType type = getMsgType(part);
                checkFileSize(part, type);
                messageDAO.createMessage(newMsg, usr, channel.getCid(), type);
                done = true;
            }
        }
        if (done) return;
        if (req.getParameter("message") == null || req.getParameter("message").isEmpty()) return;
        String newMsg = StringEscapeUtils.escapeHtml4(req.getParameter("message"));
        MsgType type = MsgType.TEXT;
        if (lastMessage != null && isWithinTimeFrame(lastMessage.getTimestamp(), 5*60) && lastMessage.getType() == MsgType.TEXT) {
            String mergedContent = lastMessage.getContenu() + "\n" + newMsg;
            messageDAO.updateMessage(lastMessage.getMid(), mergedContent);
        } else {
            messageDAO.createMessage(newMsg, usr, channel.getCid(), type);
        }
    }

    private void checkFileSize(Part imgPart, MsgType type) throws BadParameterException {
        int maxSize = 0;
        String msg = switch (type) {
            case IMAGE -> {
                maxSize = Config.IMAGE_MAX_SIZE;
                yield "Your image should be less than " + maxSize / (1024 * 1024) + "MB";
            }
            case VIDEO -> {
                maxSize = Config.VIDEO_MAX_SIZE;
                yield "Your video should be less than " + maxSize / (1024 * 1024) + "MB";
            }
            case AUDIO -> {
                maxSize = Config.AUDIO_MAX_SIZE;
                yield "Your audio should be less than " + maxSize / (1024 * 1024) + "MB";
            }
            default -> "";
        };
        if (imgPart.getSize() > maxSize) {
            throw new BadParameterException(msg);
        }

    }

    private boolean isWithinTimeFrame(String lastTimestamp, int seconds) {
        Timestamp lastTime = Timestamp.valueOf(lastTimestamp);
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        long diff = (currentTime.getTime() - lastTime.getTime()) / 1000;
        return diff <= seconds;
    }


    private String formatMessage(String msg, Part imgPart) throws IOException, BadParameterException {
        if (imgPart.getSize() > 0) {
            if (imgPart.getContentType().startsWith("image/")) {
                return Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
            } else if (imgPart.getContentType().startsWith("video/")) {
                return Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
            } else if (imgPart.getContentType().startsWith("audio/")) {
                return Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
            } else {
                throw new BadParameterException("Invalid file type");
            }
        }
        return StringEscapeUtils.escapeHtml4(msg);
    }

    private void handleLikeMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, UserNotFoundException, MessageNotFoundException, ReactionUpdateException, ReactionCreationException, ReactionNotFoundException, DataAccessException {
        int mid = Integer.parseInt(req.getParameter("mid"));
        String emoji = req.getParameter("emoji");
        int channelId = messageDAO.getMessageById(mid).getChannelId();

        if (checkSubscription(uid, String.valueOf(channelId))) {
            likeMessage(mid, uid, emoji);
            res.sendRedirect("home?action=view&channelID=" + channelId);
            return;
        } else if (checkFriendChannel(uid, String.valueOf(channelId))) {
            likeMessage(mid, uid, emoji);
            res.sendRedirect("home?action=viewFriend&channelID=" + channelId);
            return;
        }

        handleError(new ChannelNotFoundException("Channel not found"), req, res);
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

    private void handleDeleteMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, MessageNotFoundException, DataAccessException, UserNotFoundException, ChannelNotFoundException {
        int mid = Integer.parseInt(req.getParameter("mid"));
        Message message = messageDAO.getMessageById(mid);
        if (!hasDeletePermission(uid, message)) return;
        messageDAO.deleteMessage(mid);
        int channelId = message.getChannelId();
        res.sendRedirect("home?action=view&channelID=" + channelId);
    }

    private boolean hasDeletePermission(int uid, Message message) throws UserNotFoundException, ChannelNotFoundException, DataAccessException {
        boolean isfriendChannel = checkFriendChannel(uid, String.valueOf(message.getChannelId()));
        if (!checkSubscription(uid, String.valueOf(message.getChannelId())) && !isfriendChannel) {
            return false;
        }

        return message.getSenderId() == uid || (!isfriendChannel && adminDAO.userIsAdmin(uid, message.getChannelId()));
    }

    private void handleEditMessage(HttpServletRequest req, HttpServletResponse res, int uid) throws IOException, MessageNotFoundException, DataAccessException, UserNotFoundException, ChannelNotFoundException, MessageUpdateException {
        int mid = Integer.parseInt(req.getParameter("mid"));
        String newMessageContent = req.getParameter("message");
        Message message = messageDAO.getMessageById(mid);
        int channelId = message.getChannelId();
        if (!hasEditPermission(uid, message)) {
            res.sendRedirect("home?action=view&channelID=" + channelId);
            return;
        }
        if (newMessageContent == null || newMessageContent.isEmpty()) {
            messageDAO.deleteMessage(mid);
            res.sendRedirect("home?action=view&channelID=" + channelId);
            return;
        }

        message.setContenu(StringEscapeUtils.escapeHtml4(newMessageContent));
        messageDAO.updateMessage(mid, message.getContenu());
        res.sendRedirect("home?action=view&channelID=" + channelId);

    }

    private boolean hasEditPermission(int uid, Message message) throws UserNotFoundException, ChannelNotFoundException, DataAccessException {
        boolean isFriendChannel = checkFriendChannel(uid, String.valueOf(message.getChannelId()));
        if (!subscriptionDAO.isSubscribedTo(uid, message.getChannelId()) && !isFriendChannel) {
            return false;
        }

        return message.getSenderId() == uid || (!isFriendChannel && adminDAO.userIsAdmin(uid, message.getChannelId()));
    }

    private boolean checkSubscription(int uid, String channelID) {
        try {
            if (!subscriptionDAO.isSubscribedTo(uid, Integer.parseInt(channelID))) {
                return false;
            }
        } catch (UserNotFoundException | ChannelNotFoundException | DataAccessException e) {
            return false;
        }
        return true;
    }
}