package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;


import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Authent;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/Auth")
public class Auth extends HttpServlet {

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        String mail = req.getParameter("mail");
        String password = req.getParameter("password");

        System.out.println("mail: " + mail);
        System.out.println("password: " + password);

        if (mail == null || password == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        if (fr.univ.lille.s4a021.model.bdd.Authent.authenticateUser(mail, password)) {
            User usr = Authent.getUser(mail, password);
            HttpSession session = req.getSession();
            session.setAttribute("id", usr.getUid());
            res.sendRedirect("home.jsp");
        } else {
            res.sendRedirect("error.jsp");
        }
    }


}