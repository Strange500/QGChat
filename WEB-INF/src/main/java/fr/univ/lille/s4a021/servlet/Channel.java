package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.dao.ChannelDAO;
import jakarta.servlet.annotation.WebServlet;

import java.util.List;

@WebServlet("/channel")
public class Channel extends jakarta.servlet.http.HttpServlet {

    private static ChannelDAO channelDAO;

    public void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
            throws jakarta.servlet.ServletException, java.io.IOException {

        String action = req.getParameter("action");

        if (action == null) {
            res.sendRedirect("index.jsp");
            return;
        }

        if (channelDAO == null) {
            try {
                channelDAO = new ChannelDAO();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                res.sendRedirect("error.jsp");
                return;
            }
        }

        switch (action) {
            case "create":
                String name = req.getParameter("name");
                String[] abonneesArray = req.getParameterValues("users");
                List<String> abonnees = new java.util.ArrayList<>();
                if (abonneesArray != null) {
                    for (String abonnee : abonneesArray) {
                        abonnees.add(abonnee);
                    }
                }
                abonnees.add(req.getSession().getAttribute("id").toString());
                System.out.println(abonnees);


                try {
                    fr.univ.lille.s4a021.dto.Channel ch = channelDAO.createChannel(name);
                    channelDAO.abonneUsers(ch, abonnees);
                    res.sendRedirect("home.jsp");
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    res.sendRedirect("error.jsp");
                }
                break;
            case "delete":
                int cid = Integer.parseInt(req.getParameter("cid"));
                try {
                    channelDAO.deleteChannel(cid);
                    res.sendRedirect("home.jsp");
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    res.sendRedirect("error.jsp");
                }
                break;
            case "update":
                int cid2 = Integer.parseInt(req.getParameter("cid"));
                String newName = req.getParameter("newName");
                try {
                    channelDAO.updateChannel(cid2, newName);
                    res.sendRedirect("home.jsp");
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    res.sendRedirect("error.jsp");
                }
                break;
            default:
                res.sendRedirect("index.jsp");

        }


    }
}