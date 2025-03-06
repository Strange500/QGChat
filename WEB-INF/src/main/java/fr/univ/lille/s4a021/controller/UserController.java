package fr.univ.lille.s4a021.controller;

import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.model.bdd.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
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


        if (action == null) {
            res.sendRedirect("home");
            return;
        }

        try {
            if (!Util.userIsConnected(session)) {
                res.sendRedirect("home");
                return;
            }
        } catch (SQLException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
        }

        int currentUid = Util.getUid(session);

        UserDAO userDAO = null;
        try {
            userDAO = new UserDAO();
        } catch (SQLException e) {
            MainController.sendErrorPage(500, e.getMessage(), req, res);
        }

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
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
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
                } catch (SQLException e) {
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
                } catch (SQLException e) {
                    MainController.sendErrorPage(500, e.getMessage(), req, res);
                    return;
                }

                res.sendRedirect("home");
                break;


            default:
                res.sendRedirect("home");

        }


    }
}
