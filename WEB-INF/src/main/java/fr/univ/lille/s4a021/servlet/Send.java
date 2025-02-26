package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;


import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/send")
public class Send extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        try {
            ChannelDAO channelDAO = new ChannelDAO();
            Channel channel = channelDAO.getChannelById(Integer.parseInt(req.getParameter("channelID")));
            String msg = req.getParameter("message");
            int usr = (int) req.getSession().getAttribute("id");
            MessageDAO messageDAO = new MessageDAO();
            messageDAO.createMessage(msg, usr, channel.getCid());
            res.sendRedirect("channel.jsp?channelID=" + channel.getCid());
        }
        catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            res.sendRedirect("error.jsp");
        }
    }


}