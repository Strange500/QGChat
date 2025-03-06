package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
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

@MultipartConfig
@WebServlet("/send")
public class Send extends HttpServlet {

    public void service( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        int channelID ;
        String msg = req.getAttribute("message").toString();
        try {
            channelID = Integer.parseInt(req.getAttribute("channelID").toString());
        } catch (NumberFormatException e) {
            MainController.sendErrorPage(400, "Bad request: channelID is not a number", req, res);
            return;
        }

        try {
            if (!Util.userIsConnected(req.getSession())) {
                res.sendRedirect("home");
                return;
            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
        }

        try {
            if (!new ChannelDAO().isAbonne(Util.getUid(req.getSession()), channelID)) {
                MainController.sendErrorPage(401, "Unauthorized", req, res);
                return;
            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, "Internal server error", req, res);
            return;
        }



        try {


            Part imgPart = (Part) req.getAttribute("img");
            ChannelDAO channelDAO = new ChannelDAO();
            Channel channel = channelDAO.getChannelById(channelID);
            if (imgPart.getSize()>0) {
                String imgName = UUID.randomUUID().toString() + ".jpg";
                String imgPath = getServletContext().getRealPath("/img") + "/" + imgName;
                imgPart.write(imgPath);
                msg = "img:" + imgName;
            }
            msg = StringEscapeUtils.escapeHtml4(msg);
            int usr = (int) req.getSession().getAttribute("id");
            MessageDAO messageDAO = new MessageDAO();
            messageDAO.createMessage(msg, usr, channel.getCid());
            System.out.println("Message sent");
            res.sendRedirect("home?action=view&channelID=" + channel.getCid());
        }
        catch (SQLException e) {
            RequestDispatcher rd = req.getRequestDispatcher(MainController.getJSPPath(MainController.HOME));
            req.setAttribute("senderror", "Erreur lors de l'envoi du message");
            rd.forward(req, res);
        }
    }


}