package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Authent;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

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
            session.setMaxInactiveInterval(60 * 60);
            res.sendRedirect("home");
        } else {
            RequestDispatcher rd = req.getRequestDispatcher(MainController.getJSPPath(MainController.LOGIN));
            req.setAttribute("error", "Invalid credentials");
            rd.forward(req, res);
        }
    }


}