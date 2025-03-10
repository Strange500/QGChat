package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.AdminsDAO;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.SubscriptionDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelUpdateException;
import fr.univ.lille.s4a021.exception.dao.subscription.SubscriptionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import static fr.univ.lille.s4a021.controller.MainController.getJSPPath;

@WebServlet("/channel")
public class ChannelController extends jakarta.servlet.http.HttpServlet {

    public final static String MODIFY_CHANNEL = "ModifChannel.jsp";
    public final static String CREATE_CHANNEL = "createChannel.jsp";
    public final static String SHARE = "share.jsp";
    public final static String INVITE = "join.jsp";



    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        ChannelDAO channelDAO = null;
        SubscriptionDAO subscriptionDAO = null;
        AdminsDAO adminsDAO = null;
        try {
            channelDAO = Config.getConfig().getChannelDAO();
            subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            adminsDAO = Config.getConfig().getAdminsDAO();
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }

        if (action == null) {
            res.sendRedirect("home");
            return;
        }

        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }

        int uid = Util.getUid(session);
        try {
            switch (action) {
                case "create":
                    String name = req.getParameter("name");
                    String[] abonneesArray = req.getParameterValues("users");
                    List<Integer> abonnees = new java.util.ArrayList<>();
                    if (abonneesArray != null) {
                        for (String abonnee : abonneesArray) {
                            abonnees.add(Integer.parseInt(abonnee));
                        }
                    }
                    abonnees.add(Integer.parseInt(req.getSession().getAttribute("id").toString()));
                    try {
                        System.out.println(abonnees);
                        Channel ch = channelDAO.createChannel(name);
                        subscriptionDAO.subscribeUsersTo(ch, abonnees);
                        adminsDAO.setAdmin(ch.getCid(), uid);
                        res.sendRedirect("home?action=view&channelID=" + ch.getCid());
                    } catch (ChannelCreationException | AdminCreationException e) {
                        MainController.sendErrorPage(500, e.getMessage(), req, res);
                        return;
                    } catch (UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                        return;
                    }
                    break;
                case "delete":
                    int cid = Integer.parseInt(req.getParameter("channelID"));
                    try {
                        if (!adminsDAO.userIsAdmin(uid, cid)) {
                            MainController.sendErrorPage(403, "Forbidden", req, res);
                            return;
                        }
                    } catch (UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                        return;
                    }
                    try {
                        channelDAO.deleteChannel(cid);
                        res.sendRedirect("home");
                    }catch (ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                    }
                    break;
                case "update":
                    int cid2 = Integer.parseInt(req.getParameter("channelID"));
                    try {
                        if (!adminsDAO.userIsAdmin(uid, cid2)) {
                            MainController.sendErrorPage(403, "Forbidden", req, res);
                            return;
                        }
                    }catch (UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                        return;
                    }
                    String newName = req.getParameter("name");
                    String[] abonneesArray2 = req.getParameterValues("users");
                    List<Integer> abonnees2 = new java.util.ArrayList<>();
                    if (abonneesArray2 != null) {
                        for (String abonnee : abonneesArray2) {
                            abonnees2.add(Integer.parseInt(abonnee));
                        }
                    }
                    abonnees2.add(Util.getUid(session));

                    String[] adminsArray = req.getParameterValues("admins");
                    List<Integer> admins = new java.util.ArrayList<>();
                    if (adminsArray != null) {
                        for (String admin : adminsArray) {
                            int adminUid = Integer.parseInt(admin);
                            if (!abonnees2.contains(adminUid)) {
                                MainController.sendErrorPage(400, "Admin must be subscribed to the channel", req, res);
                                return;
                            }
                            admins.add(adminUid);
                        }
                    }
                    admins.add(Util.getUid(session));

                    try {
                        Channel ch = channelDAO.getChannelById(cid2);
                        channelDAO.updateChannel(cid2, newName);
                        subscriptionDAO.clearSubscriptions(cid2);
                        subscriptionDAO.subscribeUsersTo(ch, abonnees2);
                        adminsDAO.clearAdmins(cid2);
                        adminsDAO.setAdmins(ch.getCid(), admins);
                        res.sendRedirect("home?action=view&channelID=" + cid2);
                    } catch (ChannelUpdateException | AdminCreationException e) {
                        MainController.sendErrorPage(500, e.getMessage(), req, res);
                    } catch (ChannelNotFoundException | UserNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                    }
                    break;
                case "modifchannel":
                    int cid3 = Integer.parseInt(req.getParameter("channelID"));
                    try {
                        if (!adminsDAO.userIsAdmin(uid, cid3)) {
                            MainController.sendErrorPage(403, "Forbidden", req, res);
                            return;
                        }
                    } catch (UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
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
                        subscriptionDAO.unsubscribeUser(Util.getUid(session), cid5);
                        res.sendRedirect("home");
                    }catch (UserNotFoundException | ChannelNotFoundException | SubscriptionNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                        return;
                    }
                    break;
                case "deletechannel":
                    int cid4 = Integer.parseInt(req.getParameter("channelID"));
                    try {
                        if (!adminsDAO.userIsAdmin(uid, cid4)) {
                            MainController.sendErrorPage(403, "Forbidden", req, res);
                            return;
                        }
                    }  catch (UserNotFoundException | ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                        return;
                    }
                    try {
                        channelDAO.deleteChannel(cid4);
                        res.sendRedirect("home");
                    } catch (ChannelNotFoundException e) {
                        MainController.sendErrorPage(400, e.getMessage(), req, res);
                        return;
                    }
                    break;
                default:
                    res.sendRedirect("home");

            }
        } catch (DataAccessException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }

    }
}