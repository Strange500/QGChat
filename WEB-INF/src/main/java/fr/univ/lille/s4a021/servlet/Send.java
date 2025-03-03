package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;


import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

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
            RequestDispatcher rd = req.getRequestDispatcher(MainController.getJSPPath(MainController.ERROR));
            req.setAttribute("message", "Channel ID is not a number");
            req.setAttribute("errorCode", 400);
            rd.forward(req, res);
            return;
        }


        try {


            Part imgPart = (Part) req.getAttribute("img");

            System.out.println("attribute channelID: " + channelID);
            System.out.println("attribute message: " + msg);


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
            RequestDispatcher rd = req.getRequestDispatcher("home");
            req.setAttribute("senderror", "Erreur lors de l'envoi du message");
            rd.forward(req, res);
        }
    }


}