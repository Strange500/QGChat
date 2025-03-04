package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.model.bdd.Util;
import fr.univ.lille.s4a021.util.JwtManager;
import fr.univ.lille.s4a021.util.Pair;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/join")
public class Invite extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!Util.userIsConnected(request.getSession())) {
            MainController.sendErrorPage(401, "Unauthorized", request, response);
            return;
        }

        String token = request.getParameter("token");

        Pair<Integer, Integer> uidAndCid = null;
        try {
            uidAndCid = new JwtManager().getUidAndCidFromChannelInviteToken(token);
            if (uidAndCid == null) {
                MainController.sendErrorPage(400, "Invalid token", request, response);
                return;
            }
        } catch (JwtException e) {
            MainController.sendErrorPage(400, "Invalid token", request, response);
            return;
        }

        User user = null;
        Channel channel = null;
        try {
            user = new UserDAO().getUserById(Util.getUid(request.getSession()));
        } catch (SQLException e) {
            MainController.sendErrorPage(500, "Internal server error", request, response);
            return;
        }

        if (user == null) {
            MainController.sendErrorPage(500, "Internal server error", request, response);
            return;
        }

        try {
            channel = new ChannelDAO().getChannelById(uidAndCid.getSecond());
        } catch (SQLException e) {
            MainController.sendErrorPage(500, "Internal server error", request, response);
            return;
        }

        if (channel == null) {
            MainController.sendErrorPage(404, "Channel not found", request, response);
            return;
        }

        try {
            List<User> users = new ChannelDAO().getAbonnes(channel.getCid());
            User finalUser = user;
            if (users.stream().anyMatch(u -> u.getUid() == finalUser.getUid())) {
                MainController.sendErrorPage(400, "You are already subscribed to this channel", request, response);
                return;
            }
            Pair<Integer, Integer> finalUidAndCid = uidAndCid;
            if (users.stream().noneMatch(u -> u.getUid() == finalUidAndCid.getFirst())) {
                MainController.sendErrorPage(400, "User who invited you is not subscribed to this channel", request, response);
                return;
            }
            User finalUser1 = user;
            new ChannelDAO().abonneUsers(channel, new ArrayList<>(){{add(finalUser1.getUid() + "");}});
        } catch (SQLException e) {
            MainController.sendErrorPage(500, "Internal server error", request, response);
            return;
        }

        response.sendRedirect("home?action=view&channelID=" + channel.getCid());



    }

}

