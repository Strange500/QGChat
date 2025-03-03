package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.UUID;


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

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        try {
            ChannelDAO channelDAO = new ChannelDAO();
            Channel channel = channelDAO.getChannelById(Integer.parseInt(req.getParameter("channelID")));
            String msg = req.getParameter("message");
            Part imgPart = req.getPart("img");
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
            res.sendRedirect("home.jsp?channelID=" + channel.getCid());
        }
        catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            res.sendRedirect("error.jsp");
        }
    }


}