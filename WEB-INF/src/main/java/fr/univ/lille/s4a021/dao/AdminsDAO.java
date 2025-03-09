package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminCreationException;
import fr.univ.lille.s4a021.exception.dao.admin.AdminNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.util.List;

public interface AdminsDAO {

    public void setAdmin(int cid, int uid) throws UserNotFoundException, ChannelNotFoundException, AdminCreationException, DataAccessException;

    public void clearAdmins(int cid) throws ChannelNotFoundException, DataAccessException;

    public void setAdmins(int cid, List<Integer> users) throws UserNotFoundException, ChannelNotFoundException, AdminCreationException, DataAccessException;

    public boolean userIsAdmin(int uid, int cid) throws UserNotFoundException, ChannelNotFoundException, DataAccessException;

    public List<User> getAdmins(int cid) throws ChannelNotFoundException, DataAccessException;

    public void removeAdmin(int uid, int cid) throws AdminNotFoundException, ChannelNotFoundException, DataAccessException;

}
