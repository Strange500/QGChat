package fr.univ.lille.s4a021.controller;


import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

@MultipartConfig
@WebServlet("/home")
public class MainController extends HttpServlet {

    public final static String LOGIN = "login.jsp";
    public final static String HOME = "home.jsp";
    public final static String ERROR = "error.jsp";

    public static String getJSPPath(String jsp) {
        return "/WEB-INF/" + jsp;
    }


    public void service(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        if (Util.userIsConnected(session)) {
            if (action == null) {
                RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(HOME));
                rd.forward(req, res);
            } else {
                try {
                    switch (action) {
                        case "logout":
                            session.invalidate();
                            RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(LOGIN));
                            rd.forward(req, res);
                            break;

                        case "view":
                            RequestDispatcher rd2 = req.getRequestDispatcher(getJSPPath(HOME));
                            rd2.forward(req, res);
                            break;
                        default:
                            RequestDispatcher rd4 = req.getRequestDispatcher(getJSPPath(ERROR));
                            req.setAttribute("errorCode", 404);
                            req.setAttribute("message", "Action not found");
                            rd4.forward(req, res);
                            break;

                    }
                } catch (Exception e) {
                    RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(ERROR));
                    req.setAttribute("errorCode", 500);
                    req.setAttribute("message", e.getMessage());
                    rd.forward(req, res);
                }
            }
        } else {
            RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(LOGIN));
            rd.forward(req, res);
        }

    }



    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }

    public static void sendErrorPage(int errorCode, String msg, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("errorCode", errorCode);
        req.setAttribute("message", msg);
        RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(ERROR));
        rd.forward(req, res);
    }
}
