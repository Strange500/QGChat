package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.SubscriptionDAO;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/join")
public class Invite extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (!Util.userIsConnected(request.getSession())) {
                MainController.sendErrorPage(401, "Unauthorized", request, response);
                return;
            }
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(401, e.getMessage(), request, response);
        }

        UserDAO userDAO = null;
        ChannelDAO channelDAO = null;
        SubscriptionDAO subscriptionDAO = null;
        try {
            userDAO = Config.getConfig().getUserDAO();
            channelDAO = Config.getConfig().getChannelDAO();
            subscriptionDAO = Config.getConfig().getSubscriptionDAO();
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(500, e.getMessage(), request, response);
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
            try {
                user = userDAO.getUserById(Util.getUid(request.getSession()));
            } catch (UserNotFoundException e) {
                MainController.sendErrorPage(400, "User not found", request, response);
                return;
            }

            try {
                channel = channelDAO.getChannelById(uidAndCid.getSecond());
            } catch (ChannelNotFoundException e) {
                MainController.sendErrorPage(400, "Channel not found", request, response);
                return;
            }

            try {
                if (subscriptionDAO.isSubscribedTo(user.getUid(), channel.getCid())) {
                    MainController.sendErrorPage(400, "You are already subscribed to this channel", request, response);
                    return;
                }
                if (!subscriptionDAO.isSubscribedTo(uidAndCid.getFirst(), channel.getCid())) {
                    MainController.sendErrorPage(400, "User who invited you is not subscribed to this channel", request, response);
                    return;
                }
                subscriptionDAO.subscribeUsersTo(channel, List.of(user.getUid()));
            } catch (ChannelNotFoundException e) {
                MainController.sendErrorPage(400, "Channel not found", request, response);
                return;
            } catch (UserNotFoundException e) {
                MainController.sendErrorPage(400, "User not found", request, response);
                return;
            }
            response.sendRedirect("home?action=view&channelID=" + channel.getCid());
        } catch (DataAccessException e) {
            MainController.sendErrorPage(500, e.getMessage(), request, response);
            return;
        }

    }

}

