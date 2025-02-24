package fr.univ.lille.s4a021.servlet;


import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Authent;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/Auth")
public class Auth extends HttpServlet {



    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mail = request.getParameter("mail");
        String password = request.getParameter("password");

        if (Authent.authenticateUser(mail, password)) {
            User user = Authent.getUser(mail, password);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("index.jsp");
        } else {
            response.sendRedirect("error.jsp");
        }
    }


}
