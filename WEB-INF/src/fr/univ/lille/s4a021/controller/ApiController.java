package fr.univ.lille.s4a021.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.subscription.SubscriptionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@WebServlet("/api/*")
public class ApiController extends AbstractController {

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) {
        return;
    }

    @Override
    protected void processNoAuthAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        switch (req.getMethod()) {
            case "GET":
                doGet(req, res);
                break;
            case "POST":
                doPost(req, res);
                break;
            case "PUT":
                doPut(req, res);
                break;
            case "DELETE":
                doDelete(req, res);
                break;
            default:
                res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer userId = getUidFromheader(req, res);
        if (userId == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be authenticated with a valid token");
            return;
        }
        String info = req.getPathInfo();
        String[] parts = info.split("/");
        String entity = parts.length > 0 ? parts[1] : "";

        switch (entity) {
            case "channels":
                try {
                    if (parts.length == 3) {
                        Channel ch = null;
                        try {
                            ch = channelDAO.getChannelById(Integer.parseInt(parts[2]));
                        } catch (ChannelNotFoundException e) {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Channel not found");
                            return;
                        }
                        try {
                            if (!adminDAO.userIsAdmin(userId, ch.getCid())) {
                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to delete this channel");
                                return;
                            }
                        } catch (UserNotFoundException | ChannelNotFoundException e) {
                            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error checking if user is admin");
                            return;
                        }
                        channelDAO.deleteChannel(ch.getCid());
                        res.setStatus(HttpServletResponse.SC_ACCEPTED);
                        new ObjectMapper().writeValue(res.getOutputStream(), ch);
                        return;
                    }
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Channel id is missing");
                } catch (DataAccessException e) {
                    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting channel");
                } catch (ChannelNotFoundException e) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Channel not found");
                }
                break;
            case "subscription":
                try {
                    if (parts.length == 3) {
                        Channel ch = null;
                        try {
                            ch = channelDAO.getChannelById(Integer.parseInt(parts[2]));
                        } catch (ChannelNotFoundException e) {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Channel not found");
                            return;
                        }
                        subscriptionDAO.unsubscribeUser(userId, ch.getCid());
                        res.setStatus(HttpServletResponse.SC_ACCEPTED);
                        new ObjectMapper().writeValue(res.getOutputStream(), ch);
                        return;
                    }
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Channel id is missing");
                } catch (DataAccessException | UserNotFoundException e) {
                    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting subscription");
                } catch (ChannelNotFoundException e) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Channel not found");
                } catch (SubscriptionNotFoundException e) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Subscription not found");
                }
                break;
            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Integer getUidFromheader(HttpServletRequest req, HttpServletResponse res) {
        String token = req.getHeader("Authorization");
        return getUserIdFromToken(token);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer userId = getUidFromheader(req, res);
        if (userId == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be authenticated with a valid token");
            return;
        }
        String info = req.getPathInfo();
        String[] parts = info.split("/");
        String entity = parts.length > 0 ? parts[1] : "";

        switch (entity) {
            case "channels":
                try {
                    if (parts.length == 3) {
                        Channel ch = channelDAO.getSubscribedChannels(userId).stream().filter(c -> c.getCid() == Integer.parseInt(parts[2])).findFirst().orElse(null);
                        if (ch == null) {
                            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Channel not found");
                            return;
                        }
                        List<Message> messages = messageDAO.getMessageByChannelId(ch.getCid());
                        ch.setMessages(messages);
                        res.setContentType("application/json");
                        new ObjectMapper().writeValue(res.getOutputStream(), ch);
                        return;
                    }
                    List<Channel> channels = channelDAO.getSubscribedChannels(userId);
                    res.setContentType("application/json");
                    new ObjectMapper().writeValue(res.getOutputStream(), channels);
                } catch (DataAccessException | ChannelNotFoundException e) {
                    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving channels or messages");
                }
                break;
            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer userId = getUidFromheader(req, res);
        if (userId == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You must be authenticated with a valid token");
            return;
        }
        String info = req.getPathInfo();
        String[] parts = info.split("/");
        String entity = parts.length > 0 ? parts[1] : "";

        switch (entity) {
            case "channels":
                String name = req.getParameter("name");
                if (name == null) {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Channel name is missing");
                    return;
                }
                try {
                    Channel ch = channelDAO.createChannel(name);
                    subscriptionDAO.subscribeUsersTo(ch, List.of(userId));
                    adminDAO.setAdmin(ch.getCid(), userId);
                    res.setStatus(HttpServletResponse.SC_CREATED);
                    new ObjectMapper().writeValue(res.getOutputStream(), ch);
                } catch (DataAccessException | ChannelCreationException | AdminCreationException |
                         ChannelNotFoundException | UserNotFoundException e) {
                    res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                }


                break;

            default:
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private Integer getUserIdFromToken(String baseToken) {
        try {
            String rawToken = baseToken.split(" ")[1];
            String token = new String(Base64.getDecoder().decode(rawToken));
            String mail = token.split(":")[0];
            String password = token.split(":")[1];

            if (userDAO.authenticateUser(mail, password)) {
               return userDAO.getUserByMail(mail).getUid();
            }
        } catch (DataAccessException e) {
            return null;
        } catch (UserNotFoundException e) {
            throw null;
        }
        return null;
    }
}
