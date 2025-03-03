package fr.univ.lille.s4a021.servlet;

import java.io.IOException;
import java.io.PrintWriter;


import fr.univ.lille.s4a021.controller.MainController;
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

        if (mail == null || password == null) {
            RequestDispatcher rd = req.getRequestDispatcher(MainController.getJSPPath(MainController.LOGIN));
            req.setAttribute("error", "password or mail is missing");
            rd.forward(req, res);
            return;
        }

        if (fr.univ.lille.s4a021.model.bdd.Authent.authenticateUser(mail, password)) {
            User usr = Authent.getUser(mail, password);
            HttpSession session = req.getSession();
            session.setAttribute("id", usr.getUid());
            res.sendRedirect("home");
        } else {
            RequestDispatcher rd = req.getRequestDispatcher(MainController.getJSPPath(MainController.LOGIN));
            req.setAttribute("error", "Invalid credentials");
            rd.forward(req, res);
        }
    }


}