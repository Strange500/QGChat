package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

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

        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
        }
        try {


            switch (action) {
                case "send":
                    String channelID = req.getParameter("channelID");
                    if (!new ChannelDAO().isAbonne(Util.getUid(session), Integer.parseInt(channelID))) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    String msg = req.getParameter("message");
                    Part imgPart = req.getPart("img");
                    sendMessage(req, channelID, imgPart, msg);
                    res.sendRedirect("home?action=view&channelID=" + channelID);
                    break;
                case "like":
                    int channelId = likeMessage(req, res);
                    if (!new ChannelDAO().isAbonne(Util.getUid(session), channelId)) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    res.sendRedirect("home?action=view&channelID=" + channelId);
                    break;
                case "delete":
                    int mid = Integer.parseInt(req.getParameter("mid"));
                    int cid = new MessageDAO().getChannelByMessageId(mid);

                    if (!new ChannelDAO().isAbonne(Util.getUid(session), cid)) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    Message message = new MessageDAO().getMessageById(mid);
                    if (message.getSenderId() != Util.getUid(session) && !new ChannelDAO().userIsAdmin(Util.getUid(session), cid)) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    MessageDAO messageDAO = new MessageDAO();
                    messageDAO.deleteMessage(mid);
                    res.sendRedirect("home?action=view&channelID=" + cid);
                    break;
                case "edit":
                    int midEdit = Integer.parseInt(req.getParameter("mid"));
                    String newMessage = req.getParameter("message");
                    int cidEdit = new MessageDAO().getChannelByMessageId(midEdit);

                    if (!new ChannelDAO().isAbonne(Util.getUid(session), cidEdit)) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    Message messageEdit = new MessageDAO().getMessageById(midEdit);
                    if (messageEdit.getSenderId() != Util.getUid(session)) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    messageEdit.setContenu(StringEscapeUtils.escapeHtml4(newMessage));
                    new MessageDAO().updateMessage(messageEdit.getMid(), messageEdit.getContenu());
                    res.sendRedirect("home?action=view&channelID=" + cidEdit);
                    break;

                default:
                    res.sendRedirect("home");

            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }


    }


    private static int likeMessage(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException {
        MessageDAO messageDAO = new MessageDAO();

        int mid = Integer.parseInt(req.getParameter("mid"));
        int uid = (int) req.getSession().getAttribute("id");

        if (messageDAO.isLikedByUser(mid, uid)) {
            messageDAO.unlikeMessage(mid, uid);
        } else {
            messageDAO.likeMessage(mid, uid);
        }

        return messageDAO.getChannelByMessageId(mid);
    }

    private static void sendMessage(HttpServletRequest req, String channelID, Part imgPart, String msg) throws SQLException, IOException {
        ChannelDAO channelDAO = new ChannelDAO();
        Channel channel = channelDAO.getChannelById(Integer.parseInt(channelID));
        if (imgPart.getSize()>0) {
            String imgBase64 = Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
            msg = "img:" + imgBase64;
        }
        msg = StringEscapeUtils.escapeHtml4(msg);
        int usr = (int) req.getSession().getAttribute("id");
        MessageDAO messageDAO = new MessageDAO();
        messageDAO.createMessage(msg, usr, channel.getCid());
    }
}