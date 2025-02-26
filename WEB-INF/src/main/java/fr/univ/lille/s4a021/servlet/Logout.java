package fr.univ.lille.s4a021.servlet;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/logout")
public class Logout extends jakarta.servlet.http.HttpServlet {
    public void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
            throws jakarta.servlet.ServletException, java.io.IOException {
        req.getSession().invalidate();
        res.sendRedirect("index.jsp");
    }
}