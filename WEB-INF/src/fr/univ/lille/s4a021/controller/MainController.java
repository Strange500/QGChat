package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.NotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@MultipartConfig
@WebServlet("/home")
public class MainController extends AbstractController {

    public static final String HOME_JSP = "home.jsp";
    public static final String FRIEND_JSP = "friend.jsp";



    public static void main(String[] args) {
        System.out.println(getJSPPath("login.jsp"));
    }

    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, UserNotFoundException, DataAccessException, ChannelNotFoundException {
        int uid = Util.getUid(req.getSession());
        if (action == null) {
            String userProfilePicture = userDAO.getUserProfilePicture(uid);
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            List<Channel> subscribedChannels = subscriptionDAO.getSubscribedChannels(uid);
            req.setAttribute("subscribedChannels", subscribedChannels);

            int channelToViewId = req.getParameter("channelID") == null ? -1 : Integer.parseInt(req.getParameter("channelID"));
            boolean showChannel = channelToViewId != -1 && subscribedChannels.stream().anyMatch(channel -> channel.getCid() == channelToViewId);
            req.setAttribute("showChannel", showChannel);
            if (showChannel) {

                req.setAttribute("channelToViewId", channelToViewId);
                List<User> listAdmins = adminDAO.getAdmins(channelToViewId);
                Boolean isAdmin = listAdmins.stream().anyMatch(user -> user.getUid() == uid);
                int editMid = req.getParameter("editMid") == null ? -1 : Integer.parseInt(req.getParameter("editMid"));
                String sendError = req.getAttribute("senderror") == null ? "" : req.getAttribute("senderror").toString();
                Channel channel = channelDAO.getChannelById(channelToViewId);
                List<Message> messages = messageDAO.getMessagesAndImgMessagesByChannelId(channelToViewId);

                req.setAttribute("listAdmins", listAdmins);
                req.setAttribute("isAdmin", isAdmin);
                req.setAttribute("editMid", editMid);
                req.setAttribute("sendError", sendError);
                req.setAttribute("channel", channel);
                req.setAttribute("messages", messages);
                if (!messages.isEmpty()) {
                    req.setAttribute("userDAO", userDAO);
                    req.setAttribute("reactionDAO", reactionDAO);
                }


            }
            forwardToJSP(req, res, HOME_JSP);
            return;
        }

        switch (action) {
            case "logout":
                handleLogout(req, res);
                break;
            case "view":
                String userProfilePicture = userDAO.getUserProfilePicture(uid);
                User currentUser = userDAO.getUserById(uid);
                req.setAttribute("UserProfilePicture", userProfilePicture);
                req.setAttribute("currentUser", currentUser);

                List<Channel> subscribedChannels = subscriptionDAO.getSubscribedChannels(uid);
                req.setAttribute("subscribedChannels", subscribedChannels);

                int channelToViewId = req.getParameter("channelID") == null ? -1 : Integer.parseInt(req.getParameter("channelID"));
                if (channelToViewId != -1 && subscribedChannels.stream().anyMatch(channel -> channel.getCid() == channelToViewId)) {
                    req.setAttribute("showChannel", true);
                    req.setAttribute("channelToViewId", channelToViewId);
                    List<User> listAdmins = adminDAO.getAdmins(channelToViewId);
                    Boolean isAdmin = listAdmins.stream().anyMatch(user -> user.getUid() == uid);
                    int editMid = req.getParameter("editMid") == null ? -1 : Integer.parseInt(req.getParameter("editMid"));
                    String sendError = req.getAttribute("senderror") == null ? "" : req.getAttribute("senderror").toString();
                    Channel channel = channelDAO.getChannelById(channelToViewId);
                    List<Message> messages = messageDAO.getMessagesAndImgMessagesByChannelId(channelToViewId);

                    req.setAttribute("listAdmins", listAdmins);
                    req.setAttribute("isAdmin", isAdmin);
                    req.setAttribute("editMid", editMid);
                    req.setAttribute("sendError", sendError);
                    req.setAttribute("channel", channel);
                    req.setAttribute("messages", messages);
                    if (!messages.isEmpty()) {
                        req.setAttribute("userDAO", userDAO);
                        req.setAttribute("reactionDAO", reactionDAO);
                    }


                }
                forwardToJSP(req, res, HOME_JSP);
                break;
            default:
                handleError(new NotFoundException("Action not found"), req, res);
                break;
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getSession().invalidate();
        forwardToJSP(req, res, LOGIN_JSP);
    }

}