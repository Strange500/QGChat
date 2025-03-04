package fr.univ.lille.s4a021.controller;


import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

@MultipartConfig
@WebServlet("/home")
public class MainController extends HttpServlet {

    public final static String LOGIN = "login.jsp";
    public final static String HOME = "home.jsp";
    public final static String ERROR = "error.jsp";
    public final static String CREATE_CHANNEL = "createChannel.jsp";
    public final static String MODIFY_CHANNEL = "ModifChannel.jsp";
    public final static String SHARE = "share.jsp";
    public final static String INVITE = "join.jsp";



    public static String getJSPPath(String jsp) {
        return "/WEB-INF/" + jsp;
    }


    public void service(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        if (Util.userIsConnected(session)) {
            if (action == null) {
                RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(HOME));
                rd.forward(req, res);
            } else {
                try {
                    switch (action) {
                        case "logout":
                            session.invalidate();
                            RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(LOGIN));
                            rd.forward(req, res);
                            break;
                        case "createchannel":
                            RequestDispatcher rd1 = req.getRequestDispatcher(getJSPPath(CREATE_CHANNEL));
                            rd1.forward(req, res);
                            break;
                        case "view":
                            RequestDispatcher rd2 = req.getRequestDispatcher(getJSPPath(HOME));
                            rd2.forward(req, res);
                            break;
                        case "send":
                            String channelID = req.getParameter("channelID");
                            String msg = req.getParameter("message");
                            Part imgPart = req.getPart("img");
                            sendMessage(req, channelID, imgPart, msg);
                            RequestDispatcher rd6 = req.getRequestDispatcher(getJSPPath(HOME));
                            rd6.forward(req, res);
                            break;
                        case "like":
                            int channelId = likeMessage(req, res);
                            res.sendRedirect("home?action=view&channelID=" + channelId);
                            break;
                        case "modifchannel":
                            RequestDispatcher rd3 = req.getRequestDispatcher(getJSPPath(MODIFY_CHANNEL));
                            rd3.forward(req, res);
                            break;
                        case "share":
                            String channelID2 = req.getParameter("channelID");
                            req.setAttribute("channelID", channelID2);
                            RequestDispatcher rd5 = req.getRequestDispatcher(getJSPPath(SHARE));
                            rd5.forward(req, res);
                            break;
                        case "join":
                            RequestDispatcher rd7 = req.getRequestDispatcher(getJSPPath(INVITE));
                            rd7.forward(req, res);
                            break;
                        default:
                            RequestDispatcher rd4 = req.getRequestDispatcher(getJSPPath(ERROR));
                            req.setAttribute("errorCode", 404);
                            req.setAttribute("message", "Action not found");
                            rd4.forward(req, res);
                            break;

                    }
                } catch (Exception e) {
                    RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(ERROR));
                    req.setAttribute("errorCode", 500);
                    req.setAttribute("message", e.getMessage());
                    rd.forward(req, res);
                }
            }
        } else {
            RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(LOGIN));
            rd.forward(req, res);
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

    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

    public static void sendErrorPage(int errorCode, String msg, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("errorCode", errorCode);
        req.setAttribute("message", msg);
        RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(ERROR));
        rd.forward(req, res);
    }
}
