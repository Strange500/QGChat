package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;


import fr.univ.lille.s4a021.dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/register")
public class Register extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        try {
            UserDAO userDAO = new UserDAO();
            String username = req.getParameter("username");
            String mail = req.getParameter("mail");
            String password = req.getParameter("password");
            userDAO.createUser(username, mail, password);
            res.sendRedirect("index.jsp");
        }
        catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            res.sendRedirect("error.jsp");
        }
    }


}