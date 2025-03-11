package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.user.UserCreationException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserUpdateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserDAO {

    int createUser(String username, String mail, String password) throws UserCreationException, DataAccessException;

    boolean authenticateUser(String mail, String password) throws DataAccessException;

    User getUserByMail(String mail) throws UserNotFoundException, DataAccessException;

    User getUserById(int uid) throws UserNotFoundException, DataAccessException;

    List<User> getUserByIds(Collection<Integer> uids) throws UserNotFoundException, DataAccessException;

    void deleteUser(int uid) throws UserNotFoundException, DataAccessException;

    void updateUser(int uid, String newUsername, String newMail) throws UserNotFoundException, UserUpdateException, DataAccessException;

    List<User> getAllUsers() throws DataAccessException;

    void setUserProfilePicture(String base64Image, int uid) throws UserNotFoundException, UserUpdateException, DataAccessException;

    String getUserProfilePicture(int uid) throws UserNotFoundException, DataAccessException;

    Map<Integer, String> getUserProfilePictures(Collection<Integer> uids) throws UserNotFoundException, DataAccessException;

    boolean userExists(int uid) throws DataAccessException;

    boolean userAllExists(Collection<Integer> uids) throws DataAccessException;
}
