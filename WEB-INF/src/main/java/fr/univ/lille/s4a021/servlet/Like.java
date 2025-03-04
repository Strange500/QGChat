package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.model.bdd.Authent;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/like")
public class Like extends HttpServlet {

    public void service( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        if (!Util.userIsConnected(req.getSession())) {
            res.sendRedirect("home");
            return;
        }

        MessageDAO messageDAO = null;
        try {
            messageDAO = new MessageDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int mid = Integer.parseInt(req.getParameter("mid"));
        int uid = (int) req.getSession().getAttribute("id");
        int channelId = 0;
        try {
            channelId = new MessageDAO().getChannelByMessageId(mid);
        } catch (SQLException e) {
            MainController.sendErrorPage(500, "Internal server error", req, res);
            return;
        }

        try {
            if (!new ChannelDAO().isAbonne(uid, channelId)) {
                MainController.sendErrorPage(401, "Unauthorized", req, res);
                return;
            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, "Internal server error", req, res);
            return;
        }

        try {
            if (messageDAO.isLikedByUser(mid, uid)) {
                messageDAO.unlikeMessage(mid, uid);
            } else {
                messageDAO.likeMessage(mid, uid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        res.sendRedirect("home?action=view&channelID=" + channelId);
    }


}