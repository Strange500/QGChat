package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.*;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageCreationException;
import fr.univ.lille.s4a021.exception.dao.message.MessageNotFoundException;
import fr.univ.lille.s4a021.exception.dao.message.MessageUpdateException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionCreationException;
import fr.univ.lille.s4a021.exception.dao.reaction.ReactionUpdateException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;

@MultipartConfig
@WebServlet("/message")
public class MessageController extends jakarta.servlet.http.HttpServlet {

    public final static String MODIFY_CHANNEL = "ModifChannel.jsp";
    public final static String CREATE_CHANNEL = "createChannel.jsp";
    public final static String SHARE = "share.jsp";
    public final static String INVITE = "join.jsp";



    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        ChannelDAO channelDAO = null;
        MessageDAO messageDAO = null;
        SubscriptionDAO subscriptionDAO = null;
        ReactionDAO reactionDAO  = null;
        AdminsDAO adminDAO = null;
        try {
            channelDAO = Config.getConfig().getChannelDAO();
            messageDAO = Config.getConfig().getMessageDAO();
            subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            reactionDAO = Config.getConfig().getReactionDAO();
            adminDAO = Config.getConfig().getAdminsDAO();
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }

        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
        }
        try {


            switch (action) {
                case "send":
                    String channelID = req.getParameter("channelID");
                    try {
                        if (!subscriptionDAO.isSubscribedTo(Util.getUid(session), Integer.parseInt(channelID))) {
                            MainController.sendErrorPage(401, "Unauthorized", req, res);
                            return;
                        }
                    } catch (UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                    }

                    String msg = req.getParameter("message");
                    Part imgPart = req.getPart("img");
                    try {
                        sendMessage(req, channelID, imgPart, msg, channelDAO, messageDAO);
                    } catch (ChannelNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    } catch (MessageCreationException e) {
                        MainController.sendErrorPage(500, e.getMessage(), req, res);
                        return;
                    }
                    res.sendRedirect("home?action=view&channelID=" + channelID);
                    break;
                case "like":
                    int mid = Integer.parseInt(req.getParameter("mid"));
                    int uid = (int) req.getSession().getAttribute("id");
                    String emoji  = req.getParameter("emoji");
                    int channelId = 0;
                    try {
                        if (!subscriptionDAO.isSubscribedTo(Util.getUid(session), channelId)) {
                            MainController.sendErrorPage(401, "Unauthorized", req, res);
                            return;
                        }
                        channelId = messageDAO.getMessageById(mid).getChannelId();
                        likeMessage(mid, uid, emoji, reactionDAO);

                    } catch (MessageNotFoundException | UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    } catch (ReactionCreationException | ReactionUpdateException e) {
                        MainController.sendErrorPage(500, e.getMessage(), req, res);
                        return;
                    }
                    res.sendRedirect("home?action=view&channelID=" + channelId);
                    break;
                case "delete":
                    int mid2 = Integer.parseInt(req.getParameter("mid"));
                    int cid = 0;
                    try {
                        Message message = messageDAO.getMessageById(mid2);
                        if (!subscriptionDAO.isSubscribedTo(Util.getUid(session), message.getChannelId())) {
                            MainController.sendErrorPage(401, "Unauthorized", req, res);
                            return;
                        }


                        if (message.getSenderId() != Util.getUid(session) && !adminDAO.userIsAdmin(Util.getUid(session), cid)) {
                            MainController.sendErrorPage(401, "Unauthorized", req, res);
                            return;
                        }

                        messageDAO.deleteMessage(mid2);

                    }catch (MessageNotFoundException | UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    }

                    res.sendRedirect("home?action=view&channelID=" + cid);
                    break;
                case "edit":
                    int midEdit = Integer.parseInt(req.getParameter("mid"));
                    String newMessage = req.getParameter("message");
                    int cidEdit = 0;
                    try {
                        Message msgEdit = messageDAO.getMessageById(midEdit);
                        cidEdit = msgEdit.getChannelId();

                        if (!subscriptionDAO.isSubscribedTo(Util.getUid(session), cidEdit)) {
                            MainController.sendErrorPage(401, "Unauthorized", req, res);
                            return;
                        }

                        if (msgEdit.getSenderId() != Util.getUid(session)) {
                            MainController.sendErrorPage(401, "Unauthorized", req, res);
                            return;
                        }

                        msgEdit.setContenu(StringEscapeUtils.escapeHtml4(newMessage));
                        messageDAO.updateMessage(msgEdit.getMid(), msgEdit.getContenu());
                    } catch (MessageNotFoundException | UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    } catch (MessageUpdateException e) {
                        MainController.sendErrorPage(500, e.getMessage(), req, res);
                        return;
                    }
                    res.sendRedirect("home?action=view&channelID=" + cidEdit);
                    break;

                default:
                    res.sendRedirect("home");

            }
        } catch (DataAccessException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }


    }


    private static void likeMessage(int mid, int uid, String emoji, ReactionDAO dao) throws MessageNotFoundException, UserNotFoundException, ReactionCreationException, DataAccessException, ReactionUpdateException {

        ReactionDAO.Reaction reaction = ReactionDAO.Reaction.getReactionFromEmoji(emoji);
        ReactionDAO.Reaction currentReaction = dao.getUserReactionForMessage(mid, uid);

        if (currentReaction == null) {
            dao.createReactionForMessage(mid, uid, reaction);
        } else if (currentReaction == reaction) {
            dao.deleteReactionForMessage(mid, uid);
        } else {
            dao.updateUserReactionForMessage(mid, uid, reaction);
        }

    }

    private static void sendMessage(HttpServletRequest req, String channelID, Part imgPart, String msg, ChannelDAO cDao, MessageDAO mDao) throws ChannelNotFoundException, DataAccessException, IOException, MessageCreationException {
        Channel channel = cDao.getChannelById(Integer.parseInt(channelID));
        if (imgPart.getSize()>0) {
            String imgBase64 = Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
            msg = "img:" + imgBase64;
        }
        msg = StringEscapeUtils.escapeHtml4(msg);
        int usr = (int) req.getSession().getAttribute("id");
        mDao.createMessage(msg, usr, channel.getCid());
    }
}