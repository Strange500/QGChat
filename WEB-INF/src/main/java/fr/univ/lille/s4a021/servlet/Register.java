package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;


import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

@WebServlet("/register")
public class Register extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        try {
            UserDAO userDAO = new UserDAO();
            String username = StringEscapeUtils.escapeHtml4(req.getParameter("username"));
            String mail = StringEscapeUtils.escapeHtml4(req.getParameter("mail"));
            String password = req.getParameter("password");
            userDAO.createUser(username, mail, password);
            res.sendRedirect("home");
        }
        catch (SQLException e) {
            RequestDispatcher rd = req.getRequestDispatcher(MainController.getJSPPath(MainController.LOGIN));
            req.setAttribute("registererror", "Erreur lors de l'inscription");
            rd.forward(req, res);
        }
    }


}