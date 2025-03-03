package fr.univ.lille.s4a021.servlet;

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

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/like")
public class Like extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res )
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

        try {
            if (messageDAO.isLikedByUser(mid, uid)) {
                messageDAO.unlikeMessage(mid, uid);
            } else {
                messageDAO.likeMessage(mid, uid);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int channelId = 0;
        try {
            channelId = messageDAO.getChannelByMessageId(mid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        res.sendRedirect("home?action=view&channelID=" + channelId);
    }


}