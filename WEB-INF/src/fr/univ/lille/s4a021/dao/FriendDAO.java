package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException;
import fr.univ.lille.s4a021.util.Pair;

import java.util.List;

public interface FriendDAO {

    public void addFriend(int uid, int friendId) throws UserNotFoundException, DataAccessException;

    public boolean isFriend(int uid, int friendId) throws UserNotFoundException, DataAccessException;

    List<Pair<User, Channel>> getFriendChannels(int uid) throws UserNotFoundException, DataAccessException;

    public List<User> getFriendRequests(int uid) throws UserNotFoundException, DataAccessException;

    public void acceptFriendRequest(int senderuid, int receiveruid) throws UserNotFoundException, DataAccessException;

    public void declineFriendRequest(int senderuid, int receiveruid) throws UserNotFoundException, DataAccessException;

    public void sendFriendRequest(int senderuid, int receiveruid) throws UserNotFoundException, DataAccessException;

    public List<User> getNotFriends(int uid) throws UserNotFoundException, DataAccessException;

    public boolean isFriendChannel(int uid, int channelId) throws UserNotFoundException, DataAccessException;

    public User getFriendForChannel(int channelId, int myUid) throws ChannelNotFoundException, DataAccessException;
}
