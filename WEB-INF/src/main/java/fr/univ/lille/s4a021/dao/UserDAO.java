package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.user.UserCreationException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserUpdateException;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

public interface UserDAO {

    public int createUser(String username, String mail, String password) throws UserCreationException, DataAccessException;

    public boolean authenticateUser(String mail, String password) throws DataAccessException;

    public User getUserByMail(String mail) throws UserNotFoundException, DataAccessException;

    public User getUserById(int uid) throws UserNotFoundException, DataAccessException;

    public List<User> getUserByIds(Collection<Integer> uids) throws UserNotFoundException, DataAccessException;

    public void deleteUser(int uid) throws UserNotFoundException, DataAccessException;

    public void updateUser(int uid, String newUsername, String newMail) throws UserNotFoundException, UserUpdateException, DataAccessException;

    public List<User> getAllUsers() throws DataAccessException;

    public void setUserProfilePicture(String base64Image, int uid) throws UserNotFoundException, UserUpdateException, DataAccessException;

    public String getUserProfilePicture(int uid) throws UserNotFoundException, DataAccessException;

    public boolean userExists(int uid) throws DataAccessException;

    public boolean userAllExists(Collection<Integer> uids) throws DataAccessException;
}
