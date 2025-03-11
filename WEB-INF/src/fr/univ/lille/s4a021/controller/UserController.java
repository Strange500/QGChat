package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.BadParameterException;
import fr.univ.lille.s4a021.exception.UnauthorizedException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
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
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@MultipartConfig
@WebServlet("/user")
public class UserController extends AbstractController {

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB
    private static final String EDIT_JSP = "editUser.jsp";
    private static final String FRIEND_JSP = "friend.jsp";
    List<String> defaultProfilePics = Arrays.asList("default1.png", "default2.png", "default3.png", "default4.png");

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
            throws ServletException, IOException, UserNotFoundException, DataAccessException, UserCreationException, UserUpdateException {

        if ("auth".equalsIgnoreCase(action)) {
            if (authenticateUser(req)) {
                res.sendRedirect("home");
                return;
            }
        }

        if ("register".equalsIgnoreCase(action)) {
            handleUserRegistration(req, res);
        }
    }

    private void handleUserRegistration(HttpServletRequest req, HttpServletResponse res) throws IOException, UserCreationException, DataAccessException, UserNotFoundException, UserUpdateException {
        String username = StringEscapeUtils.escapeHtml4(req.getParameter("username"));
        String mail = StringEscapeUtils.escapeHtml4(req.getParameter("mail"));
        String password = req.getParameter("password");

        int uid = userDAO.createUser(username, mail, password);
        userDAO.setUserProfilePicture(getDefaultProfilePic(), uid);
        res.sendRedirect("home");

    }

    @Override
    protected void processAction(String action, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, BadParameterException, UserNotFoundException, UserUpdateException, DataAccessException, UnauthorizedException {
        int uid = Util.getUid(req.getSession());

        switch (action) {
            case "edit":
                forwardToJSP(req, res, EDIT_JSP);
                break;

            case "friend":
                forwardToJSP(req, res, FRIEND_JSP);
                break;

            case "delete":
                handleDeleteUser(uid, req, res);
                break;

            case "update":
                handleUpdateUser(uid, req, res);
                break;

            case "setprofilepic":
                handleSetProfilePicture(uid, req, res);
                break;

            default:
                res.sendRedirect("home");
                break;
        }
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

        String username = req.getParameter("username");
        String email = req.getParameter("email");

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

    private void validateProfilePicture(Part imgPart) throws BadParameterException {
        if (imgPart.getSize() == 0) {
            throw new BadParameterException("No image provided");
        }

        if (imgPart.getSize() > MAX_FILE_SIZE) {
            throw new BadParameterException("Image too big");
        }
    }

    private boolean authenticateUser(HttpServletRequest req) throws DataAccessException, UserNotFoundException {
        String mail = req.getParameter("mail");
        String password = req.getParameter("password");

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
        String uidParam = req.getParameter("uid");
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