package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.*;
import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.Message;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.model.bdd.Util;
import fr.univ.lille.s4a021.util.JwtManager;
import fr.univ.lille.s4a021.util.Pair;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public enum JSP {
    HOME("home.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException {
            req.removeAttribute("referer");
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            List<Channel> subscribedChannels = subscriptionDAO.getSubscribedChannels(uid);
            req.setAttribute("subscribedChannels", subscribedChannels);

            List<Pair<User, Channel>> friendChannels = friendDAO.getFriendChannels(uid);
            req.setAttribute("friendChannels", friendChannels);

            List<User> friendRequests = friendDAO.getFriendRequests(uid);
            req.setAttribute("friendRequests", friendRequests);

            List<User> pendingFriendRequests = friendDAO.getFriendRequestSent(uid);
            req.setAttribute("pendingFriendRequests", pendingFriendRequests);

            int channelToViewId = AbstractController.getEscapedParameter(req, "channelID") == null ? -1 : Integer.parseInt(AbstractController.getEscapedParameter(req, "channelID"));
            boolean friendChannel = friendChannels.stream().anyMatch(pair -> pair.getSecond().getCid() == channelToViewId);
            req.setAttribute("friendChannel", friendChannel);
            boolean showChannel = subscribedChannels.stream().anyMatch(channel -> channel.getCid() == channelToViewId);
            req.setAttribute("showChannel", showChannel || friendChannel);
            if (showChannel || friendChannel) {
                req.setAttribute("channelToViewId", channelToViewId);
                if (!friendChannel) {
                    List<User> listAdmins = adminDAO.getAdmins(channelToViewId);
                    Boolean isAdmin = listAdmins.stream().anyMatch(user -> user.getUid() == uid);
                    req.setAttribute("listAdmins", listAdmins);
                    req.setAttribute("isAdmin", isAdmin);
                }
                int editMid = AbstractController.getEscapedParameter(req, "editMid") == null ? -1 : Integer.parseInt(AbstractController.getEscapedParameter(req, "editMid"));
                String sendError = req.getAttribute("senderror") == null ? "" : req.getAttribute("senderror").toString();
                Channel channel = channelDAO.getChannelById(channelToViewId);
                List<Message> messages = messageDAO.getMessageByChannelId(channelToViewId);

                req.setAttribute("editMid", editMid);
                req.setAttribute("sendError", sendError);
                req.setAttribute("channel", channel);
                req.setAttribute("messages", messages);
                if (!messages.isEmpty()) {
                    req.setAttribute("userDAO", userDAO);
                    req.setAttribute("reactionDAO", reactionDAO);
                }

                if (friendChannel) {
                    User friend = friendDAO.getFriendForChannel(channelToViewId, uid);
                    req.setAttribute("friend", friend);
                }


            }
        }
    },
    LOGIN("login.jsp"),
    EDIT_USER("editUser.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException {
            req.setAttribute("referer", "home");
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            User user = userDAO.getUserById(uid);
            req.setAttribute("user", user);
        }
    },
    FRIEND("friend.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException {
            req.setAttribute("referer", "home");
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            List<User> notFriends = friendDAO.getNotFriends(uid);
            Map<Integer, String> base64ProfilePictures = userDAO.getUserProfilePictures(notFriends.stream().map(User::getUid).toList());
            req.setAttribute("notFriends", notFriends);
            req.setAttribute("base64ProfilePictures", base64ProfilePictures);
        }
    },
    EDIT_CHANNEL("ModifChannel.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException, IOException {
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            String channelID = AbstractController.getEscapedParameter(req, "channelID");
            req.setAttribute("referer", "home?action=view&channelID=" + channelID);
            if (channelID == null) {
                res.sendRedirect("home");
                return;
            }

            Channel channel = channelDAO.getChannelById(Integer.parseInt(channelID));
            List<User> admins = adminDAO.getAdmins(channel.getCid());
            List<User> users = userDAO.getAllUsers();
            List<User> subscribers = subscriptionDAO.getSubscribedUsers(channel.getCid());
            req.setAttribute("channel", channel);
            req.setAttribute("admins", admins);
            req.setAttribute("users", users);
            req.setAttribute("subscribers", subscribers);
        }
    },
    CREATE_CHANNEL("createChannel.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException {
            req.setAttribute("referer", "home");
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            List<User> users = userDAO.getAllUsers();
            req.setAttribute("users", users);
        }
    },
    SHARE_CHANNEL("share.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException {
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            int chanelID = Integer.parseInt(AbstractController.getEscapedParameter(req, "channelID"));
            Channel channel = channelDAO.getChannelById(chanelID);
            req.setAttribute("referer", "home?action=view&channelID=" + chanelID);
            String token = new JwtManager().createJwtForChannelLink(uid, chanelID);
            String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/channel?action=join&token=" + token;
            req.setAttribute("channel", channel);
            req.setAttribute("url", url);
        }
    },
    JOIN("join.jsp") {
        @Override
        public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException {
            req.setAttribute("referer", "home");
            Integer uid = (Integer) req.getAttribute("id");
            String userProfilePicture = this.userDAO.getUserProfilePicture(Util.getUid(req.getSession()));
            User currentUser = userDAO.getUserById(uid);
            req.setAttribute("UserProfilePicture", userProfilePicture);
            req.setAttribute("currentUser", currentUser);

            String token = AbstractController.getEscapedParameter(req, "token");
            if (token == null) {
                AbstractController.handleError(new IllegalArgumentException("The token is missing"), req, res);
                return;
            }
            Pair<Integer, Integer> pair;
            try {
                pair = new JwtManager().getUidAndCidFromChannelInviteToken(token)   ;
            } catch (JwtException e) {
                AbstractController.handleError(e, req, res);
                return;
            }
            if (pair == null) {
                AbstractController.handleError(new IllegalArgumentException("The token is invalid"), req, res);
                return;
            }

            int userID = pair.getFirst();
            int channelID = pair.getSecond();

            User user = userDAO.getUserById(userID);
            Channel channel = channelDAO.getChannelById(channelID);
            req.setAttribute("token", token);
            req.setAttribute("user", user);
            req.setAttribute("channel", channel);

        }
    },
    ERROR("error.jsp");

    protected final UserDAO userDAO;
    protected final ChannelDAO channelDAO;
    protected final SubscriptionDAO subscriptionDAO;
    protected final AdminsDAO adminDAO;
    protected final MessageDAO messageDAO;
    protected final ReactionDAO reactionDAO;
    protected final FriendDAO friendDAO;
    private final String jsp;


    JSP(String jspPath) {
        this.jsp = jspPath;
        try {
            this.userDAO = Config.getConfig().getUserDAO();
            this.channelDAO = Config.getConfig().getChannelDAO();
            this.subscriptionDAO = Config.getConfig().getSubscriptionDAO();
            this.adminDAO = Config.getConfig().getAdminsDAO();
            this.messageDAO = Config.getConfig().getMessageDAO();
            this.reactionDAO = Config.getConfig().getReactionDAO();
            this.friendDAO = Config.getConfig().getFriendDAO();
        } catch (ConfigErrorException e) {
            throw new RuntimeException("Failed to initialize DAOs", e);
        }
    }

    public static String getJSPPath(String jsp) {
        return "/WEB-INF/jsp/" + jsp;
    }

    public void prepare(HttpServletRequest req, HttpServletResponse res) throws MyDiscordException, IOException {
    }

    public void launch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        req.setAttribute("referer", req.getHeader("referer"));
        prepare(req, res);
        RequestDispatcher dispatcher = req.getRequestDispatcher(getJSPPath(jsp));
        dispatcher.forward(req, res);
    }
}

