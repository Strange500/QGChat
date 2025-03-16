package fr.univ.lille.s4a021.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univ.lille.s4a021.dao.ChannelDAO;
import fr.univ.lille.s4a021.dao.MessageDAO;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/*")
public class ApiController extends AbstractController {
    private ChannelDAO channelDAO;
    private MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.channelDAO = (ChannelDAO) getServletContext().getAttribute("channelDAO");
        this.messageDAO = (MessageDAO) getServletContext().getAttribute("messageDAO");
    }

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String info = req.getPathInfo();
        String[] parts = info.split("/");

        // Attribution compacte de ChanOrMes avec vérification de parts[1]: true for channel, false for message, null for anything else
        Boolean ChanOrMes = (parts.length > 1) ? ("channel".equals(parts[1]) ? true : ("message".equals(parts[1]) ? false : null)) : null;
        if(!ChanOrMes){
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erreur : Valeur de l'argument invalide. Attendu : 'channel' ou 'message'.");
        }

        // Check session
        HttpSession session = req.getSession(false);
        try {
            if (session == null || !Util.userIsConnected(session)) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated"); // à changer pour demander authentification
                return;
            }
        } catch (ConfigErrorException e) {
            throw new RuntimeException(e);
        }

        // Get all message per channel
        int userId = Util.getUid(session);
        try {
            List<Channel> channels = channelDAO.getSubscribedChannels(userId);
            Map<Channel, List<Message>> channelMessages = new HashMap<>();

            for (Channel channel : channels) {
                List<Message> messages = messageDAO.getMessageByChannelId(channel.getCid());
                channelMessages.put(channel, messages);
            }

            // print channel: message
            res.setContentType("application/json");
            new ObjectMapper().writeValue(res.getOutputStream(), channelMessages);
        } catch (DataAccessException | ChannelNotFoundException e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving channels or messages");
        }
    }
}
