package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.MyDiscordException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserCreationException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserUpdateException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@MultipartConfig
@WebServlet("/user")
public class UserController extends AbstractController {

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB
    List<String> defaultProfilePics = Arrays.asList("default1.png", "default2.png", "default3.png", "default4.png");

    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_ADD_FRIEND = "addFriend";
    private static final String ACTION_REMOVE_FRIEND = "removeFriend";
    private static final String ACTION_SEND_FRIEND_REQUEST = "sendFriendRequest";
    private static final String ACTION_ACCEPT_FRIEND_REQUEST = "acceptFriendRequest";
    private static final String ACTION_DECLINE_FRIEND_REQUEST = "declineFriendRequest";
    private static final String ACTION_CANCEL_FRIEND_REQUEST = "cancelFriendRequest";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_SET_PROFILE_PIC = "setprofilepic";

    private static final String ACTION_AUTH = "auth";
    private static final String ACTION_REGISTER = "register";

    private String getDefaultProfilePic() throws IOException {
        try (InputStream st = getClass().getClassLoader().getResourceAsStream(defaultProfilePics.get((int) (Math.random() * defaultProfilePics.size())))) {
            if (st == null) {
                throw new IOException("Failed to load default profile picture");
            }
            return Base64.getEncoder().encodeToString(st.readAllBytes());
        } catch (IOException e) {
            throw new IOException("Failed to load default profile picture", e);
        }
    }

    @Override
    protected void processNoAuthAction(String action, HttpServletRequest req, HttpServletResponse res)
            throws IOException, MyDiscordException, ServletException {

        if (ACTION_AUTH.equalsIgnoreCase(action)) {
            if (authenticateUser(req)) {
                res.sendRedirect("home");
                return;
            }else {
                req.setAttribute("loginError", "Invalid credential");
                forwardToJSP(req, res, JSP.LOGIN);
                return;
            }
        }

        if (ACTION_REGISTER.equalsIgnoreCase(action)) {
            try {
                handleUserRegistration(req, res);
            }catch (UserCreationException e) {
                req.setAttribute("registerError", e.getMessage());
                forwardToJSP(req, res, JSP.LOGIN);
            }
        }
    }

    private void handleUserRegistration(HttpServletRequest req, HttpServletResponse res) throws IOException, UserCreationException, DataAccessException, UserNotFoundException, UserUpdateException {
        String username = this.getEscapedParameter(req, "username");
        String mail = this.getEscapedParameter(req, "mail");
        String password = req.getParameter("password");
        if (this.parameterContainsUnauthorizedChars(req, "username") || this.parameterContainsUnauthorizedChars(req, "mail")) {
            throw new UserCreationException("Invalid characters in username or mail");
        }
        if (password == null || password.isEmpty()) {
            throw new UserCreationException("Password cannot be empty");
        }
        if (mail == null || mail.isEmpty()) {
            throw new UserCreationException("Mail cannot be empty");
        }
        if (username == null || username.isEmpty()) {
            throw new UserCreationException("Username cannot be empty");
        }
        int uid = userDAO.createUser(username, mail, password);
        userDAO.setUserProfilePicture(getDefaultProfilePic(), uid);
        res.sendRedirect("home");

    }

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, MyDiscordException {
        int uid = Util.getUid(req.getSession());
        if (action == null || action.isEmpty()) {
            res.sendRedirect("home");
            return;
        }
        switch (action) {
            case ACTION_EDIT:
                forwardToJSP(req, res, JSP.EDIT_USER);
                break;

            case ACTION_ADD_FRIEND:
                forwardToJSP(req, res, JSP.FRIEND);
                break;

            case ACTION_SEND_FRIEND_REQUEST:
                handleSendFriendRequest(uid, req, res);
                break;

            case ACTION_ACCEPT_FRIEND_REQUEST:
                handleAcceptFriendRequest(uid, req, res);
                break;

            case ACTION_DECLINE_FRIEND_REQUEST:
                handleDeclineFriendRequest(uid, req, res);
                break;

            case ACTION_DELETE:
                handleDeleteUser(uid, req, res);
                break;

            case ACTION_UPDATE:
                try {
                    handleUpdateUser(uid, req, res);
                } catch (BadParameterException e) {
                    req.setAttribute("editException", e.getMessage());
                    forwardToJSP(req, res, JSP.EDIT_USER);
                }

                break;

            case ACTION_SET_PROFILE_PIC:
                try {
                    handleSetProfilePicture(uid, req, res);
                } catch (BadParameterException e) {
                    req.setAttribute("editException", e.getMessage());
                    forwardToJSP(req, res, JSP.EDIT_USER);
                }
                break;
            case ACTION_CANCEL_FRIEND_REQUEST:
                handleCancelFriendRequest(uid, req, res);
                break;
            case ACTION_REMOVE_FRIEND:
                handleRemoveFriend(uid, req, res);
                break;
            default:
                res.sendRedirect("home");
                break;
        }
    }

    private void handleRemoveFriend(int uid, HttpServletRequest req, HttpServletResponse res) throws BadParameterException, UserNotFoundException, DataAccessException, IOException {
        int friendId = parseUidParameter(req);
        friendDAO.removeFriend(uid, friendId);
        res.sendRedirect("home");
    }

    private void handleCancelFriendRequest(int uid, HttpServletRequest req, HttpServletResponse res) throws BadParameterException, UserNotFoundException, DataAccessException, IOException {
        int receiveruid = parseUidParameter(req);
        friendDAO.declineFriendRequest(uid, receiveruid);
        res.sendRedirect("home");
    }

    private void handleDeclineFriendRequest(int uid, HttpServletRequest req, HttpServletResponse res) throws UserNotFoundException, DataAccessException, IOException, BadParameterException {
        int senderuid = parseUidParameter(req);
        friendDAO.declineFriendRequest(senderuid, uid);
        res.sendRedirect("home");
    }

    private void handleAcceptFriendRequest(int uid, HttpServletRequest req, HttpServletResponse res) throws UserNotFoundException, DataAccessException, IOException, BadParameterException, ChannelNotFoundException {
        int senderuid = parseUidParameter(req);
        friendDAO.acceptFriendRequest(senderuid, uid);
        Channel ch = friendDAO.getFriendChannel(uid, senderuid);
        res.sendRedirect("home?action=viewFriend&channelID=" + ch.getCid());
    }

    private void handleDeleteUser(int uid, HttpServletRequest req, HttpServletResponse res) throws IOException, BadParameterException, UnauthorizedException, UserNotFoundException, DataAccessException {
        int paramUid = parseUidParameter(req);
        if (uid != paramUid) {
            throw new UnauthorizedException("Unauthorized");
        }
        userDAO.deleteUser(uid);
        res.sendRedirect("home");

    }

    private void handleUpdateUser(int uid, HttpServletRequest req, HttpServletResponse res) throws IOException, BadParameterException, UserNotFoundException, UserUpdateException, DataAccessException, UnauthorizedException {
        int uidToUpdate = parseUidParameter(req);
        if (uidToUpdate != uid) {
            throw new UnauthorizedException("Unauthorized");
        }

        String username = this.getEscapedParameter(req, "username");
        String email = this.getEscapedParameter(req, "email");
        if (this.parameterContainsUnauthorizedChars(req, "username") || this.parameterContainsUnauthorizedChars(req, "email")) {
            throw new BadParameterException("Invalid characters in username or email");
        }
        if (username == null || username.isEmpty()) {
            throw new BadParameterException("Username cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new BadParameterException("Email cannot be empty");
        }
        userDAO.updateUser(uidToUpdate, username, email);
        res.sendRedirect("home");

    }

    private void handleSetProfilePicture(int uid, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, BadParameterException, UserNotFoundException, UserUpdateException, DataAccessException {
        int uidToSetProfilePic = parseUidParameter(req);
        if (uidToSetProfilePic != uid) {
            handleError(new UnauthorizedException("Unauthorized"), req, res);
            return;
        }

        Part imgPart = req.getPart("profilepic");
        validateProfilePicture(imgPart);

        String base64Img = Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());
        userDAO.setUserProfilePicture(base64Img, uidToSetProfilePic);
        res.sendRedirect("home");

    }

    private void handleSendFriendRequest(int uid, HttpServletRequest req, HttpServletResponse res) throws IOException, BadParameterException, UserNotFoundException, DataAccessException {
        int friendId = parseUidParameter(req);
        friendDAO.sendFriendRequest(uid, friendId);
        res.sendRedirect(req.getHeader("Referer"));
    }

    private void validateProfilePicture(Part imgPart) throws BadParameterException {
        if (imgPart.getSize() == 0) {
            throw new BadParameterException("No image provided");
        }

        if (imgPart.getSize() > MAX_FILE_SIZE) {
            throw new BadParameterException("Image too big");
        }
    }

    private boolean authenticateUser(HttpServletRequest req) throws DataAccessException, UserNotFoundException {
        String mail = this.getEscapedParameter(req, "mail");
        String password = this.getEscapedParameter(req, "password");

        if (mail == null || password == null) {
            return false;
        }

        if (userDAO.authenticateUser(mail, password)) {
            User usr = userDAO.getUserByMail(mail);
            HttpSession session = req.getSession();
            session.setAttribute("id", usr.getUid());
            session.setMaxInactiveInterval(60 * 60); // 1 hour
            return true;
        } else {
            return false;
        }
    }

    private int parseUidParameter(HttpServletRequest req) throws BadParameterException {
        String uidParam = this.getEscapedParameter(req, "uid");
        if (uidParam == null) {
            throw new BadParameterException("UID cannot be null");
        }
        try {
            return Integer.parseInt(uidParam);
        } catch (NumberFormatException e) {
            throw new BadParameterException("Invalid UID format");
        }
    }
}