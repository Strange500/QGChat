package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.subscription.SubscriptionNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SubscriptionDAO {

    List<User> getSubscribedUsers(int cid) throws ChannelNotFoundException, DataAccessException;

    void clearSubscriptions(int cid) throws ChannelNotFoundException, DataAccessException;

    void unsubscribeUser(int uid, int cid) throws UserNotFoundException, ChannelNotFoundException, SubscriptionNotFoundException, DataAccessException;

    boolean isSubscribedTo(int uid, int cid) throws UserNotFoundException, ChannelNotFoundException, DataAccessException;

    void subscribeUsersTo(Channel ch, List<Integer> Uids) throws ChannelNotFoundException, UserNotFoundException, DataAccessException;


}
