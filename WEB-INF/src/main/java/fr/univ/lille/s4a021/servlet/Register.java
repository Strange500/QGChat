package fr.univ.lille.s4a021.servlet;

import fr.univ.lille.s4a021.Config;
import fr.univ.lille.s4a021.controller.MainController;
import fr.univ.lille.s4a021.dao.UserDAO;
import fr.univ.lille.s4a021.dao.impl.UserDAOSql;
import fr.univ.lille.s4a021.exception.ConfigErrorException;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.user.UserCreationException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserUpdateException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.jakartaee.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@WebServlet("/register")
public class Register extends HttpServlet {

    List<String> defaultProfilePics = Arrays.asList("default1.png", "default2.png", "default3.png", "default4.png");

    private String getDefaultProfilePic() throws IOException {
        return Base64.getEncoder().encodeToString(getClass().getClassLoader().getResourceAsStream(defaultProfilePics.get((int) (Math.random() * defaultProfilePics.size()))).readAllBytes());

    }

    public void doPost( HttpServletRequest req, HttpServletResponse res )
            throws ServletException, IOException
    {
        try {
            UserDAO userDAO = null;
            try {
                userDAO = Config.getConfig().getUserDAO();
            } catch (ConfigErrorException e) {
                MainController.handleError(e, req, res);
                return;
            }
            String username = StringEscapeUtils.escapeHtml4(req.getParameter("username"));
            String mail = StringEscapeUtils.escapeHtml4(req.getParameter("mail"));
            String password = req.getParameter("password");
            int uid = userDAO.createUser(username, mail, password);
            userDAO.setUserProfilePicture(getDefaultProfilePic(), uid);
            res.sendRedirect("home");
        } catch (UserNotFoundException | UserUpdateException | DataAccessException | UserCreationException e) {
            MainController.handleError(e, req, res);
        }
    }


}