package fr.univ.lille.s4a021.dao;

import fr.univ.lille.s4a021.dto.Channel;
import fr.univ.lille.s4a021.dto.User;
import fr.univ.lille.s4a021.exception.dao.DataAccessException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelCreationException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException;
import fr.univ.lille.s4a021.exception.dao.channel.ChannelUpdateException;

import java.util.List;

public interface ChannelDAO {


    Channel createChannel(String name) throws ChannelCreationException, DataAccessException;

    Channel getChannelByName(String name) throws ChannelNotFoundException, DataAccessException;

    Channel getChannelById(int cid) throws ChannelNotFoundException, DataAccessException;

    void deleteChannel(int cid) throws ChannelNotFoundException, DataAccessException;

    void updateChannel(int cid, String newName) throws ChannelNotFoundException, ChannelUpdateException, DataAccessException;

    List<Channel> getAllChannels() throws DataAccessException;

    boolean channelExists(int cid) throws DataAccessException;

}
