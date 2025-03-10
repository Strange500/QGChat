package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserUpdateException;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Base64;

import static fr.univ.lille.s4a021.controller.MainController.getJSPPath;

@MultipartConfig
@WebServlet("/user")
public class UserController extends HttpServlet {

    private static final int MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB

    public static final String EDIT = "editUser.jsp";
    public static final String FRIEND = "friend.jsp";



    public void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        UserDAO userDAO = null;
        try {
            userDAO = Config.getConfig().getUserDAO();
        } catch (ConfigErrorException e) {
            e.printStackTrace();
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }

        if (action == null) {
            res.sendRedirect("home");
            return;
        }

        if (action.equals("auth")) {
            try {
                if (auth(req.getParameter("mail"), req.getParameter("password"), userDAO, session)) {
                    res.sendRedirect("home");
                } else {
                       RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(MainController.LOGIN));
                       req.setAttribute("error", "Invalid credentials");
                       rd.forward(req, res);
                }
            } catch (DataAccessException e) {
                e.printStackTrace();
                MainController.sendErrorPage(500, e.getMessage(), req, res);
                return;
            } catch (UserNotFoundException e) {
                MainController.sendErrorPage(404, e.getMessage(), req, res);
                return;
            }
            return;
        }

        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (ConfigErrorException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
        }

        int currentUid = Util.getUid(session);

        try {

            switch (action) {
                case "edit":
                    RequestDispatcher rd = req.getRequestDispatcher(getJSPPath(EDIT));
                    rd.forward(req, res);
                    break;

                case "friend":
                    rd = req.getRequestDispatcher(getJSPPath(FRIEND));
                    rd.forward(req, res);
                    break;

                case "delete":
                    int uid = Integer.parseInt(req.getParameter("uid"));
                    if (uid != currentUid) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    try {
                        userDAO.deleteUser(uid);
                    }  catch (UserNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    }

                    res.sendRedirect("home");
                    break;
                case "update":
                    int uidToUpdate = Integer.parseInt(req.getParameter("uid"));
                    if (uidToUpdate != currentUid) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    String username = req.getParameter("username");
                    String email = req.getParameter("email");

                    try {
                        userDAO.updateUser(uidToUpdate, username, email);
                    } catch (UserNotFoundException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    } catch (UserUpdateException e) {
                        MainController.sendErrorPage(500, e.getMessage(), req, res);
                        return;
                    }

                    res.sendRedirect("home");

                    break;
                case "setprofilepic":
                    int uidToSetProfilePic = Integer.parseInt(req.getParameter("uid"));
                    if (uidToSetProfilePic != currentUid) {
                        MainController.sendErrorPage(401, "Unauthorized", req, res);
                        return;
                    }

                    Part imgPart = req.getPart("profilepic");
                    if (imgPart.getSize() == 0) {
                        MainController.sendErrorPage(400, "Bad request: no image", req, res);
                        return;
                    }

                    if (imgPart.getSize() > MAX_FILE_SIZE) {
                        MainController.sendErrorPage(400, "Bad request: image too big", req, res);
                        return;
                    }

                    String base64Img = Base64.getEncoder().encodeToString(imgPart.getInputStream().readAllBytes());

                    try {
                        userDAO.setUserProfilePicture(base64Img, uidToSetProfilePic);
                    } catch (UserNotFoundException | UserUpdateException e) {
                        MainController.sendErrorPage(404, e.getMessage(), req, res);
                        return;
                    }

                    res.sendRedirect("home");
                    break;


                default:
                    res.sendRedirect("home");

            }
        } catch (DataAccessException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
            return;
        }


    }

    private static boolean auth(String mail, String password, UserDAO userDAO, HttpSession session) throws DataAccessException, UserNotFoundException {
        if (mail == null || password == null) {
            return false;
        }

        if (userDAO.authenticateUser(mail, password)) {
            User usr = userDAO.getUserByMail(mail);
            session.setAttribute("id", usr.getUid());
            session.setMaxInactiveInterval(60 * 60);
            return true;
        } else {
            return false;
        }

    }


}
