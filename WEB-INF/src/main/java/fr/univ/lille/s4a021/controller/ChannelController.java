package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static fr.univ.lille.s4a021.controller.MainController.getJSPPath;

@WebServlet("/channel")
public class ChannelController extends jakarta.servlet.http.HttpServlet {

    public final static String MODIFY_CHANNEL = "ModifChannel.jsp";
    public final static String CREATE_CHANNEL = "createChannel.jsp";
    public final static String SHARE = "share.jsp";
    public final static String INVITE = "join.jsp";



    public void service(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
            throws jakarta.servlet.ServletException, java.io.IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        ChannelDAO channelDAO = null;
        try {
            channelDAO = new ChannelDAO();
        } catch (SQLException e) {
            RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(MainController.ERROR));
            req.setAttribute("message", e.getMessage());
            req.setAttribute("errorCode", 500);
            rd.forward(req, res);
        }

        if (action == null) {
            res.sendRedirect("home");
            return;
        }

        if (channelDAO == null) {
            try {
                channelDAO = new ChannelDAO();
            } catch (SQLException e) {
                MainController.sendErrorPage(500, e.getMessage(), req, res);
                return;
            }
        }
        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }

        int uid = Util.getUid(session);

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

                try {
                    fr.univ.lille.s4a021.dto.Channel ch = channelDAO.createChannel(name);
                    channelDAO.abonneUsers(ch, abonnees);
                    channelDAO.setAdmin(ch, uid);
                    res.sendRedirect("home?action=view&channelID=" + ch.getCid());
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                break;
            case "delete":
                int cid = Integer.parseInt(req.getParameter("channelID"));
                try {
                    if (!channelDAO.userIsAdmin(uid, cid)) {
                        MainController.sendErrorPage(403, "Forbidden", req, res);
                        return;
                    }
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                try {
                    channelDAO.deleteChannel(cid);
                    res.sendRedirect("home");
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                break;
            case "update":
                int cid2 = Integer.parseInt(req.getParameter("channelID"));
                try {
                    if (!channelDAO.userIsAdmin(uid, cid2)) {
                        MainController.sendErrorPage(403, "Forbidden", req, res);
                        return;
                    }
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                String newName = req.getParameter("name");
                String[] abonneesArray2 = req.getParameterValues("users");
                List<String> abonnees2 = new java.util.ArrayList<>();
                if (abonneesArray2 != null) {
                    abonnees2.addAll(Arrays.asList(abonneesArray2));
                }
                abonnees2.add(Util.getUid(session) + "");

                String[] adminsArray = req.getParameterValues("admins");
                List<String> admins = new java.util.ArrayList<>();
                if (adminsArray != null) {
                    for (String admin : adminsArray) {
                        if (!abonnees2.contains(admin)) {
                            MainController.sendErrorPage(400, "Admins must be abonnees", req, res);
                            return;
                        }
                        admins.add(admin);
                    }
                }
                admins.add(Util.getUid(session) + "");

                try {
                    fr.univ.lille.s4a021.dto.Channel ch = channelDAO.getChannelById(cid2);
                    channelDAO.updateChannel(cid2, newName);
                    channelDAO.clearAbonnes(cid2);
                    channelDAO.abonneUsers(ch, abonnees2);
                    channelDAO.clearAdmins(cid2);
                    channelDAO.setAdmins(ch, admins);
                    res.sendRedirect("home?action=view&channelID=" + cid2);
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                break;
            case "modifchannel":
                int cid3 = Integer.parseInt(req.getParameter("channelID"));
                try {
                    if (!channelDAO.userIsAdmin(uid, cid3)) {
                        MainController.sendErrorPage(403, "Forbidden", req, res);
                        return;
                    }
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                RequestDispatcher rd3 = req.getRequestDispatcher(getJSPPath(MODIFY_CHANNEL));
                rd3.forward(req, res);
                break;
            case "createchannel":
                RequestDispatcher rd1 = req.getRequestDispatcher(getJSPPath(CREATE_CHANNEL));
                rd1.forward(req, res);
                break;
            case "share":
                String channelID2 = req.getParameter("channelID");
                req.setAttribute("channelID", channelID2);
                RequestDispatcher rd5 = req.getRequestDispatcher(getJSPPath(SHARE));
                rd5.forward(req, res);
                break;
            case "join":
                RequestDispatcher rd7 = req.getRequestDispatcher(getJSPPath(INVITE));
                rd7.forward(req, res);
                break;
            case "quit":
                int cid5 = Integer.parseInt(req.getParameter("channelID"));
                try {
                    channelDAO.unsubscribeUser(Util.getUid(session), cid5);
                    res.sendRedirect("home");
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                break;
            case "deletechannel":
                int cid4 = Integer.parseInt(req.getParameter("channelID"));
                try {
                    if (!channelDAO.userIsAdmin(uid, cid4)) {
                        MainController.sendErrorPage(403, "Forbidden", req, res);
                        return;
                    }
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                try {
                    channelDAO.deleteChannel(cid4);
                    res.sendRedirect("home");
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }
                break;
            default:
                res.sendRedirect("home");

        }


    }
}